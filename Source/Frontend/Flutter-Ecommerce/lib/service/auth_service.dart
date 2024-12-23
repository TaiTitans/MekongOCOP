import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:dio_cookie_manager/dio_cookie_manager.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:cookie_jar/cookie_jar.dart';
import 'package:jwt_decoder/jwt_decoder.dart';
import 'package:shared_preferences/shared_preferences.dart';

class AuthService {
  final CookieJar cookieJar = CookieJar();
  final Dio _dio = Dio();
  String getProfileUrl(){
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/user/profile';
  }
  String updateProfileUrl(){
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/user/profile';
  }
  String registerAccountUrl(int otp) {
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

  String checkProfileUrl() {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/user/checkProfile';
  }

  String getOTPRenewPassUrl(String email) {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/otp/forgot?email=$email';
  }

  String renewPasswordUrl(String email, int otp, String newPassword) {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/user/password/renew?email=$email&otp=$otp&newPassword=$newPassword';
  }

  Future<void> init() async {
    final sharedPreferences = await SharedPreferences.getInstance();
    String? token = sharedPreferences.getString('accessToken');

    if (token != null && !isTokenNotExpired(token)) {
      await refreshAccessTokenIfNeeded();
    }
  }

  // Kiểm tra và làm mới token nếu hết hạn
  Future<void> refreshAccessTokenIfNeeded() async {
    final sharedPreferences = await SharedPreferences.getInstance();
    String? accessToken = sharedPreferences.getString('accessToken');

    // Kiểm tra nếu accessToken hết hạn
    if (accessToken != null && !isTokenNotExpired(accessToken)) {
      await refreshAccessToken();
    }
  }


  String getNewAccessToken() {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/user/refresh-token';
  }
  bool isTokenNotExpired(String? token) {
    if (token == null) {
      return false; // Token null có nghĩa là không hợp lệ
    }

    // Lấy ngày hết hạn từ token
    DateTime? expirationDate = JwtDecoder.getExpirationDate(token);
    if (expirationDate == null) {
      return false; // Nếu không có expirationDate, coi như token không hợp lệ
    }

    // Lấy thời gian hiện tại
    DateTime now = DateTime.now();

    // So sánh thời gian hiện tại với ngày hết hạn
    return now.isBefore(expirationDate); // Trả về true nếu token chưa hết hạn
  }
  Future<String?> refreshAccessToken() async {
    final sharedPreferences = await SharedPreferences.getInstance();
    String? refreshToken = sharedPreferences.getString('refreshToken'); // Lấy refreshToken từ SharedPreferences

    if (refreshToken == null) {
      print('Refresh token is missing');
      return null;
    }

    final String url = getNewAccessToken();
    final dio = Dio();

    try {
      final response = await dio.post(
        url,
        options: Options(
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer $refreshToken', // Gửi refreshToken qua header Authorization
          },
        ),
      );

      if (response.statusCode == 200) {
        // Giả sử server trả về accessToken mới
        String newAccessToken = response.data['accessToken'];

        // Cập nhật accessToken trong SharedPreferences
        await sharedPreferences.setString('accessToken', newAccessToken);

        return newAccessToken;
      } else {
        print('Error refreshing token: ${response.data}');
      }
    } catch (e) {
      print('Error: $e');
    }

    return null;
  }

  Future<Map<String, dynamic>> signIn(String username, String password) async {
    final String url = loginUrl();
    final dio = Dio();

    try {
      final response = await dio.post(
        url,
        data: jsonEncode({
          'username': username,
          'password': password,
        }),
        options: Options(
          headers: {'Content-Type': 'application/json'},
        ),
      );

      if (response.statusCode == 200) {
        final responseBody = response.data;

        // Xử lý cookies
        final rawCookies = response.headers['set-cookie'];
        final cookies = _parseCookies(rawCookies is List<String> ? rawCookies.join(', ') : rawCookies);

        // Lưu accessToken, refreshToken và hasProfile vào SharedPreferences
        final sharedPreferences = await SharedPreferences.getInstance();
        await sharedPreferences.setString('accessToken', cookies['accessToken'] ?? '');
        await sharedPreferences.setString('refreshToken', cookies['refreshToken'] ?? '');

        // Kiểm tra và lưu hasProfile
        final hasProfile = cookies['hasProfile']?.toLowerCase() == 'true';
        await sharedPreferences.setBool('hasProfile', hasProfile);

        return {
          'cookies': cookies,
          'hasProfile': hasProfile,
          'responseBody': responseBody,
        };
      } else {
        final responseBody = response.data;
        return {'error': responseBody['message']};
      }
    } catch (e) {
      print('Error: $e');
      return {'error': 'Sai tên đăng nhập hoặc mật khẩu'};
    }
  }

  Map<String, String> _parseCookies(String? rawCookies) {
    if (rawCookies == null) return {};

    return rawCookies.split(',').fold({}, (Map<String, String> acc, String cookie) {
      List<String> parts = cookie.split(';')[0].split('=');
      if (parts.length == 2) {
        String key = parts[0].trim();
        String value = parts[1].trim();
        acc[key] = value;
      }
      return acc;
    });
  }


  Future<String?> registerAccount(int otp, String username, String password,
      String email) async {
    final String url = registerAccountUrl(otp);
    try {
      final response = await http.post(
          Uri.parse(url),
          headers: {'Content-Type': 'application/json'},
          body: jsonEncode({
            'username': username,
            'password': password,
            'email': email,
          })
      );
      if (response.statusCode == 200) {
        return null;
      } else {
        final responseBody = jsonDecode(response.body);
        return responseBody['message'];
      }
    } catch (e) {
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

  Future<String?> renewPassword(String email, int otp,
      String newPassword) async {
    final String _RenewPasswordUrl = renewPasswordUrl(email, otp, newPassword);
    try {
      final response = await http.patch(Uri.parse(_RenewPasswordUrl));

      if (response.statusCode == 200) {
        return null;
      } else {
        final responseBody = jsonDecode(response.body);
        return responseBody['message'];
      }
    } catch (e) {
      print('Error: $e');
      return 'Lỗi không xác định. Xin hãy thử lại.';
    }
  }

  Future<Map<String, String>> getLoginCookies() async {
    final String url = loginUrl();
    final response = await http.get(Uri.parse(url));
    final cookies = response.headers['set-cookie'];
    if (cookies != null) {
      return Map.fromEntries(
        cookies.split(';').map((cookie) {
          final parts = cookie.trim().split('=');
          if (parts.length == 2) {
            return MapEntry(parts[0].trim(), parts[1].trim());
          }
          return MapEntry('', '');
        }).where((entry) => entry.key.isNotEmpty),
      );
    }
    return {};
  }



  Future<Map<String, dynamic>?> fetchUserProfile(String accessToken) async {
    try {
      final response = await _dio.get(
        getProfileUrl(),
        options: Options(
          headers: {
            'Authorization': 'Bearer $accessToken',
            'Content-Type': 'application/json',
          },
        ),
      );

      if (response.statusCode == 200 && response.data['status'] == 'Success') {
        return response.data['data']; // Trả về dữ liệu sản phẩm từ response
      } else {
        throw Exception('Failed to load product details');
      }
    } catch (e) {
      print('Error: $e');
      return null;
    }
  }
  Future<Map<String, dynamic>?> updateUserProfile({
    required String fullName,
    required String birthday,
    required String sex,
    required String bio,
  }) async {
    try {
      // Get the access token from SharedPreferences
      final sharedPreferences = await SharedPreferences.getInstance();
      final accessToken = sharedPreferences.getString('accessToken') ?? '';

      if (accessToken.isEmpty) {
        throw Exception('Access token not found in SharedPreferences');
      }

      // Prepare the request body
      final Map<String, dynamic> requestBody = {
        "full_name": fullName,
        "birthday": birthday,
        "sex": sex,
        "bio": bio,
      };

      // Make the PUT request
      final response = await _dio.put(
        updateProfileUrl(),
        data: requestBody,
        options: Options(
          headers: {
            'Authorization': 'Bearer $accessToken',
            'Content-Type': 'application/json',
          },
        ),
      );

      // Handle successful response
      if (response.statusCode == 200) {
        return response.data;
      } else {
        throw Exception('Failed to update profile');
      }
    } catch (e) {
      print('Error updating profile: $e');
      return null;
    }
  }
}

