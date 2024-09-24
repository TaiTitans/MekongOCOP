import 'package:http/http.dart' as http;
import 'package:flutter_dotenv/flutter_dotenv.dart';
class AuthService {
  String getOTPSignUpUrl(String email) {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/otp/signup?email=$email';
  }

  Future<bool> sendOTPSignUp(String email) async {
    final String _OTPSignUpUrl = getOTPSignUpUrl(email);

    try {
      final response = await http.post(
        Uri.parse(_OTPSignUpUrl),
      );

      if (response.statusCode == 200) {
        return true;
      } else {
        print('Failed to send OTP: ${response.statusCode}');
        return false;
      }
    } catch (e) {
      print('Error: $e');
      return false;
    }
  }
}
