import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:flutter_dotenv/flutter_dotenv.dart';
class AuthService {
  String registerAccountUrl(int otp){
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/register?otp=$otp';
  }

  String getOTPSignUpUrl(String email) {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/otp/signup?email=$email';
  }
  String loginUrl() {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/login';
  }
  String getOTPRenewPassUrl(String email) {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/otp/forgot?email=$email';
  }
  String renewPasswordUrl(String email,int otp, String newPassword){
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/user/password/renew?email=$email&otp=$otp&newPassword=$newPassword';
  }


  Future<String?> signIn(String username, String password) async {
    final String url = loginUrl();
    try{
      final response = await http.post(
        Uri.parse(url),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'username': username,
          'password': password,
        })
      );
      if(response.statusCode == 200){
        return null;
      }else{
        final responseBody = jsonDecode(response.body);
        return responseBody['message'];
      }
    }catch(e){
      print('Error: $e');
      return 'Lỗi không xác định. Xin thử lại';
    }
  }

  Future<String?> registerAccount(int otp, String username, String password, String email) async {
    final String url = registerAccountUrl(otp);
    try{
      final response = await http.post(
        Uri.parse(url),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'username': username,
          'password': password,
          'email': email,
        })
      );
      if(response.statusCode == 200){
        return null;
      }else{
        final responseBody = jsonDecode(response.body);
        return responseBody['message'];
      }

    }catch(e){
      print('Error: $e');
      return 'Lỗi không xác định. Xin thử lại.';
    }
  }


  Future<String?> sendOTPSignUp(String email) async {
    final String _OTPSignUpUrl = getOTPSignUpUrl(email);

    try {
      final response = await http.post(
        Uri.parse(_OTPSignUpUrl),
      );

      if (response.statusCode == 200) {
        return null;
      } else {
        print('Failed to send OTP: ${response.statusCode}');
        final responseBody = jsonDecode(response.body);
        return responseBody['message'];
      }
    } catch (e) {
      print('Error: $e');
      return 'Lỗi không xác định. Xin hãy thử lại.';
    }
  }

  Future<String?> sendOTPForgotPass(String email) async {
    final String _OTPForgotPassUrl = getOTPRenewPassUrl(email);

    try {
      final response = await http.post(
        Uri.parse(_OTPForgotPassUrl),
      );

      if (response.statusCode == 200) {
        return null;
      } else {
        print('Failed to send OTP: ${response.statusCode}');
        final responseBody = jsonDecode(response.body);
        return responseBody['message'];
      }
    } catch (e) {
      print('Error: $e');
      return 'Lỗi không xác định. Xin hãy thử lại.';
    }
  }

  Future<String?> renewPassword(String email, int otp, String newPassword) async {
    final String _RenewPasswordUrl = renewPasswordUrl(email, otp, newPassword);
    try{
      final response = await http.patch(Uri.parse(_RenewPasswordUrl));

      if(response.statusCode == 200){
        return null;
      }else{
        final responseBody = jsonDecode(response.body);
        return responseBody['message'];
      }
    }catch(e){
      print('Error: $e');
      return 'Lỗi không xác định. Xin hãy thử lại.';
    }
  }

}
