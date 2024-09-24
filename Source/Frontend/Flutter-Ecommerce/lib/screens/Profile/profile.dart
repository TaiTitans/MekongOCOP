import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:smart_shop/Common/Widgets/gradient_header.dart';
import 'package:smart_shop/Screens/Onboarding/onboarding.dart';
import 'package:smart_shop/Screens/Orders/order.dart';
import 'package:smart_shop/Screens/PrivacyPolicy/privacy_policy.dart';
import 'package:smart_shop/Screens/Settings/settings.dart';
import 'package:smart_shop/Screens/ShippingAddress/shipping_address.dart';
import 'package:smart_shop/Screens/SignUp/sign_up.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';

class Profile extends StatelessWidget {
  static const String routeName = 'profile';
  const Profile({Key? key}) : super(key: key);

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
                text: 'Oleh Chabanov',
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
                      Navigator.pushReplacementNamed(context, SignUp.routeName);
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
          _buildProfileTile(Icons.location_on_outlined, 'Shipping Address', () {
            Navigator.pushNamed(context, ShippingAddress.routeName);
          }),
          _buildProfileTile(Icons.payment_rounded, 'Payment Method', () {}),
          _buildProfileTile(Icons.border_all, 'Orders', () {
            Navigator.pushNamed(context, Orders.routeName);
          }),
          _buildProfileTile(Icons.settings, 'Settings', () {
            Navigator.pushNamed(context, Settings.routeName);
          }),
          _buildProfileTile(Icons.login_outlined, 'Logout', () {
            Navigator.pushReplacementNamed(context, OnBoarding.routeName);
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
}
