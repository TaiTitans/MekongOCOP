import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:smart_shop/Common/Widgets/custom_app_bar.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';

import '../../service/address_service.dart';

class ShippingAddress extends StatefulWidget {
  static const String routeName = 'shippingAddress';
  const ShippingAddress({Key? key}) : super(key: key);

  @override
  _ShippingAddressState createState() => _ShippingAddressState();
}

class _ShippingAddressState extends State<ShippingAddress> {
  final AddressService _addressService = AddressService();
  String accessToken = '';
  List<dynamic> addresses = [];

  @override
  void initState() {
    super.initState();
    _loadAccessTokenAndFetchAddresses();
  }

  Future<void> _loadAccessTokenAndFetchAddresses() async {
    // Lấy accessToken từ SharedPreferences
    final sharedPreferences = await SharedPreferences.getInstance();
    final token = sharedPreferences.getString('accessToken') ?? '';

    if (token.isNotEmpty) {
      setState(() {
        accessToken = token;
      });
      _fetchAddresses(); // Gọi API lấy danh sách địa chỉ
    } else {
      print('Access token not found');
    }
  }

  Future<void> _fetchAddresses() async {
    try {
      // Fetch addresses from API
      List<dynamic> fetchedAddresses = await _addressService.fetchAddress(accessToken);
      setState(() {
        addresses = fetchedAddresses;
      });
    } catch (e) {
      print('Failed to fetch addresses: $e');
    }
  }

  Future<void> _addAddress(String addressDescription) async {
    try {
      await _addressService.addAddress(addressDescription, accessToken);
      _fetchAddresses(); // Refresh address list after adding
    } catch (e) {
      print('Failed to add address: $e');
    }
  }

  Future<void> _deleteAddress(int addressId) async {
    try {
      await _addressService.deleteAddress(addressId, accessToken);
      _fetchAddresses(); // Refresh address list after deleting
    } catch (e) {
      print('Failed to delete address: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.whiteLight,
      appBar: _buildAppBar(context),
      body: _buildBody(context),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          String? newAddress = await _showAddAddressDialog(context);
          if (newAddress != null && newAddress.isNotEmpty) {
            _addAddress(newAddress);
          }
        },
        child: Icon(Icons.add),
      ),
    );
  }


  PreferredSize _buildAppBar(BuildContext context) {
    return PreferredSize(
      preferredSize: Size(double.infinity, MediaQuery.of(context).size.height * .20),
      child: CustomAppBar(
        isHome: false,
        title: 'Địa chỉ',
        fixedHeight: 120.0,
        enableSearchField: false,
        leadingIcon: Icons.arrow_back,
        leadingOnTap: () {
          Navigator.pop(context);
        },
      ),
    );
  }

  Widget _buildBody(BuildContext context) {
    return Column(
      children: [
        _buildAddressList(context),
      ],
    );
  }

  Widget _buildAddressList(BuildContext context) {
    if (addresses.isEmpty) {
      return Center(child: Text('No addresses available.'));
    }

    return ListView.builder(
      shrinkWrap: true,
      itemCount: addresses.length,
      itemBuilder: (context, index) {
        final address = addresses[index];
        return _buildAddressCard(context, address);
      },
    );
  }

  Widget _buildAddressCard(BuildContext context, dynamic address) {
    return Container(
      margin: const EdgeInsets.symmetric(vertical: 10.0, horizontal: 20.0),  // Khoảng cách giữa các card
      padding: const EdgeInsets.all(20.0),
      decoration: BoxDecoration(
        color: AppColors.white,
        borderRadius: BorderRadius.circular(10.0),  // Viền bo tròn
        boxShadow: [  // Đổ bóng
          BoxShadow(
            color: Colors.grey.withOpacity(0.5),  // Màu của bóng
            spreadRadius: 1,  // Độ lan của bóng
            blurRadius: 5,  // Độ mờ của bóng
            offset: Offset(0, 3),  // Vị trí của bóng (x, y)
          ),
        ],
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'Địa chỉ',
                style: FontStyles.montserratBold17().copyWith(fontSize: 14.0),
              ),
              IconButton(
                icon: Icon(Icons.delete),
                onPressed: () => _deleteAddress(address['addressId']),
              ),
            ],
          ),
          const SizedBox(height: 5.0),
          Text(address['addressDescription']),
        ],
      ),
    );
  }

  Future<String?> _showAddAddressDialog(BuildContext context) async {
    String addressDescription = '';
    return showDialog<String>(
      context: context,
      builder: (BuildContext context) {
        return Dialog(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(15.0), // Bo tròn viền
          ),
          child: Container(
            width: MediaQuery.of(context).size.width * 0.8, // Điều chỉnh chiều rộng (80% chiều rộng màn hình)
            padding: EdgeInsets.all(20.0), // Thêm padding để nội dung có không gian
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text(
                  'Thêm địa chỉ mới',
                  style: TextStyle(
                    fontWeight: FontWeight.bold,
                    fontSize: 20.0,
                    color: AppColors.primaryDark, // Màu tiêu đề
                  ),
                ),
                const SizedBox(height: 20.0), // Khoảng cách giữa tiêu đề và TextField
                TextField(
                  onChanged: (value) {
                    addressDescription = value;
                  },
                  decoration: InputDecoration(
                    hintText: "Nhập địa chỉ",
                    hintStyle: TextStyle(color: Colors.grey),
                    filled: true,
                    fillColor: Colors.grey[200], // Nền của TextField
                    contentPadding: EdgeInsets.symmetric(
                      vertical: 12.0,
                      horizontal: 15.0,
                    ),
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(10.0),
                      borderSide: BorderSide.none,
                    ),
                  ),
                ),
                const SizedBox(height: 20.0), // Khoảng cách giữa TextField và nút
                Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    TextButton(
                      onPressed: () {
                        Navigator.of(context).pop();
                      },
                      child: Text(
                        'Huỷ',
                        style: TextStyle(
                          color: AppColors.primaryDark,
                          fontSize: 16.0,
                        ),
                      ),
                    ),
                    const SizedBox(width: 10.0),
                    ElevatedButton(
                      onPressed: () {
                        Navigator.of(context).pop(addressDescription);
                      },
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AppColors.primaryLight, // Màu nút "Thêm"
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(10.0),
                        ),
                        padding: EdgeInsets.symmetric(vertical: 12.0, horizontal: 20.0),
                      ),
                      child: Text(
                        'Thêm',
                        style: TextStyle(
                          fontSize: 16.0,
                          fontWeight: FontWeight.bold,
                          color: Colors.white,
                        ),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        );
      },
    );
  }


}
