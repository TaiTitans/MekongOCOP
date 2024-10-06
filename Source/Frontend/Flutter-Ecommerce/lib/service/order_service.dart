import 'package:dio/dio.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class OrderService{
  final _dio = Dio();

  String getOrderUrl() {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/common/order';
  }


  String cancelOrderUrl(int orderId) {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/common/order/$orderId/cancel';
  }

  String getProductUrl(int productId) {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/common/store/product/$productId';
  }
  Future<Map<String, dynamic>> getProduct(String accessToken, int productId) async {
    final String url = getProductUrl(productId);
    try {
      Response response = await _dio.get(
        url,
        options: Options(
          headers: {
            'Authorization': 'Bearer $accessToken',
          },
        ),
      );

      // Kiểm tra và trả về dữ liệu
      if (response.statusCode == 200) {
        return response.data; // Trả về dữ liệu dạng Map
      } else {
        throw Exception('Failed to load orders: ${response.statusCode}');
      }
    } catch (e) {
      // Xử lý lỗi
      throw Exception('Failed to load orders: $e');
    }
  }

  Future<Map<String, dynamic>> getOrder(String accessToken) async {
    final String url = getOrderUrl();
    try {
      Response response = await _dio.get(
        url,
        options: Options(
          headers: {
            'Authorization': 'Bearer $accessToken',
          },
        ),
      );

      // Kiểm tra và trả về dữ liệu
      if (response.statusCode == 200) {
        return response.data; // Trả về dữ liệu dạng Map
      } else {
        throw Exception('Failed to load orders: ${response.statusCode}');
      }
    } catch (e) {
      // Xử lý lỗi
      throw Exception('Failed to load orders: $e');
    }
  }

  // Hàm gửi yêu cầu hủy đơn hàng
  Future<Map<String, dynamic>> cancelOrder(String accessToken, int orderId) async {
    final String url = cancelOrderUrl(orderId);
    try {
      Response response = await _dio.patch(
        url,
        options: Options(
          headers: {
            'Authorization': 'Bearer $accessToken',
          },
        ),
      );

      // Kiểm tra và trả về dữ liệu
      if (response.statusCode == 200) {
        return response.data;
      } else {
        throw Exception('Failed to cancel order: ${response.statusCode}');
      }
    } catch (e) {
      // Xử lý lỗi
      throw Exception('Failed to cancel order: $e');
    }
  }


}