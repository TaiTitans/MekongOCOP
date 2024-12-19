import asyncio
import aiohttp
from flask import Flask, request, jsonify
import h5py
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
import torch
from transformers import AutoModel, AutoTokenizer
import sys
import logging
import requests
import os
from dotenv import load_dotenv
import re

# Load biến môi trường từ file .env
load_dotenv()

# Lấy API key và CSE ID từ biến môi trường
api_key = os.getenv('API_KEY')
cse_id = os.getenv('CSE_ID')

# Đảm bảo đầu ra tiêu chuẩn (stdout) sử dụng UTF-8
if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding='utf-8')
else:
    sys.stdout = open(sys.stdout.fileno(), mode='w', encoding='utf-8', buffering=1)

# Khởi tạo Flask app
app = Flask(__name__)

# Cấu hình logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Tải PhoBERT model và tokenizer từ Hugging Face
try:
    phobert = AutoModel.from_pretrained("vinai/phobert-base-v2")
    tokenizer = AutoTokenizer.from_pretrained("vinai/phobert-base-v2")
except Exception as e:
    logger.error(f"Không thể tải PhoBERT: {str(e)}")
    sys.exit(1)

# Từ điển các viết tắt thông dụng
abbreviation_dict = {
    "i": "đi",
    "s": "sao",
    "k": "không",
    "xc":"xin chào",
    "ko": "không",
    "tn": "tên",
    "dc": "được",
    "sdt": "số điện thoại",
    "pls": "làm ơn",
    "ns": "nói",
    "sn": "sinh nhật",
    "kh": "khách hàng",
    "ad": "admin",
    "nv": "nhân viên",
    "bc": "báo cáo",
    "vs": "vệ sinh",
    "ht": "hỗ trợ",
    "d/c": "địa chỉ",
    "kb": "kết bạn",
    "b": "bạn",
    "r": "rồi",
    "bt": "bình thường",
    "bthg": "bình thường",
    "tgd": "thời gian",
    "t/g": "thời gian",
    "tg": "thời gian",
    "stt": "status",
    "mng": "mọi người",
    "z": "zalo",
    "v": "vậy",
}

# Hàm thay thế các từ viết tắt
def expand_abbreviations(text):
    words = text.split()
    expanded_words = [abbreviation_dict.get(word.lower(), word) for word in words]
    return " ".join(expanded_words)


# Hàm load dữ liệu từ file h5
def load_data():
    try:
        with h5py.File('chatbot_data_phobert_v4.h5', 'r') as h5f:
            questions = [q.decode('utf-8') for q in h5f['questions'][:]]
            answers = [a.decode('utf-8') for a in h5f['answers'][:]]
            question_embeddings = np.array(h5f['question_embeddings'][:], dtype=np.float32)
        return questions, answers, question_embeddings
    except Exception as e:
        logger.error(f"Không thể tải dữ liệu từ file H5: {str(e)}")
        sys.exit(1)

# Tải dữ liệu từ file H5
questions, answers, question_embeddings = load_data()

# Hàm lấy embedding từ PhoBERT, sử dụng hàm mở rộng từ viết tắt
def get_phobert_embedding(text):
    # Mở rộng các từ viết tắt trong câu hỏi người dùng
    expanded_text = expand_abbreviations(text)
    
    tokens = tokenizer(expanded_text, return_tensors='pt', padding=True, truncation=True, max_length=256)
    with torch.no_grad():
        outputs = phobert(**tokens)
    return outputs.last_hidden_state.mean(dim=1).squeeze().numpy()

# Endpoint trả lời câu hỏi
@app.route('/ask', methods=['POST'])
def ask():
    data = request.json
    user_input = data.get('question', '')
    
    if not user_input:
        return jsonify({"error": "Câu hỏi không được để trống."}), 400
    
    try:
        input_embedding = get_phobert_embedding(user_input).reshape(1, -1)
    except Exception as e:
        logger.error(f"Lỗi khi lấy embedding: {str(e)}")
        return jsonify({"error": "Không thể xử lý câu hỏi."}), 500
    
    similarities = cosine_similarity(input_embedding, question_embeddings)
    max_similarity = float(similarities.max())

    logger.info(f"Độ tương đồng lớn nhất: {max_similarity}")
    most_similar_index = similarities.argmax()

    if max_similarity > 0.4:
        response = answers[most_similar_index]
    else:
        response = "Xin lỗi, tôi không hiểu câu hỏi của bạn."

    return jsonify({
        "question": user_input,
        "response": response,
        "similarity": max_similarity
    })

# Khởi chạy Flask app
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)