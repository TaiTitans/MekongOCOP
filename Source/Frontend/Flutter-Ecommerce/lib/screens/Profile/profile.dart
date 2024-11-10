import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:intl/intl.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:smart_shop/Common/Widgets/gradient_header.dart';
import 'package:smart_shop/Screens/Onboarding/onboarding.dart';
import 'package:smart_shop/Screens/Orders/order.dart';
import 'package:smart_shop/Screens/PrivacyPolicy/privacy_policy.dart';
import 'package:smart_shop/Screens/Settings/settings.dart';
import 'package:smart_shop/Screens/ShippingAddress/shipping_address.dart';
import 'package:smart_shop/Screens/SignUp/sign_up.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';
import 'package:smart_shop/screens/chat/listchat.dart';
import 'package:smart_shop/service/auth_service.dart';


class Profile extends StatefulWidget {
  static const String routeName = 'profile';
  const Profile({Key? key}) : super(key: key);

  @override
  _ProfileState createState() => _ProfileState();
}

class _ProfileState extends State<Profile> {
  // Controllers for form fields
  final TextEditingController _fullNameController = TextEditingController();
  final TextEditingController _birthdayController = TextEditingController();
  final TextEditingController _bioController = TextEditingController();

  // Gender options
  String? _selectedGender;
  final List<Map<String, String>> _genderOptions = [
    {"M": "Nam"},
    {"F": "Nữ"}
  ];

  bool isUpdating = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _buildBody(context),
      resizeToAvoidBottomInset: false,
    );
  }

  Widget _buildBody(BuildContext context) {
    var screenHeight = MediaQuery.of(context).size.height;
    return SingleChildScrollView(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Stack(
            children: [
              AppHeaderGradient(
                isProfile: true,
                fixedHeight: screenHeight * .24.h,
                text: '',
              ),
              Positioned(
                top: screenHeight * .14.h,
                right: 20.0.w,
                child: Container(
                  padding: const EdgeInsets.all(10.0),
                  decoration: BoxDecoration(
                    color: AppColors.white,
                    borderRadius: BorderRadius.circular(20.0.r),
                  ),
                  child: GestureDetector(
                    onTap: () {
                      _showEditProfileModal(context);
                    },
                    child: const Icon(
                      Icons.edit_outlined,
                      color: AppColors.primaryLight,
                    ),
                  ),
                ),
              ),
            ],
          ),
          SizedBox(height: 10.0.h),
          _buildProfileTile(Icons.location_on_outlined, 'Địa chỉ giao hàng', () {
            Navigator.pushNamed(context, ShippingAddress.routeName);
          }),
          _buildListChat(Icons.mark_unread_chat_alt_outlined, 'Danh sách trò chuyện', () {
            Navigator.pushNamed(context, ChatListScreen.routeName);
          }),
          _buildProfileTile(Icons.border_all, 'Đơn hàng', () {
            Navigator.pushNamed(context, Orders.routeName);
          }),
          _buildProfileTile(Icons.settings, 'Cài đặt', () {
          }),
          _buildProfileTile(Icons.login_outlined, 'Đăng xuất', () async {
            await _logout(context);
          }),
          _buildPrivacy(context),
        ],
      ),
    );
  }

  Widget _buildProfileTile(IconData icon, String title, Function() onTap) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        margin: EdgeInsets.symmetric(horizontal: 20.0.w, vertical: 10.0.h),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(10.0.r),
          color: AppColors.white,
        ),
        child: ListTile(
          leading: Icon(
            icon,
            color: AppColors.primaryLight,
          ),
          title: Text(
            title,
            style: FontStyles.montserratSemiBold17(),
          ),
        ),
      ),
    );
  }
  Widget _buildListChat(IconData icon, String title, Function() onTap) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        margin: EdgeInsets.symmetric(horizontal: 20.0.w, vertical: 10.0.h),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(10.0.r),
          color: AppColors.white,
        ),
        child: ListTile(
          leading: Icon(
            icon,
            color: AppColors.primaryLight,
          ),
          title: Text(
            title,
            style: FontStyles.montserratSemiBold17(),
          ),
        ),
      ),
    );
  }

  Widget _buildPrivacy(BuildContext context) {
    return GestureDetector(
      onTap: () {
        Navigator.pushNamed(context, PrivacyPolicy.routeName);
      },
      child: Padding(
        padding: EdgeInsets.only(left: 20.0.w, top: 10.0.h, bottom: 100.0.h),
        child: Text(
          'Privacy Policy',
          style: FontStyles.montserratRegular12().copyWith(
            decoration: TextDecoration.underline,
            color: AppColors.textLightColor,
          ),
        ),
      ),
    );
  }

  void _showEditProfileModal(BuildContext context) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (BuildContext context) {
        return Padding(
          padding: EdgeInsets.only(
            left: 20.0.w,
            right: 20.0.w,
            top: 20.0.h,
            bottom: MediaQuery.of(context).viewInsets.bottom,
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                'Chỉnh sửa thông tin',
                style: FontStyles.montserratBold19(),
              ),
              SizedBox(height: 20.0.h),
              TextField(
                controller: _fullNameController,
                decoration: InputDecoration(labelText: 'Họ và tên'),
              ),
              // Birthday Date Picker
              TextFormField(
                controller: _birthdayController,
                decoration: InputDecoration(
                  labelText: 'Sinh nhật',
                ),
                readOnly: true, // Make it readonly so users tap to pick a date
                onTap: () async {
                  DateTime? pickedDate = await showDatePicker(
                    context: context,
                    initialDate: DateTime.now(),
                    firstDate: DateTime(1900), // Set a reasonable date range
                    lastDate: DateTime.now(),
                  );

                  if (pickedDate != null) {
                    String formattedDate =
                    DateFormat('yyyy-MM-dd').format(pickedDate);
                    setState(() {
                      _birthdayController.text = formattedDate; // Set the date in the TextField
                    });
                  }
                },
              ),
              // Gender Dropdown
              DropdownButtonFormField<String>(
                value: _selectedGender,
                decoration: InputDecoration(labelText: 'Giới tính'),
                items: _genderOptions.map((gender) {
                  String value = gender.keys.first;
                  String displayText = gender.values.first;
                  return DropdownMenuItem(
                    value: value,
                    child: Text(displayText),
                  );
                }).toList(),
                onChanged: (newValue) {
                  setState(() {
                    _selectedGender = newValue!;
                  });
                },
              ),
              TextField(
                controller: _bioController,
                decoration: InputDecoration(labelText: 'Giới thiệu'),
              ),
              SizedBox(height: 20.0.h),
              ElevatedButton(
                onPressed: () {
                  _updateUserProfile();
                },
                child: isUpdating
                    ? CircularProgressIndicator(color: AppColors.white)
                    : Text('Cập nhật'),
              ),
            ],
          ),
        );
      },
    );
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
  Future<void> _updateUserProfile() async {
    setState(() {
      isUpdating = true;
    });

    try {
      final profileService = AuthService();

      await profileService.updateUserProfile(
        fullName: _fullNameController.text,
        birthday: _birthdayController.text,
        sex: _selectedGender ?? "M",
        bio: _bioController.text,
      );

      Navigator.pop(context);

_showSuccessSnackbar("Cập nhật thành công");
      // Optionally, show a success message or reload profile info
    } catch (error) {
      _showErrorSnackbar("Cập nhật thất bại");
      print('Error updating profile: $error');
    } finally {
      setState(() {
        isUpdating = false;
      });
    }
  }

  Future<void> _logout(BuildContext context) async {
    // Xóa access token và refresh token từ SharedPreferences
    final sharedPreferences = await SharedPreferences.getInstance();
    await sharedPreferences.remove('accessToken');
    await sharedPreferences.remove('refreshToken'); // Nếu bạn lưu refresh token

    // Điều hướng đến màn hình đăng nhập
    Navigator.pushReplacementNamed(context, OnBoarding.routeName);
  }


}