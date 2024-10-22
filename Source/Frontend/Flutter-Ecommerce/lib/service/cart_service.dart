import 'package:dio/dio.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:shared_preferences/shared_preferences.dart';

class CartService {
  final Dio _dio = Dio();

  String getCartUrl() {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/common/cart';
  }

  String deleteCartUrl(int productId) {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/common/cart?productId=$productId';
  }

  String updateCartUrl(int productId, int newQuantity) {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/common/cart?productId=$productId&newQuantity=$newQuantity';
  }

  String createOrderUrl(String address, String payment) {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/common/order?address=$address&payment=$payment';
  }

  // Fetch cart items
  Future<List<dynamic>> fetchCart(String accessToken) async {
    try {
      final response = await _dio.get(
        getCartUrl(),
        options: Options(
          headers: {
            'Authorization': 'Bearer $accessToken',
            'Content-Type': 'application/json',
          },
        ),
      );

      if (response.statusCode == 200) {
        // Return the cartItemList directly
        return response.data['data']['cartItemList'] as List<dynamic>;
      } else {
        throw Exception('Failed to load cart');
      }
    } catch (e) {
      print('Error: $e');
      return [];
    }
  }

  // Delete a product from cart
  Future<void> deleteProductFromCart(int productId, String accessToken) async {
    try {
      final response = await _dio.delete(
        deleteCartUrl(productId),
        options: Options(
          headers: {
            'Authorization': 'Bearer $accessToken',
            'Content-Type': 'application/json',
          },
        ),
      );

      if (response.statusCode != 200) {
        throw Exception('Failed to delete product from cart');
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  // Update product quantity in cart
  Future<void> updateProductQuantity(int productId, int newQuantity, String accessToken) async {
    try {
      final response = await _dio.put(
        updateCartUrl(productId, newQuantity),
        options: Options(
          headers: {
            'Authorization': 'Bearer $accessToken',
            'Content-Type': 'application/json',
          },
        ),
      );

      if (response.statusCode != 200) {
        throw Exception('Failed to update product quantity');
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  // Create order
  Future<Map<String, dynamic>> createOrder(String addressId, String paymentMethodId, String accessToken) async {
    try {
      final response = await _dio.post(
        createOrderUrl(addressId, paymentMethodId),
        options: Options(
          headers: {
            'Authorization': 'Bearer $accessToken',
            'Content-Type': 'application/json',
          },
        ),
      );

      if (response.statusCode == 200) {
        // Trả về dữ liệu phản hồi dưới dạng Map
        return response.data; // Giả sử server trả về một đối tượng JSON
      } else {
        throw Exception('Failed to create order');
      }
    } catch (e) {
      print('Error: $e');
      return {'status': 'Error', 'message': e.toString()}; // Trả về thông điệp lỗi
    }
  }

}
