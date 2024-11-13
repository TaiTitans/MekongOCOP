import json
import numpy as np
import h5py
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics import precision_score, recall_score, f1_score, confusion_matrix
from sklearn.model_selection import KFold
from sklearn.svm import SVC
import logging
import sys
import io
import os
from underthesea import word_tokenize
import random
import string

# Cấu hình logging
logging.basicConfig(filename='chatbot_interactions.log', level=logging.INFO,
                   format='%(asctime)s - %(levelname)s - %(message)s')

# Đảm bảo đầu ra UTF-8
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

def preprocess_text(text):
    # Tokenize tiếng Việt
    text = word_tokenize(text, format="text")
    # Chuyển về lowercase
    text = text.lower()
    # Xóa dấu câu
    text = ''.join([c for c in text if c not in string.punctuation])
    return text

def augment_data(text, num_augmented=2):
    # Tạo câu mới bằng cách hoán đổi từ và thêm/xóa một số từ
    words = text.split()
    augmented_texts = []
    
    for _ in range(num_augmented):
        if len(words) > 2:
            # Hoán đổi vị trí của các từ
            idx1, idx2 = random.sample(range(len(words)), 2)
            words[idx1], words[idx2] = words[idx2], words[idx1]
            
        augmented_text = ' '.join(words)
        augmented_texts.append(augmented_text)
    
    return augmented_texts

# Đọc dữ liệu
with open('intents.json', 'r', encoding='utf-8') as file:
    data = json.load(file)

# Chuẩn bị dữ liệu
questions = []
answers = []
labels = []
label_map = {}

for idx, intent in enumerate(data['intents']):
    label_map[intent['tag']] = idx
    for pattern in intent['patterns']:
        # Tiền xử lý câu hỏi gốc
        processed_pattern = preprocess_text(pattern)
        questions.append(processed_pattern)
        answers.append(random.choice(intent['responses']))
        labels.append(idx)
        
        # Thêm dữ liệu augmented
        augmented_patterns = augment_data(processed_pattern)
        for aug_pattern in augmented_patterns:
            questions.append(aug_pattern)
            answers.append(random.choice(intent['responses']))
            labels.append(idx)

# Chuyển đổi thành numpy array
questions = np.array(questions)
answers = np.array(answers)
labels = np.array(labels)

# Cấu hình TF-IDF
vectorizer = TfidfVectorizer(
    ngram_range=(1, 2),
    max_features=5000,
    min_df=2,
    max_df=0.95,
    sublinear_tf=True
)

# Tính TF-IDF
question_embeddings = vectorizer.fit_transform(questions)

# K-fold Cross Validation
kf = KFold(n_splits=5, shuffle=True, random_state=42)
accuracies = []
precisions = []
recalls = []
f1_scores = []

for fold, (train_index, test_index) in enumerate(kf.split(question_embeddings)):
    print(f"\nFold {fold + 1}")
    
    train_questions = questions[train_index]
    test_questions = questions[test_index]
    train_labels = labels[train_index]
    test_labels = labels[test_index]

    # Huấn luyện SVM với tham số tối ưu
    svm = SVC(
        kernel='rbf',
        C=10.0,
        gamma='scale',
        probability=True,
        class_weight='balanced'
    )
    svm.fit(question_embeddings[train_index], train_labels)

    # Đánh giá
    predictions = svm.predict(question_embeddings[test_index])
    
    # Tính các metrics
    precision = precision_score(test_labels, predictions, average='weighted')
    recall = recall_score(test_labels, predictions, average='weighted')
    f1 = f1_score(test_labels, predictions, average='weighted')
    accuracy = np.mean(predictions == test_labels)
    
    accuracies.append(accuracy)
    precisions.append(precision)
    recalls.append(recall)
    f1_scores.append(f1)
    
    print(f"Accuracy: {accuracy:.4f}")
    print(f"Precision: {precision:.4f}")
    print(f"Recall: {recall:.4f}")
    print(f"F1-score: {f1:.4f}")
    
    # Confusion Matrix
    cm = confusion_matrix(test_labels, predictions)
    print("\nConfusion Matrix:")
    print(cm)

# In kết quả trung bình
print("\nKết quả trung bình qua 5 folds:")
print(f"Accuracy: {np.mean(accuracies):.4f} ± {np.std(accuracies):.4f}")
print(f"Precision: {np.mean(precisions):.4f} ± {np.std(precisions):.4f}")
print(f"Recall: {np.mean(recalls):.4f} ± {np.std(recalls):.4f}")
print(f"F1-score: {np.mean(f1_scores):.4f} ± {np.std(f1_scores):.4f}")

# Lưu model
with h5py.File('chatbot_data_tfidf_svm.h5', 'w') as h5f:
    h5f.create_dataset('questions', data=np.array(questions, dtype=h5py.string_dtype(encoding='utf-8')))
    h5f.create_dataset('answers', data=np.array(answers, dtype=h5py.string_dtype(encoding='utf-8')))
    h5f.create_dataset('question_embeddings', data=question_embeddings.toarray().astype('float32'))
    h5f.create_dataset('labels', data=labels)
    h5f.create_dataset('metrics/accuracies', data=accuracies)
    h5f.create_dataset('metrics/precisions', data=precisions)
    h5f.create_dataset('metrics/recalls', data=recalls)
    h5f.create_dataset('metrics/f1_scores', data=f1_scores)

print("\nĐã lưu dữ liệu vào file H5.")