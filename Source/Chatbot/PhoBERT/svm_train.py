import torch
from transformers import AutoModel, AutoTokenizer
import numpy as np
from sklearn.model_selection import StratifiedKFold
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import MinMaxScaler, LabelEncoder
from sklearn.svm import SVC
from sklearn.model_selection import GridSearchCV
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, confusion_matrix, classification_report
from imblearn.over_sampling import SMOTE
import pandas as pd
import logging
import warnings
import os
import json
import h5py
import sys
import io

# Đảm bảo đầu ra tiêu chuẩn (stdout) sử dụng UTF-8
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# Cấu hình logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('training.log', encoding='utf-8'),
        logging.StreamHandler(sys.stdout)
    ]
)

# Bỏ qua warnings
warnings.filterwarnings('ignore')

# Kiểm tra và sử dụng GPU nếu có
device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
logging.info(f"Using device: {device}")

# Load PhoBERT model và tokenizer
model_name = "vinai/phobert-base-v2"
phobert = AutoModel.from_pretrained(model_name)
tokenizer = AutoTokenizer.from_pretrained(model_name)

# Chuyển model sang device
phobert = phobert.to(device)

def load_json_data(file_path):
    """
    Load data từ file JSON
    """
    try:
        with open(file_path, 'r', encoding='utf-8') as file:
            data = json.load(file)
        
        questions = []
        labels = []
        responses = []  # Thêm list để lưu responses
        
        for intent in data['intents']:
            tag = intent['tag']
            patterns = intent['patterns']
            intent_responses = intent['responses']  # Lấy responses cho intent
            
            # Thêm các patterns và tag tương ứng
            questions.extend(patterns)
            labels.extend([tag] * len(patterns))
            responses.extend([intent_responses] * len(patterns))  # Thêm responses tương ứng
        
        return questions, labels, responses
    except Exception as e:
        logging.error(f"Error loading JSON data: {str(e)}")
        raise

def get_phobert_embeddings_batch(texts, batch_size=32):
    """
    Tính toán embeddings cho các văn bản sử dụng PhoBERT với xử lý batch
    """
    embeddings = []
    for i in range(0, len(texts), batch_size):
        batch_texts = texts[i:i + batch_size]
        tokens = tokenizer(batch_texts, 
                         return_tensors='pt', 
                         padding=True, 
                         truncation=True, 
                         max_length=128,
                         return_attention_mask=True)
        
        tokens = {k: v.to(device) for k, v in tokens.items()}
        
        with torch.no_grad():
            outputs = phobert(**tokens)
            
        # Sử dụng attention mask để lấy mean có trọng số
        attention_mask = tokens['attention_mask'].unsqueeze(-1)
        hidden_states = outputs.last_hidden_state
        masked_embeddings = hidden_states * attention_mask
        sum_embeddings = torch.sum(masked_embeddings, dim=1)
        sum_mask = torch.clamp(torch.sum(attention_mask, dim=1), min=1e-9)
        batch_embeddings = sum_embeddings / sum_mask
        
        embeddings.append(batch_embeddings.cpu().numpy())
    
    return np.vstack(embeddings)

def save_model_to_h5(model, label_encoder, embeddings, labels, responses, file_path):
    """
    Lưu model, label encoder và dữ liệu training vào file h5
    """
    try:
        with h5py.File(file_path, 'w') as f:
            # Lưu model parameters
            model_params = model.get_params()
            model_params_group = f.create_group('model_params')
            for key, value in model_params.items():
                if isinstance(value, (int, float, str)):
                    model_params_group.attrs[key] = str(value)

            # Lưu support vectors
            svm_model = model.named_steps['svm']
            f.create_dataset('support_vectors', data=svm_model.support_vectors_)
            f.create_dataset('dual_coef', data=svm_model.dual_coef_)
            f.create_dataset('intercept', data=svm_model.intercept_)

            # Lưu label encoder classes
            label_classes = [str(cls).encode('utf-8') for cls in label_encoder.classes_]
            f.create_dataset('label_encoder_classes', data=label_classes)

            # Lưu embeddings và labels training
            f.create_dataset('embeddings', data=embeddings)
            f.create_dataset('labels', data=labels)

            # Lưu responses
            responses_encoded = [str(resp).encode('utf-8') for resp in responses]
            f.create_dataset('responses', data=responses_encoded)

            # Lưu thông tin bổ sung
            f.attrs['n_features'] = embeddings.shape[1]
            f.attrs['n_classes'] = len(label_encoder.classes_)

    except Exception as e:
        logging.error(f"Error saving model to h5: {str(e)}")
        raise

def train_evaluate_model():
    """
    Huấn luyện và đánh giá model
    """
    # Load data
    json_path = "intents.json"  # Đường dẫn đến file intents.json của bạn
    questions, labels, responses = load_json_data(json_path)
    
    # Encode labels
    label_encoder = LabelEncoder()
    y_labels = label_encoder.fit_transform(labels)
    
    # Tính embeddings cho toàn bộ dataset
    logging.info("Calculating embeddings...")
    X_embeddings = get_phobert_embeddings_batch(questions)
    
    # Định nghĩa pipeline
    pipeline = Pipeline([
        ('scaler', MinMaxScaler()),
        ('svm', SVC(probability=True, class_weight='balanced'))
    ])
    
    # Định nghĩa param_grid
    param_grid = {
        'svm__C': [0.01, 0.1, 1, 10],
        'svm__kernel': ['linear', 'rbf'],
        'svm__gamma': ['scale', 'auto']
    }
    
    # Khởi tạo StratifiedKFold
    skf = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
    best_scores = []
    
    # Training và evaluation
    for fold, (train_index, test_index) in enumerate(skf.split(X_embeddings, y_labels)):
        logging.info(f"\nTraining Fold {fold+1}...")
        
        X_train, X_test = X_embeddings[train_index], X_embeddings[test_index]
        y_train, y_test = y_labels[train_index], y_labels[test_index]
        
        # Áp dụng SMOTE
        smote = SMOTE(random_state=42)
        X_train_balanced, y_train_balanced = smote.fit_resample(X_train, y_train)
        
        # GridSearchCV
        grid_search = GridSearchCV(
            pipeline,
            param_grid,
            cv=3,
            scoring='f1_weighted',
            n_jobs=-1
        )
        
        grid_search.fit(X_train_balanced, y_train_balanced)
        
        # Dự đoán và đánh giá
        y_pred = grid_search.predict(X_test)
        
        # In kết quả chi tiết
        logging.info(f"\nFold {fold+1}:")
        logging.info(f"Best parameters: {grid_search.best_params_}")
        logging.info("\nClassification Report:")
        report = classification_report(y_test, y_pred, 
                                       target_names=label_encoder.classes_,
                                       zero_division=0)
        logging.info(report)
        
        # Tính các metrics
        accuracy = accuracy_score(y_test, y_pred)
        precision = precision_score(y_test, y_pred, average='weighted', zero_division=0)
        recall = recall_score(y_test, y_pred, average='weighted', zero_division=0)
        f1 = f1_score(y_test, y_pred, average='weighted', zero_division=0)
        
        # Lưu kết quả
        best_scores.append({
            'accuracy': accuracy,
            'precision': precision,
            'recall': recall,
            'f1': f1,
            'params': grid_search.best_params_,
            'model': grid_search.best_estimator_
        })
        
        # Log metrics
        logging.info(f"\nMetrics for Fold {fold+1}:")
        logging.info(f"Accuracy: {accuracy:.4f}")
        logging.info(f"Precision: {precision:.4f}")
        logging.info(f"Recall: {recall:.4f}")
        logging.info(f"F1 Score: {f1:.4f}")
    
    # Tìm model tốt nhất
    best_fold = max(range(len(best_scores)), key=lambda i: best_scores[i]['f1'])
    best_model = best_scores[best_fold]
    
    # In kết quả tổng hợp
    logging.info("\nBest Model Performance (From Fold {}):".format(best_fold + 1))
    logging.info(f"Accuracy: {best_model['accuracy']:.4f}")
    logging.info(f"Precision: {best_model['precision']:.4f}")
    logging.info(f"Recall: {best_model['recall']:.4f}")
    logging.info(f"F1 Score: {best_model['f1']:.4f}")
    logging.info(f"Best Parameters: {best_model['params']}")
    
    # Tính mean scores
    mean_accuracy = np.mean([score['accuracy'] for score in best_scores])
    mean_precision = np.mean([score['precision'] for score in best_scores])
    mean_recall = np.mean([score['recall'] for score in best_scores])
    mean_f1 = np.mean([score['f1'] for score in best_scores])
    
    logging.info("\nMean scores across all folds:")
    logging.info(f"Mean Accuracy: {mean_accuracy:.4f}")
    logging.info(f"Mean Precision: {mean_precision:.4f}")
    logging.info(f"Mean Recall: {mean_recall:.4f}")
    logging.info(f"Mean F1 Score: {mean_f1:.4f}")
    
    # Tạo thư mục output nếu chưa tồn tại
    output_dir = 'models'
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
    
    # Lưu model dạng h5
    h5_path = os.path.join(output_dir, 'chatbot_svm_model.h5')
    save_model_to_h5(
        best_model['model'],
        label_encoder,
        X_embeddings,
        y_labels,
        responses,
        h5_path
    )
    
    logging.info(f"\nModel and label encoder saved in {output_dir} directory")
    logging.info(f"H5 model saved as {h5_path}")

if __name__ == "__main__":
    try:
        train_evaluate_model()
    except Exception as e:
        logging.error(f"An error occurred: {str(e)}")