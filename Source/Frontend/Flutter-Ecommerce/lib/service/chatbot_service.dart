import 'package:dio/dio.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class ChatbotService {
  final Dio _dio = Dio();

  // Hàm trả về URL API
  String askQuestionUrl() {
    final String apiUrl = dotenv.env['API_CHATBOT_URL'] ?? '';
    return '$apiUrl/ask';
  }

  // Hàm gửi câu hỏi đến API và nhận phản hồi
  Future<Map<String, dynamic>> sendQuestion(String question) async {
    try {
      final String url = askQuestionUrl();

      // Tạo body cho request
      Map<String, String> requestBody = {
        'question': question,
      };

      // Gửi POST request đến server
      Response response = await _dio.post(
        url,
        data: requestBody,
        options: Options(
          headers: {
            'Content-Type': 'application/json',
          },
        ),
      );

      // Kiểm tra nếu response thành công
      if (response.statusCode == 200) {
        // Trả về dữ liệu phản hồi
        return response.data as Map<String, dynamic>;
      } else {
        throw Exception('Failed to get response from chatbot API');
      }
    } catch (e) {
      print('Error sending question to chatbot: $e');
      rethrow;
    }
  }
}
