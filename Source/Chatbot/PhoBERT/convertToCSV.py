import json
import pandas as pd
import sys

# Đọc dữ liệu từ file intents.json
with open('intents.json', 'r', encoding='utf-8') as file:
    data = json.load(file)

# Chuyển đổi dữ liệu JSON thành DataFrame
intents = data['intents']
df = pd.DataFrame(intents)

# Xuất dữ liệu ra file CSV với BOM
df.to_csv('intents.csv', index=False, encoding='utf-8-sig')

# Sử dụng sys.stdout.reconfigure để thiết lập mã hóa utf-8 cho hàm print
sys.stdout.reconfigure(encoding='utf-8')

# Hiển thị dữ liệu dưới dạng bảng
print(df.to_markdown(index=False))