import json
import numpy as np
import h5py
from sklearn.metrics import precision_score, recall_score, f1_score, confusion_matrix
from sklearn.model_selection import KFold
from sklearn.preprocessing import LabelEncoder
import torch
import torch.nn as nn
import torch.optim as optim
from transformers import AutoModel, AutoTokenizer
import logging
import sys
import io
import os
from dotenv import load_dotenv

# Cấu hình logging
logging.basicConfig(filename='chatbot_interactions.log', level=logging.INFO, 
                    format='%(asctime)s - %(levelname)s - %(message)s')

load_dotenv()

# Đảm bảo đầu ra tiêu chuẩn (stdout) sử dụng UTF-8
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# Tải PhoBERT-base-v2 model và tokenizer từ Hugging Face
phobert = AutoModel.from_pretrained("vinai/phobert-base-v2")
tokenizer = AutoTokenizer.from_pretrained("vinai/phobert-base-v2")

# Đường dẫn file H5
h5_file = 'chatbot_data_phobert_rnn.h5'

# Đọc dữ liệu từ file intents.json
with open('intents.json', 'r', encoding='utf-8') as file:
    data = json.load(file)

# Log số lớp (intents)
num_classes = len(data['intents'])
print(f"Tổng số lớp (intents): {num_classes}")

# Tách dữ liệu thành câu hỏi và câu trả lời
questions = []
answers = []
for intent in data['intents']:
    for pattern in intent['patterns']:
        questions.append(pattern)
    responses = intent['responses']
    for pattern in intent['patterns']:
        answer = responses[questions.index(pattern) % len(responses)]
        answers.append(answer)

# Chuyển đổi dữ liệu thành numpy array
questions = np.array(questions)
answers = np.array(answers)

# Encode labels
label_encoder = LabelEncoder()
encoded_answers = label_encoder.fit_transform(answers)

# Hàm lấy embedding từ PhoBERT
def get_phobert_embedding(text):
    tokens = tokenizer(text, return_tensors='pt', padding=True, truncation=True, max_length=256)
    with torch.no_grad():
        outputs = phobert(**tokens)
    embedding = outputs.last_hidden_state.mean(dim=1)
    return embedding.squeeze().numpy()

# Tính toán embedding cho tất cả câu hỏi
question_embeddings = np.array([get_phobert_embedding(q) for q in questions])

# Định nghĩa mô hình RNN
class RNNModel(nn.Module):
    def __init__(self, input_size, hidden_size, output_size):
        super(RNNModel, self).__init__()
        self.rnn = nn.RNN(input_size, hidden_size, batch_first=True)
        self.fc = nn.Linear(hidden_size, output_size)
    
    def forward(self, x):
        h0 = torch.zeros(1, x.size(0), hidden_size).to(device)
        out, _ = self.rnn(x, h0)
        out = self.fc(out[:, -1, :])
        return out

# Thiết lập các tham số
input_size = question_embeddings.shape[1]
hidden_size = 128
output_size = len(label_encoder.classes_)  # Số lượng lớp phải khớp với số lượng lớp trong LabelEncoder
num_epochs = 10
batch_size = 32
learning_rate = 0.001

# Khởi tạo mô hình, loss function và optimizer
device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
model = RNNModel(input_size, hidden_size, output_size).to(device)
criterion = nn.CrossEntropyLoss()
optimizer = optim.Adam(model.parameters(), lr=learning_rate)

# Thiết lập K-fold Cross Validation
kf = KFold(n_splits=5, shuffle=True, random_state=42)

# Lưu trữ các kết quả để tính toán độ chính xác
accuracies = []

# Lặp qua từng fold
for train_index, test_index in kf.split(question_embeddings):
    train_questions, test_questions = question_embeddings[train_index], question_embeddings[test_index]
    train_answers, test_answers = encoded_answers[train_index], encoded_answers[test_index]

    # Chuyển đổi dữ liệu thành tensor và thêm chiều batch
    train_questions = torch.tensor(train_questions, dtype=torch.float32).unsqueeze(1).to(device)
    test_questions = torch.tensor(test_questions, dtype=torch.float32).unsqueeze(1).to(device)
    train_answers = torch.tensor(train_answers, dtype=torch.long).to(device)
    test_answers = torch.tensor(test_answers, dtype=torch.long).to(device)

    # Huấn luyện mô hình
    model.train()
    for epoch in range(num_epochs):
        optimizer.zero_grad()
        outputs = model(train_questions)
        loss = criterion(outputs, train_answers)
        loss.backward()
        optimizer.step()
        print(f'Epoch [{epoch+1}/{num_epochs}], Loss: {loss.item():.4f}')

    # Đánh giá mô hình trên tập kiểm tra
    model.eval()
    with torch.no_grad():
        outputs = model(test_questions)
        _, predicted = torch.max(outputs.data, 1)
        correct = (predicted == test_answers).sum().item()
        accuracy = correct / len(test_answers)
        accuracies.append(accuracy)

        # Tính toán precision, recall, F1-score
        precision = precision_score(test_answers.cpu(), predicted.cpu(), average='weighted')
        recall = recall_score(test_answers.cpu(), predicted.cpu(), average='weighted')
        f1 = f1_score(test_answers.cpu(), predicted.cpu(), average='weighted')

        print(f'Accuracy for this fold: {accuracy * 100:.2f}%')
        print(f'Precision: {precision:.4f}, Recall: {recall:.4f}, F1 Score: {f1:.4f}')

        # Tính toán confusion matrix
        cm = confusion_matrix(test_answers.cpu(), predicted.cpu())
        print('Confusion Matrix:')
        print(cm)
        logging.info(f'Confusion Matrix:\n{cm}')

# Tính toán độ chính xác trung bình
mean_accuracy = np.mean(accuracies)
print(f'Mean Accuracy across all folds: {mean_accuracy * 100:.2f}%')

# Xuất ra file H5 sau khi hoàn thành K-fold
with h5py.File(h5_file, 'w') as h5f:
    h5f.create_dataset('questions', data=np.array(questions, dtype=h5py.string_dtype(encoding='utf-8')))
    h5f.create_dataset('answers', data=np.array(answers, dtype=h5py.string_dtype(encoding='utf-8')))
    h5f.create_dataset('question_embeddings', data=question_embeddings.astype('float16'))
    h5f.create_dataset('accuracies', data=np.array(accuracies, dtype='float32'))

print('Dữ liệu đã được lưu vào file H5.')