import 'dart:io';

import 'package:dio/dio.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:shared_preferences/shared_preferences.dart';

class SellerService{
  String registerSellerUrl() {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/user/seller/request';
  }

  Future<bool> submitSeller({
    required File? imageFile,
  }) async {
    final String url = registerSellerUrl();
    final sharedPreferences = await SharedPreferences.getInstance();
    final accessToken = sharedPreferences.getString('accessToken') ?? '';

    if (accessToken.isEmpty) {
      throw Exception('Access token not found in SharedPreferences');
    }
    Map<String, dynamic> formDataMap = {
    };
    if (imageFile != null) {
      formDataMap['file'] = await MultipartFile.fromFile(
        imageFile.path,
        filename: 'verifyseller.jpg',
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
        print('Seller request submitted successfully');
        return true;
      } else {
        print('Failed to submit seller request: ${response.data}');
        return false;
      }
    } catch (e) {
      print('Error: $e');
      return false;
    }
  }
}