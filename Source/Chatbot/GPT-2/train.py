import torch
from transformers import RobertaForCausalLM, AutoTokenizer, DataCollatorForLanguageModeling
from transformers import Trainer, TrainingArguments
from datasets import load_dataset

# 1. Tải mô hình và tokenizer PhoBERT
model_name = "vinai/phobert-base"  # Sử dụng mô hình PhoBERT
tokenizer = AutoTokenizer.from_pretrained(model_name)
model = RobertaForCausalLM.from_pretrained(model_name)

# Thiết lập padding token
tokenizer.pad_token = tokenizer.eos_token  # Thiết lập padding token giống với end of sequence token
model.resize_token_embeddings(len(tokenizer))  # Cập nhật kích thước embedding của mô hình

# 2. Chuẩn bị dữ liệu
file_path = '/kaggle/input/ocop-dataset/ocop_data.txt'  # Đường dẫn đến tệp dữ liệu

# Tải dataset từ tệp văn bản
dataset = load_dataset('text', data_files=file_path)  # Tải dataset từ tệp văn bản

def clean_text(text):
    # Hàm để làm sạch dữ liệu đầu vào
    text = text.replace("�", "")  # Xóa ký tự không hợp lệ
    text = text.encode('utf-8', 'ignore').decode('utf-8', 'ignore')  # Chỉ giữ lại ký tự hợp lệ
    return text.strip()  # Trả về văn bản đã được xử lý

def tokenize_function(examples):
    # Hàm để token hóa dữ liệu
    examples["text"] = [clean_text(text) for text in examples["text"]]  # Làm sạch dữ liệu đầu vào
    return tokenizer(examples["text"], truncation=True, padding="max_length", max_length=128)

# Token hóa dataset
tokenized_dataset = dataset['train'].map(tokenize_function, batched=True, remove_columns=["text"])  # Áp dụng token hóa cho dataset

# 3. Chuẩn bị data collator
data_collator = DataCollatorForLanguageModeling(tokenizer=tokenizer, mlm=False)  # Chuẩn bị collator cho training

# 4. Thiết lập tham số huấn luyện
training_args = TrainingArguments(
    output_dir="./ocop-chatbot",  # Thư mục lưu mô hình
    overwrite_output_dir=True,  # Ghi đè thư mục đầu ra nếu đã tồn tại
    num_train_epochs=3,  # Số lượng epochs
    per_device_train_batch_size=8,  # Kích thước batch cho từng thiết bị
    save_steps=10_000,  # Lưu mô hình sau mỗi 10,000 bước
    save_total_limit=2,  # Giới hạn số lượng mô hình đã lưu
    prediction_loss_only=True,  # Chỉ lưu loss trong quá trình huấn luyện
)

# 5. Khởi tạo Trainer
trainer = Trainer(
    model=model,  # Mô hình
    args=training_args,  # Tham số huấn luyện
    data_collator=data_collator,  # Data collator
    train_dataset=tokenized_dataset,  # Dataset đã được token hóa
)

# 6. Huấn luyện mô hình
trainer.train()  # Bắt đầu huấn luyện

# 7. Lưu mô hình và tokenizer
model_path = "./ocop-chatbot-final"  # Đường dẫn lưu mô hình cuối cùng
model.save_pretrained(model_path)  # Lưu mô hình
tokenizer.save_pretrained(model_path)  # Lưu tokenizer

print(f"Mô hình đã được lưu tại: {model_path}")  # Thông báo đường dẫn lưu mô hình

# 8. Kiểm tra mô hình
def clean_response(response):
    # Hàm để làm sạch phản hồi
    response = response.replace("�", "")  # Xóa ký tự không hợp lệ
    response = response.encode('utf-8', 'ignore').decode('utf-8', 'ignore')  # Chỉ giữ lại ký tự hợp lệ
    response = ''.join([c if ord(c) < 128 else '' for c in response])  # Loại bỏ các ký tự không phải ASCII
    return response.strip()  # Trả về phản hồi đã được xử lý

def generate_response(prompt):
    # Hàm để tạo phản hồi từ mô hình
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    model.to(device)
    input_ids = tokenizer.encode(prompt, return_tensors='pt').to(device)  # Mã hóa prompt thành tensor và chuyển sang thiết bị
    attention_mask = (input_ids != tokenizer.pad_token_id).long()  # Tạo attention mask
    
    output = model.generate(
        input_ids, 
        attention_mask=attention_mask,  # Truyền attention mask
        max_length=100, 
        num_return_sequences=1, 
        no_repeat_ngram_size=2, 
        do_sample=True,  # Bật sampling
        temperature=0.7,  # Điều chỉnh temperature
        top_k=50,  # Điều chỉnh top_k
        top_p=0.95,  # Điều chỉnh top_p
        pad_token_id=tokenizer.pad_token_id  # Thiết lập pad_token_id
    )  # Tạo đầu ra từ mô hình

    # Giải mã đầu ra thành văn bản, skip_special_tokens giúp loại bỏ ký tự đặc biệt
    response = tokenizer.decode(output[0], skip_special_tokens=True)
    
    response = clean_response(response)  # Làm sạch phản hồi
    
    return response  # Trả về phản hồi đã được xử lý

# Ví dụ sử dụng
prompt = "Các sản phẩm OCOP nổi tiếng ở Cần Thơ là"  # Prompt mẫu
response = generate_response(prompt)  # Tạo phản hồi từ mô hình
print(f"Câu hỏi: {prompt}")  # In ra câu hỏi
print(f"Trả lời: {response}")  # In ra phản hồi từ mô hình