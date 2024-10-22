class OrderResponse {
  final String status;
  final String message;
  final Map<String, dynamic> data;

  OrderResponse({
    required this.status,
    required this.message,
    required this.data,
  });
}