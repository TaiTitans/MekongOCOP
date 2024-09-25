import 'package:dio/dio.dart';
import 'package:cookie_jar/cookie_jar.dart';
import 'package:dio_cookie_manager/dio_cookie_manager.dart';
import 'dart:io';
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:shared_preferences/shared_preferences.dart';

class ProfileService {
  final Dio _dio = Dio();
  final CookieJar cookieJar;

  ProfileService(this.cookieJar) {
    _dio.interceptors.add(CookieManager(cookieJar));
  }

  String addProfileUrl() {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/user/profile';
  }
  String loginUrl() {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/login';
  }
  Future<void> submitProfile({
    required String fullName,
    required String birthDay,
    required String sex,
    required String bio,
    required File? imageFile,
  }) async {
    final String url = addProfileUrl();

    // Lấy accessToken từ SharedPreferences
    final sharedPreferences = await SharedPreferences.getInstance();
    final accessToken = sharedPreferences.getString('accessToken') ?? '';

    if (accessToken.isEmpty) {
      throw Exception('Access token not found in SharedPreferences');
    }

    // Lấy thông tin hasProfile
    final hasProfile = sharedPreferences.getBool('hasProfile') ?? false;
    print('Has profile: $hasProfile');

    // Tạo form-data để gửi lên server
    Map<String, dynamic> formDataMap = {
      'dto': json.encode({
        'full_name': fullName, // Change to match backend field name
        'birthday': birthDay, // Ensure the format is "yyyy-MM-dd"
        'sex': sex,
        'bio': bio,
      }),
    };

    if (imageFile != null) {
      formDataMap['file'] = await MultipartFile.fromFile(
        imageFile.path,
        filename: 'profile.jpg',
      );
    }

    FormData formData = FormData.fromMap(formDataMap);
    final dio = Dio();

    try {
      Response response = await dio.post(
        url,
        data: formData,
        options: Options(
          headers: {
            'Authorization': 'Bearer $accessToken',
          },
        ),
      );

      if (response.statusCode == 200) {
        print('Profile submitted successfully');
      } else {
        print('Failed to submit profile: ${response.data}');
      }
    } catch (e) {
      print('Error: $e');
    }
  }

}
