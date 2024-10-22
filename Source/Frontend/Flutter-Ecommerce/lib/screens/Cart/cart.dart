import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:intl/intl.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:smart_shop/Common/Widgets/app_button.dart';
import 'package:smart_shop/Common/Widgets/cart_tile.dart';
import 'package:smart_shop/Common/Widgets/custom_app_bar.dart';
import 'package:smart_shop/Screens/CheckOut/check_out.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';

import '../../service/address_service.dart';
import '../../service/cart_service.dart';

class Cart extends StatefulWidget {
  static const String routeName = 'cart';
  const Cart({Key? key}) : super(key: key);

  @override
  _CartState createState() => _CartState();
}

class _CartState extends State<Cart> {
  late Future<List<dynamic>> _addressesFuture;
  final AddressService _addressService = AddressService();
  late Future<List<dynamic>> _cartItemsFuture;
  final CartService _cartService = CartService();
  late String accessToken;


  @override
  void initState() {
    super.initState();
    _initialize();
    _cartItemsFuture = _fetchCartItems();
    _addressesFuture = _fetchAddresses();
  }
  Future<List<dynamic>> _fetchAddresses() async {
    final sharedPreferences = await SharedPreferences.getInstance();
    accessToken = sharedPreferences.getString('accessToken') ?? '';
    return _addressService.fetchAddress(accessToken); // Gọi hàm fetchAddress
  }
  Future<void> _initialize() async {
    final sharedPreferences = await SharedPreferences.getInstance();
    accessToken = sharedPreferences.getString('accessToken') ?? '';
    _cartItemsFuture = _fetchCartItems();
    // Ensure that the state is updated after initializing the future.
    setState(() {});
  }

  Future<List<dynamic>> _fetchCartItems() async {
    return _cartService.fetchCart(accessToken);
  }

  // Remove product from cart
  Future<void> _removeProductFromCart(int productId) async {
    await _cartService.deleteProductFromCart(productId, accessToken);
    setState(() {
      _cartItemsFuture = _fetchCartItems();
    });
  }

  // Update product quantity in cart
  Future<void> _updateProductQuantity(int productId, int newQuantity) async {
    await _cartService.updateProductQuantity(
        productId, newQuantity, accessToken);
    setState(() {
      _cartItemsFuture = _fetchCartItems();
    });
  }

  // Show modal to enter address and select payment method
  void _showCheckoutModal(BuildContext context) {
    String? _selectedPaymentMethod;
    String? _selectedAddress; // Biến để lưu địa chỉ được chọn
    List<dynamic> _addressList = []; // Danh sách các địa chỉ

    showModalBottomSheet(
      context: context,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20.0)),
      ),
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setState) {
            return Padding(
              padding: const EdgeInsets.all(20.0),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text("Chọn địa chỉ giao hàng",
                      style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                  FutureBuilder<List<dynamic>>(
                    future: _addressesFuture, // Hàm Future để tải danh sách địa chỉ
                    builder: (context, snapshot) {
                      if (snapshot.connectionState == ConnectionState.waiting) {
                        return Center(child: CircularProgressIndicator());
                      } else if (snapshot.hasError) {
                        return Center(child: Text('Có lỗi xảy ra khi tải địa chỉ'));
                      } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
                        return Center(child: Text('Không có địa chỉ nào'));
                      } else {
                        _addressList = snapshot.data!; // Lưu danh sách địa chỉ

                        return DropdownButton<String>(
                          value: _selectedAddress,
                          hint: Text('Chọn địa chỉ'),
                          isExpanded: true,
                          items: _addressList.map((address) {
                            return DropdownMenuItem<String>(
                              value: address['addressDescription'].toString(), // Mô tả địa chỉ
                              child: Text(address['addressDescription']),
                            );
                          }).toList(),
                          onChanged: (value) {
                            setState(() {
                              _selectedAddress = value; // Cập nhật địa chỉ được chọn
                            });
                          },
                        );
                      }
                    },
                  ),
                  SizedBox(height: 20),
                  Text("Chọn phương thức thanh toán",
                      style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                  ListTile(
                    title: Text("Tiền mặt"),
                    leading: Radio<String>(
                      value: "Cash",
                      groupValue: _selectedPaymentMethod,
                      onChanged: (value) {
                        setState(() {
                          _selectedPaymentMethod = value;
                        });
                      },
                    ),
                  ),
                  ListTile(
                    title: Text("VietQR"),
                    leading: Radio<String>(
                      value: "VietQR",
                      groupValue: _selectedPaymentMethod,
                      onChanged: (value) {
                        setState(() {
                          _selectedPaymentMethod = value;
                        });
                      },
                    ),
                  ),
                  SizedBox(height: 20),
                  ElevatedButton(
                    onPressed: () {
                      if (_selectedAddress != null &&
                          _selectedPaymentMethod != null) {
                        _createOrder(_selectedAddress!, _selectedPaymentMethod!); // Truyền chuỗi địa chỉ vào hàm _createOrder
                        _showSuccessSnackbar("Đặt hàng thành công!");
                        Navigator.pop(context);
                      } else {
                        _showErrorSnackbar("Vui lòng chọn địa chỉ và phương thức thanh toán");
                      }
                    },
                    child: Text("Xác nhận"),
                    style: ElevatedButton.styleFrom(
                      minimumSize: Size(double.infinity, 48),
                    ),
                  ),
                ],
              ),
            );
          },
        );
      },
    );
  }


  // Create order
  Future<void> _createOrder(String address, String paymentMethodId) async {
    final response = await _cartService.createOrder(address, paymentMethodId, accessToken);

    if (response['status'] == 'Success') {
      // Extract QR code URL
      String qrCodeUrl = response['data']['qr_code_url'];

      // Show QR code dialog or modal
      _showQRCodeDialog(qrCodeUrl);
    } else {
      _showErrorSnackbar(response['message']);
    }
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.whiteLight,
      appBar: _buildAppBar(context),
      body: _buildBody(context),
      bottomSheet: _buildBottomSheet(context),
    );
  }

  PreferredSize _buildAppBar(BuildContext context) {
    return PreferredSize(
      preferredSize: Size(double.infinity, MediaQuery
          .of(context)
          .size
          .height * .20.h),
      child: CustomAppBar(
        isHome: false,
        title: 'Giỏ hàng',
        fixedHeight: 88.0.h,
        enableSearchField: false,
        leadingIcon: Platform.isIOS ? Icons.arrow_back_ios : Icons.arrow_back,
        leadingOnTap: () {
          Navigator.pop(context);
        },
      ),
    );
  }

  Widget _buildBody(BuildContext context) {
    return FutureBuilder<List<dynamic>>(
      future: _cartItemsFuture,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return Center(child: CircularProgressIndicator());
        } else if (snapshot.hasError) {
          return Center(child: Text('Có lỗi xảy ra'));
        } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
          return Center(child: Text('Giỏ hàng trống'));
        } else {
          // Assuming snapshot.data! is already a List<dynamic>
          final cartItems = snapshot.data!; // No need for ['cartItemList'] if it's already a list

          return ListView.separated(
            itemCount: cartItems.length,
            shrinkWrap: true,
            itemBuilder: (context, index) {
              final item = cartItems[index];
              return Container(
                color: AppColors.white,
                margin: EdgeInsets.symmetric(horizontal: 20.0.w, vertical: 10.0.h),
                child: CartTile(
                  productName: item['productName'] as String, // Ensure 'productName' is a valid key and type
                  quantity: int.tryParse(item['quantity'].toString()) ?? 0, // Convert to int safely
                  price: double.tryParse(item['price'].toString()) ?? 0.0, // Convert to double safely
                  onRemove: () => _removeProductFromCart(item['productId']),
                  onUpdateQuantity: (newQuantity) => _updateProductQuantity(item['productId'], newQuantity),
                ),
              );
            },
            separatorBuilder: (context, index) => const Divider(),
          );
        }
      },
    );
  }


  Widget _buildBottomSheet(BuildContext context) {
    var size = MediaQuery.of(context).size;
    const shippingFee = 30000; // Fixed shipping fee

    return Container(
      width: double.infinity,
      decoration: BoxDecoration(
        color: AppColors.white,
        borderRadius: BorderRadius.only(
          topLeft: Radius.circular(20.0.r),
          topRight: Radius.circular(20.0.r),
        ),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            margin: const EdgeInsets.all(10.0),
            padding: const EdgeInsets.all(10.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Display shipping fee
                Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                  Text(
                    'Phí ship: ',
                    style: FontStyles.montserratBold17(),
                  ),
                  SizedBox(width: 4.0),
                  Text(
                    '30.000 đ',
                    style: FontStyles.montserratBold14().copyWith(color: Colors.green),
                  ),
            ]
                ),
                SizedBox(height: 8.0), // Space between shipping fee and total price
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text('Tổng tiền:', style: FontStyles.montserratBold19()),
                    FutureBuilder<List<dynamic>>(
                      future: _cartItemsFuture,
                      builder: (context, snapshot) {
                        if (snapshot.connectionState == ConnectionState.waiting) {
                          return Text('...');
                        } else if (snapshot.hasData) {
                          final cartItems = snapshot.data!;
                          final totalPrice = cartItems.fold<double>(0, (sum, item) {
                            final price = double.parse(item['price'].toString());
                            final quantity = int.parse(item['quantity'].toString());
                            return sum + (price * quantity);
                          });
                          // Add shipping fee to totalPrice
                          final totalPriceWithExtra = totalPrice + shippingFee;

                          // Format the total price with VND
                          return Text(
                            formatCurrency(totalPriceWithExtra),
                            style: FontStyles.montserratBold19().copyWith(color: Colors.green), // Change color as needed
                          );
                        } else {
                          return Text(formatCurrency(0), style: FontStyles.montserratBold19().copyWith(color: Colors.blue));
                        }
                      },
                    ),
                  ],
                ),
              ],
            ),
          ),
          Container(
            margin: EdgeInsets.only(bottom: 10.0.h),
            child: AppButton.button(
              text: 'Đặt hàng',
              color: AppColors.secondary,
              height: 48.h,
              width: size.width - 20.w,
              onTap: () {
                _showCheckoutModal(context); // Show modal on button tap
              },
            ),
          ),
        ],
      ),
    );
  }
  void _showQRCodeDialog(String qrCodeUrl) {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text("QR Code thanh toán"),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // Hiển thị hình ảnh QR code
              Image.memory(base64Decode(qrCodeUrl.split(',')[1])),
              SizedBox(height: 20),
              Text("Scan QR code để hoàn tất thanh toán"),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              child: Text("Đóng"),
            ),
          ],
        );
      },
    );
  }
  String formatCurrency(double price) {
    final formatter = NumberFormat.simpleCurrency(locale: 'vi_VN');
    return formatter.format(price);
  }
  void _showCustomSnackbar(String message, Color backgroundColor) {
    final overlay = Overlay.of(context);
    final overlayEntry = OverlayEntry(
      builder: (context) => Positioned(
        top: MediaQuery.of(context).viewInsets.top + 50,
        left: 0,
        right: 0,
        child: Material(
          color: Colors.transparent,
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
            decoration: BoxDecoration(
              color: backgroundColor,
              borderRadius: BorderRadius.circular(2),
            ),
            child: Text(
              message,
              style: const TextStyle(color: Colors.black),
            ),
          ),
        ),
      ),
    );

    overlay.insert(overlayEntry);
    Future.delayed(const Duration(seconds: 3), () {
      overlayEntry.remove();
    });
  }

  void _showErrorSnackbar(String message) {
    _showCustomSnackbar(message, Colors.white);
  }

  void _showSuccessSnackbar(String message) {
    _showCustomSnackbar(message, Colors.white);
  }
}

