import 'package:dio/dio.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class AddressService {
  final Dio _dio = Dio();

  String getAddressUrl() {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/common/address';
  }

  String addAddressUrl() {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/common/address';
  }
  String deleteAddressUrl(int addressId) {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/common/address/$addressId';
  }

  Future<List<dynamic>> fetchAddress(String accessToken) async {
    try {
      final response = await _dio.get(
        getAddressUrl(),
        options: Options(
          headers: {
            'Authorization': 'Bearer $accessToken',
            'Content-Type': 'application/json',
          },
        ),
      );

      if (response.statusCode == 200 && response.data['status'] == 'Success') {
        return response.data['data'] as List<dynamic>;
      } else {
        throw Exception('Failed to load addresses');
      }
    } catch (e) {
      print('Error: $e');
      return [];
    }
  }

  Future<void> addAddress(String addressDescription, String accessToken) async {
    try {
      final response = await _dio.post(
        addAddressUrl(),
        options: Options(
          headers: {
            'Authorization': 'Bearer $accessToken',
            'Content-Type': 'application/json',
          },
        ),
        data: {
          'addressDescription': addressDescription,
        }, // Gửi addressDescription trong body
      );

      if (response.statusCode != 200) {
        throw Exception('Failed to create order');
      }
    } catch (e) {
      print('Error: $e');
    }
  }
  Future<void> deleteAddress(int addressId, String accessToken) async {
    try {
      final response = await _dio.delete(
        deleteAddressUrl(addressId),
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

}