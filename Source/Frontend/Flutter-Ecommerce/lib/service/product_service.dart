import 'package:dio/dio.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:shared_preferences/shared_preferences.dart';

class ProductService{
  final Dio _dio = Dio();
  String getNewFeedUrl() {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/common/product/newfeed';
  }

  String favoriteProductUrl(int productId) {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/common/favorite/$productId';
  }





  Future<List<dynamic>> fetchProductsNewFeed(String accessToken) async {
    try {
      final response = await _dio.get(
        getNewFeedUrl(),
        options: Options(
          headers: {
            'Authorization': 'Bearer $accessToken',
            'Content-Type': 'application/json',
          },
        ),
      );

      if (response.statusCode == 200) {
        return response.data['data']; // Return the 'data' part of the response
      } else {
        throw Exception('Failed to load products');
      }
    } catch (e) {
      print('Error: $e');
      return [];
    }
  }

  Future<bool> favoriteProduct(int productId, String accessToken) async {
    try {
      final response = await _dio.post(
        favoriteProductUrl(productId),
        options: Options(
          headers: {
            'Authorization': 'Bearer $accessToken',
            'Content-Type': 'application/json',
          },
        ),
      );

      if (response.statusCode == 200) {
        return true;
      } else {
        throw Exception('Failed to favorite product');
      }
    } catch (e) {
      print('Error: $e');
      return false; // Trả về false nếu có lỗi
    }
  }


}

