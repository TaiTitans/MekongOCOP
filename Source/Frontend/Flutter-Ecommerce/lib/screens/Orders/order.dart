import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:intl/intl.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:smart_shop/Common/Widgets/custom_app_bar.dart';
import 'package:smart_shop/Common/Widgets/item_widget.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';

import '../../service/order_service.dart';

class Orders extends StatefulWidget {
  static const String routeName = 'orders';
  const Orders({Key? key}) : super(key: key);

  @override
  _OrdersState createState() => _OrdersState();
}

class _OrdersState extends State<Orders> {
  final OrderService _orderService = OrderService();
  Future<Map<String, dynamic>>? _orderFuture;

  @override
  void initState() {
    super.initState();
    _fetchOrders();
  }

  void _fetchOrders() async {
    final sharedPreferences = await SharedPreferences.getInstance();
    final accessToken = sharedPreferences.getString('accessToken') ?? '';

    if (accessToken.isNotEmpty) {
      setState(() {
        _orderFuture = _orderService.getOrder(accessToken);
      });
    } else {
      setState(() {
        _orderFuture = Future.value({'status': 'Error', 'message': 'No access token found', 'data': []});
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.whiteLight,
      appBar: _buildAppBar(context),
      body: _buildBody(context),
    );
  }

  PreferredSize _buildAppBar(BuildContext context) {
    return PreferredSize(
      preferredSize: Size(double.infinity, MediaQuery.of(context).size.height * .20),
      child: CustomAppBar(
        isHome: false,
        title: 'Đơn hàng',
        fixedHeight: 88.0,
        enableSearchField: false,
        leadingIcon: Icons.arrow_back,
        leadingOnTap: () {
          Navigator.pop(context);
        },
      ),
    );
  }

  Widget _buildBody(BuildContext context) {
    return Padding(
      padding: EdgeInsets.symmetric(vertical: 10.h),
      child: FutureBuilder<Map<String, dynamic>>(
        future: _orderFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}', style: TextStyle(color: Colors.red)));
          } else if (!snapshot.hasData || snapshot.data!['data'].isEmpty) {
            return Center(child: Text('Không có đơn hàng nào', style: TextStyle(fontSize: 18.sp)));
          } else {
            var orders = snapshot.data!['data'];
            return ListView.builder(
              itemCount: orders.length,
              itemBuilder: (context, index) {
                return _buildOrdersCard(orders[index]);
              },
            );
          }
        },
      ),
    );
  }

  Widget _buildOrdersCard(Map<String, dynamic> order) {
    return Card(
      elevation: 4,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(15.r),
      ),
      margin: EdgeInsets.symmetric(horizontal: 20.w, vertical: 10.h),
      child: Padding(
        padding: EdgeInsets.all(16.r),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  'Đơn hàng #${order['order_id']}',
                  style: TextStyle(fontSize: 18.sp, fontWeight: FontWeight.bold),
                ),
                _buildStatusText(order['status']),
              ],
            ),
            SizedBox(height: 10.h),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  'Tổng: ${formatCurrency(order['total_price'])}',
                  style: TextStyle(fontSize: 16.sp, fontWeight: FontWeight.w600, color: Colors.green),
                ),
                Text(
                  'Ngày: ${_formatDate(order['created_at'])}',
                  style: TextStyle(fontSize: 14.sp, color: Colors.grey),
                ),
              ],
            ),
            SizedBox(height: 10.h),
            Text(
              'Phương thức: ${order['payment'] == 'Cash' ? 'Tiền mặt' : 'Chuyển khoản'}',
              style: TextStyle(fontSize: 14.sp),
            ),
            Text(
              'Ship: 30.000 đ',
              style: TextStyle(fontSize: 14.sp),
            ),
            SizedBox(height: 10.h),
            _buildOrderItemsList(order['items']),
            if (order['status'] == 'Request')
              Align(
                alignment: Alignment.centerRight,
                child: ElevatedButton(
                  onPressed: () => _cancelOrder(order['order_id']),
                  child: Text('Huỷ đơn hàng', style: TextStyle(color: Colors.black),),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.grey,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(30.r),
                    ),
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildStatusText(String status) {
    Color textColor;
    String statusText = _mapStatus(status);
    switch (status) {
      case 'Request':
        textColor = Colors.grey;
        break;
      case 'Pending':
        textColor = Colors.orange;
        break;
      case 'Success':
        textColor = Colors.green;
        break;
      case 'Cancel_Request':
        textColor = Colors.red;
        break;
      default:
        textColor = Colors.grey;
    }

    return Text(
      statusText,
      style: TextStyle(
        color: textColor,
        fontSize: 12.sp,
        fontWeight: FontWeight.bold,
      ),
    );
  }

  String _mapStatus(String status) {
    switch (status) {
      case 'Request':
        return "Đang chờ xử lí";
      case 'Pending':
        return "Đang giao hàng";
      case 'Success':
        return "Giao hàng thành công";
      case 'Cancel_Request':
        return "Đơn hàng bị huỷ";
      default:
        return "Không xác định";
    }
  }

  Future<void> _cancelOrder(int orderId) async {
    final sharedPreferences = await SharedPreferences.getInstance();
    final accessToken = sharedPreferences.getString('accessToken') ?? '';

    if (accessToken.isNotEmpty) {
      try {
        await _orderService.cancelOrder(accessToken, orderId);
        _fetchOrders();
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Đã huỷ đơn hàng thành công')),
        );
      } catch (e) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Lỗi khi huỷ đơn hàng: $e')),
        );
      }
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Không tìm thấy token đăng nhập')),
      );
    }
  }

  Widget _buildOrderItemsList(List<dynamic> items) {
    return SizedBox(
      height: 150.h,
      child: FutureBuilder<List<Map<String, dynamic>>>(
        future: _fetchProductDetails(items),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Lỗi: ${snapshot.error}', style: TextStyle(color: Colors.red)));
          } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return Center(child: Text('Không có sản phẩm', style: TextStyle(fontSize: 16.sp)));
          } else {
            var products = snapshot.data!;
            return ListView.builder(
              itemCount: products.length,
              itemBuilder: (context, index) {
                return ListTile(
                  contentPadding: EdgeInsets.symmetric(vertical: 4.h),
                  title: Text(
                    products[index]['productName'],
                    style: TextStyle(fontSize: 14.sp, fontWeight: FontWeight.w500),
                    overflow: TextOverflow.ellipsis,
                  ),
                  subtitle: Text(
                    'Số lượng: ${items[index]['quantity']}',
                    style: TextStyle(fontSize: 12.sp),
                  ),
                  trailing: Text(
                    formatCurrency(items[index]['price']),
                    style: TextStyle(fontSize: 14.sp, fontWeight: FontWeight.w600),
                  ),
                );
              },
            );
          }
        },
      ),
    );
  }

  String formatCurrency(double price) {
    final formatter = NumberFormat.currency(locale: 'vi_VN', symbol: 'đ');
    return formatter.format(price);
  }

  String _formatDate(int timestamp) {
    DateTime date = DateTime.fromMillisecondsSinceEpoch(timestamp);
    return DateFormat('dd/MM/yyyy').format(date);
  }

  Future<List<Map<String, dynamic>>> _fetchProductDetails(List<dynamic> items) async {
    final sharedPreferences = await SharedPreferences.getInstance();
    final accessToken = sharedPreferences.getString('accessToken') ?? '';

    List<Map<String, dynamic>> productDetails = [];

    for (var item in items) {
      var product = await _orderService.getProduct(accessToken, item['productId']);
      productDetails.add(product['data']);
    }

    return productDetails;
  }
}
