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

# Hàm tiền xử lý văn bản với VNCoreNLP
def preprocess_text_with_vncorenlp(text):
    segmented_text = vncorenlp.tokenize(text)
    return " ".join([" ".join(sentence) for sentence in segmented_text])

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
for train_index, test_index in kf.split(question_embeddings):
    train_questions, test_questions = questions[train_index], questions[test_index]
    train_answers, test_answers = answers[train_index], answers[test_index]
    train_labels, test_labels = labels[train_index], labels[test_index]

    correct = 0
    total_loss = 0
    threshold = 0.85
    predicted_answers = []

    for i, question in enumerate(test_questions):
        input_embedding, _ = get_phobert_embedding(question)
        input_embedding = input_embedding.reshape(1, -1)
        similarities = cosine_similarity(input_embedding, question_embeddings[train_index])
        max_similarity = similarities.max()

        logging.info(f"User Input: {question}, Max Similarity: {max_similarity}")

        if max_similarity > threshold:
            most_similar_index = similarities.argmax()
            response = train_answers[most_similar_index]
            predicted_answers.append(response)
            correct += 1
        else:
            response = "Không tìm thấy câu trả lời phù hợp."
            predicted_answers.append(response)

        loss = 1 - max_similarity
        total_loss += loss

    accuracy = correct / len(test_questions)
    accuracies.append(accuracy)

    y_true = [1 if ans == exp_ans else 0 for ans, exp_ans in zip(predicted_answers, test_answers)]
    y_pred = [1 if cosine_similarity(get_phobert_embedding(ans)[0].reshape(1, -1), 
                                       get_phobert_embedding(test_answers[i])[0].reshape(1, -1))[0][0] > threshold else 0 
               for i, ans in enumerate(predicted_answers)]

    precision = precision_score(y_true, y_pred)
    recall = recall_score(y_true, y_pred)
    f1 = f1_score(y_true, y_pred)

    precisions.append(precision)
    recalls.append(recall)
    f1_scores.append(f1)

# Tính toán độ chính xác trung bình
mean_accuracy = np.mean(accuracies)
print(f"Mean Accuracy across all folds: {mean_accuracy * 100:.2f}%")

# Xuất ra file H5
with h5py.File(h5_file, 'w') as h5f:
    h5f.create_dataset('questions', data=np.array(questions, dtype=h5py.string_dtype(encoding='utf-8')))
    h5f.create_dataset('answers', data=np.array(answers, dtype=h5py.string_dtype(encoding='utf-8')))
    h5f.create_dataset('question_embeddings', data=question_embeddings.astype('float16'))
    h5f.create_dataset('accuracies', data=np.array(accuracies, dtype='float32'))

print("Dữ liệu đã được lưu vào file H5.")

# Đóng VNCoreNLP
vncorenlp.close()
