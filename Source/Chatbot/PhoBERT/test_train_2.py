import json
import numpy as np
import h5py
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.metrics import precision_score, recall_score, f1_score, confusion_matrix
from sklearn.model_selection import KFold
import torch
from transformers import AutoModel, AutoTokenizer
import logging
import sys
import io
import os
from dotenv import load_dotenv
import matplotlib.pyplot as plt
from vncorenlp import VnCoreNLP

# Cấu hình logging
logging.basicConfig(filename='chatbot_interactions.log', level=logging.INFO, 
                    format='%(asctime)s - %(levelname)s - %(message)s')

load_dotenv()

# Đảm bảo đầu ra tiêu chuẩn (stdout) sử dụng UTF-8
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# Tích hợp VNCoreNLP
path_to_vncorenlp = "VnCoreNLP-1.2.zip"  # Cập nhật đường dẫn phù hợp
annotators = "wseg,pos,ner"
vncorenlp = VnCoreNLP(path_to_vncorenlp, annotators=annotators, max_heap_size='-Xmx2g')

# Tải PhoBERT-base-v2 model và tokenizer từ Hugging Face
phobert = AutoModel.from_pretrained("vinai/phobert-base-v2")
tokenizer = AutoTokenizer.from_pretrained("vinai/phobert-base-v2")

# Đường dẫn file H5
h5_file = 'chatbot_data_phobert_v3.h5'

# Đọc dữ liệu từ file intents.json
with open('intents.json', 'r', encoding='utf-8') as file:
    data = json.load(file)

# Log số lớp (intents)
num_classes = len(data['intents'])
print(f"Tổng số lớp (intents): {num_classes}")

# Tách dữ liệu thành câu hỏi và câu trả lời
questions = []
answers = []
labels = []
label_map = {}
for idx, intent in enumerate(data['intents']):
    label_map[intent['tag']] = idx
    for pattern in intent['patterns']:
        questions.append(pattern)
        answers.append(intent['responses'][0])
        labels.append(idx)

# Chuyển đổi dữ liệu thành numpy array
questions = np.array(questions)
answers = np.array(answers)
labels = np.array(labels)

# Hàm nhận diện địa danh với VNCoreNLP
def extract_named_entities(text):
    annotated_text = vncorenlp.annotate(text)
    named_entities = []
    for sentence in annotated_text['sentences']:
        for token in sentence:
            if token['ner'] == "LOCATION":
                named_entities.append(token['form'])
    return named_entities

# Hàm tiền xử lý văn bản với VNCoreNLP
def preprocess_text_with_vncorenlp(text):
    segmented_text = vncorenlp.tokenize(text)
    named_entities = extract_named_entities(text)
    processed_text = " ".join([" ".join(sentence) for sentence in segmented_text])
    if named_entities:
        processed_text += " " + " ".join(named_entities)
    return processed_text

# Hàm lấy embedding từ PhoBERT
def get_phobert_embedding(text):
    text = preprocess_text_with_vncorenlp(text)
    tokens = tokenizer(text, return_tensors='pt', padding=True, truncation=True, max_length=256)
    with torch.no_grad():
        outputs = phobert(**tokens)
    embedding = outputs.last_hidden_state.mean(dim=1)
    return embedding.squeeze().numpy(), tokens

# Tiền xử lý và tính toán embedding cho tất cả câu hỏi
question_embeddings = np.array([get_phobert_embedding(q)[0] for q in questions])

# Thiết lập K-fold Cross Validation
kf = KFold(n_splits=5, shuffle=True, random_state=42)

# Lưu trữ các kết quả để tính toán độ chính xác
accuracies = []
precisions = []
recalls = []
f1_scores = []

# Lặp qua từng fold
threshold = 0.85
for train_index, test_index in kf.split(question_embeddings):
    train_questions, test_questions = questions[train_index], questions[test_index]
    train_answers, test_answers = answers[train_index], answers[test_index]
    train_labels, test_labels = labels[train_index], labels[test_index]

    correct = 0
    predicted_answers = []

    for i, question in enumerate(test_questions):
        input_embedding, _ = get_phobert_embedding(question)
        similarities = cosine_similarity(input_embedding.reshape(1, -1), question_embeddings[train_index])
        max_similarity = similarities.max()

        if max_similarity > threshold:
            most_similar_index = similarities.argmax()
            predicted_answers.append(train_answers[most_similar_index])
            correct += 1
        else:
            predicted_answers.append("Không tìm thấy câu trả lời phù hợp.")

    # Tính toán các chỉ số
    y_true = [1 if ans == exp_ans else 0 for ans, exp_ans in zip(predicted_answers, test_answers)]
    y_pred = y_true  # Đối với Cosine Similarity, y_pred = y_true
    accuracy = correct / len(test_questions)
    accuracies.append(accuracy)
    precisions.append(precision_score(y_true, y_pred))
    recalls.append(recall_score(y_true, y_pred))
    f1_scores.append(f1_score(y_true, y_pred))

# Vẽ biểu đồ kết quả qua các fold
folds = list(range(1, len(accuracies) + 1))
plt.figure(figsize=(10, 6))
plt.plot(folds, accuracies, marker='o', label='Accuracy')
plt.plot(folds, precisions, marker='s', label='Precision')
plt.plot(folds, recalls, marker='^', label='Recall')
plt.plot(folds, f1_scores, marker='d', label='F1 Score')
plt.title("Performance Metrics Across Folds")
plt.xlabel("Fold")
plt.ylabel("Score")
plt.legend()
plt.grid(alpha=0.3)
plt.show()

# Vẽ biểu đồ số lượng câu hỏi và câu trả lời
plt.figure(figsize=(6, 6))
categories = ['Questions', 'Answers']
values = [len(questions), len(answers)]
plt.bar(categories, values, color=['blue', 'green'])
plt.title("Total Questions and Answers")
plt.ylabel("Count")
plt.grid(axis='y', alpha=0.3)
plt.show()

# Xuất ra file H5
with h5py.File(h5_file, 'w') as h5f:
    h5f.create_dataset('questions', data=np.array(questions, dtype=h5py.string_dtype(encoding='utf-8')))
    h5f.create_dataset('answers', data=np.array(answers, dtype=h5py.string_dtype(encoding='utf-8')))
    h5f.create_dataset('question_embeddings', data=question_embeddings.astype('float16'))
    h5f.create_dataset('accuracies', data=np.array(accuracies, dtype='float32'))

print("Dữ liệu đã được lưu vào file H5.")

# Đóng VNCoreNLP
vncorenlp.close()
