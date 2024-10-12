import json
import numpy as np
import h5py
from sklearn.metrics.pairwise import cosine_similarity
import torch
from transformers import AutoModel, AutoTokenizer
import time
import requests

import sys
import io
import os
from dotenv import load_dotenv
load_dotenv()
# Đảm bảo đầu ra tiêu chuẩn (stdout) sử dụng UTF-8
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# Tải PhoBERT-base-v2 model và tokenizer từ Hugging Face
phobert = AutoModel.from_pretrained("vinai/phobert-base-v2")
tokenizer = AutoTokenizer.from_pretrained("vinai/phobert-base-v2")

api_key = os.getenv('API_KEY')
cse_id = os.getenv('CSE_ID')


# Đường dẫn file H5
h5_file = 'chatbot_data_phobert_v2.h5'

# Đọc dữ liệu từ file intents.json
with open('intents.json', 'r', encoding='utf-8') as file:
    data = json.load(file)

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

# Hàm lấy embedding từ PhoBERT, sử dụng mean pooling
def get_phobert_embedding(text):
    if not isinstance(text, str) or not text:  # Kiểm tra nếu text không phải là chuỗi hoặc rỗng
        return np.zeros((1, 768))  # Trả về một embedding giả (768 chiều)
    
    tokens = tokenizer(text, return_tensors='pt', padding=True, truncation=True, max_length=256)
    with torch.no_grad():
        outputs = phobert(**tokens)
    # Sử dụng mean pooling thay vì CLS token
    embedding = outputs.last_hidden_state.mean(dim=1)  # Mean pooling across all token embeddings
    return embedding.squeeze().numpy()

# Kiểm tra xem file H5 đã tồn tại chưa, nếu có thì đọc từ file H5, nếu không thì tạo mới
try:
    with h5py.File(h5_file, 'r') as h5f:
        questions = h5f['questions'][:]
        answers = [a.decode('utf-8') for a in h5f['answers'][:]]  # Giải mã từ byte string sang string
        question_embeddings = h5f['question_embeddings'][:]
    print("Dữ liệu đã được tải từ chatbot_data_phobert_v2.h5")
except FileNotFoundError:
    # Tạo embedding cho tất cả câu hỏi nếu không tìm thấy file H5
    print("Tính toán embedding cho câu hỏi...")
    question_embeddings = np.array([get_phobert_embedding(question) for question in questions])
    
    # Lưu dữ liệu vào file H5
    with h5py.File(h5_file, 'w') as h5f:
        h5f.create_dataset('questions', data=np.array(questions, dtype=h5py.string_dtype(encoding='utf-8')))
        h5f.create_dataset('answers', data=np.array(answers, dtype=h5py.string_dtype(encoding='utf-8')))
        h5f.create_dataset('question_embeddings', data=question_embeddings.astype('float16'))
    print("Dữ liệu đã được lưu vào chatbot_data_phobert_v2.h5")

# Hàm tìm kiếm thông tin từ Google bằng Google Custom Search API
def google_search(query, api_key, cse_id, num_results=5):
    url = "https://www.googleapis.com/customsearch/v1"
    params = {
        "q": query,
        "cx": cse_id,  # Custom Search Engine ID
        "key": api_key,
        "num": num_results
    }
    try:
        response = requests.get(url, params=params)
        results = response.json()
        return results.get('items', [])
    except Exception as e:
        print(f"Lỗi khi gọi Google Custom Search API: {str(e)}")
        return []


# Hàm sinh câu trả lời từ Google Search
def generate_response_from_google_results(search_results):
    # Tổng hợp các đoạn mô tả từ Google Search
    combined_text = " ".join([result['snippet'] for result in search_results if 'snippet' in result])
    if combined_text:
        return combined_text
    else:
        return "Xin lỗi, tôi không tìm thấy thông tin phù hợp."


def get_response(user_input):
    input_embedding = get_phobert_embedding(user_input).reshape(1, -1)
    similarities = cosine_similarity(input_embedding, question_embeddings)
    max_similarity = similarities.max()

    # Logging độ tương đồng
    print(f"Độ tương đồng lớn nhất: {max_similarity}")

    # Kiểm tra ngưỡng tương đồng. Chỉ trả về câu trả lời từ dataset nếu độ tương đồng cao hơn 0.8
    if max_similarity > 0.8:
        most_similar_index = similarities.argmax()
        return answers[most_similar_index], max_similarity
    else:
        # Nếu không có câu trả lời phù hợp, tìm kiếm câu trả lời từ Google thông qua Google Custom Search API
        search_results = google_search(user_input, api_key, cse_id)
        google_response = generate_response_from_google_results(search_results)
        
        if google_response:  # Nếu Google Custom Search API trả về kết quả hợp lệ
            return google_response, 0  # Đặt độ tương đồng bằng 0 vì không có trong dataset
        else:
            # Trả về thông báo không tìm thấy thông tin phù hợp
            return "Xin lỗi, tôi không tìm thấy thông tin phù hợp.", 0
# Hàm tính độ tương đồng giữa câu trả lời của chatbot và câu trả lời mong đợi
def get_answer_similarity(predicted_answer, expected_answer):
    # Kiểm tra nếu cả hai đều là chuỗi văn bản
    if isinstance(predicted_answer, str) and isinstance(expected_answer, str):
        predicted_embedding = get_phobert_embedding(predicted_answer).reshape(1, -1)
        expected_embedding = get_phobert_embedding(expected_answer).reshape(1, -1)
        
        # Tính độ tương đồng cosine giữa hai embedding
        similarity = cosine_similarity(predicted_embedding, expected_embedding)
        return similarity[0][0]
    else:
        # Nếu không phải chuỗi, trả về độ tương đồng bằng 0
        return 0

# Hàm tính accuracy và loss dựa trên độ tương đồng mềm
def calculate_metrics(test_questions, expected_answers):
    correct = 0
    total_loss = 0
    threshold = 0.7  # Giảm ngưỡng xuống 0.7 để ghi nhận các câu trả lời gần đúng

    for i, question in enumerate(test_questions):
        # Lấy câu trả lời từ hệ thống
        response, similarity = get_response(question)
        
        # Tính độ tương đồng giữa câu trả lời chatbot và câu trả lời mong đợi
        answer_similarity = get_answer_similarity(response, expected_answers[i])

        # Nếu độ tương đồng lớn hơn ngưỡng, câu trả lời được coi là đúng
        if answer_similarity > threshold:
            correct += 1
        
        # Tính loss dựa trên độ tương đồng của câu hỏi
        loss = 1 - similarity
        total_loss += loss

        print(f"Câu hỏi {i+1}: {question}")
        print(f"Phản hồi: {response}")
        print(f"Độ tương đồng câu trả lời: {answer_similarity:.4f}")
        print(f"Loss: {loss:.4f}")

    # Tính toán accuracy và loss trung bình
    accuracy = correct / len(test_questions)
    average_loss = total_loss / len(test_questions)
    print(f"Accuracy: {accuracy * 100:.2f}%")
    print(f"Average Loss: {average_loss:.4f}")

# Hàm test chatbot
def test_chatbot():
    test_questions = ["Xin chào", "Tôi muốn biết thông tin về miền Tây", "Làm thế nào để thanh toán hóa đơn?"]
    expected_answers = ["Chào bạn!", "Miền Tây là một vùng đẹp.", "Bạn có thể thanh toán trực tuyến."]
    calculate_metrics(test_questions, expected_answers)

# Gọi hàm test chatbot
test_chatbot()
