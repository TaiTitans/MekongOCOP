import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:smart_shop/Common/Widgets/app_button.dart';
import 'package:smart_shop/Common/Widgets/custom_app_bar.dart';
import 'package:smart_shop/Screens/Orders/order.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';

class CheckOut extends StatelessWidget {
  static const String routeName = 'checkout';
  const CheckOut({Key? key}) : super(key: key);

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
      preferredSize:
          Size(double.infinity, MediaQuery.of(context).size.height * .20.h),
      child: CustomAppBar(
        isHome: false,
        title: 'Check Out',
        fixedHeight: 88.0.h,
        enableSearchField: false,
        leadingIcon: Icons.arrow_back,
        leadingOnTap: () {
          Navigator.pop(context);
        },
      ),
    );
  }

  Widget _buildBody(BuildContext context) {
    return SizedBox(
      height: MediaQuery.of(context).size.height * .75.h,
      child: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.max,
          children: [
            _buildAddress(context),
            SizedBox(height: 5.0.h),
            _buildDeliveryMethod(context),
            SizedBox(height: 5.0.h),
            _buildPaymentMethod(context),
          ],
        ),
      ),
    );
  }

  Widget _buildAddress(BuildContext context) {
    return Container(
      margin: const EdgeInsets.all(20.0),
      child: Column(
        children: [
          _buildTitle(context, Icons.location_on_outlined, 'Shipping Address'),
          SizedBox(height: 20.0.h),
          _buildAddressCard(context),
        ],
      ),
    );
  }

  Widget _buildDeliveryMethod(BuildContext context) {
    var screenHeight = MediaQuery.of(context).size.height;
    return Container(
      margin:  EdgeInsets.symmetric(horizontal: 20.0.h),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildTitle(
              context, Icons.directions_car_outlined, 'Delivery Method'),
          SizedBox(height: 20.0.h),
          SizedBox(
            height: screenHeight * .20.h,
            width: MediaQuery.of(context).size.width,
            child: ListView.builder(
              itemCount: 3,
              shrinkWrap: true,
              scrollDirection: Axis.horizontal,
              itemBuilder: (context, index) {
                return _buildDeliveryCard(context,
                    'assets/checkOut/icon${index + 1}.png', '18', '1-2');
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildPaymentMethod(BuildContext context) {
    return Container(
      // color: Colors.brown,
      margin: const EdgeInsets.all(20.0),
      child: Column(
        children: [
          _buildTitle(context, Icons.payment_outlined, 'Payment Method'),
          SizedBox(height: 20.0.h),
          _buildPaymentCard(context),
        ],
      ),
    );
  }

  Widget _buildAddressCard(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(20.0),
      decoration: BoxDecoration(
          color: AppColors.white, borderRadius: BorderRadius.circular(10.0.r)),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'Oleh Chabanov',
                style: FontStyles.montserratSemiBold14(),
              ),
              const Icon(Icons.arrow_forward_ios_rounded)
            ],
          ),
          const SizedBox(height: 5.0),
          Text(
            '225 Highland Aven\nSpringfield, IL 62704, USA',
            style: FontStyles.montserratRegular14(),
          )
        ],
      ),
    );
  }

  Widget _buildDeliveryCard(
      BuildContext context, String imageLink, String price, String days) {
    var screenHeight = MediaQuery.of(context).size.height;
    var screenWidth = MediaQuery.of(context).size.width;
    return Container(
      height: screenHeight * .15.h,
      width: screenWidth * .30.w,
      margin: EdgeInsets.only(right: 10.0.w),
      decoration: BoxDecoration(
        color: AppColors.white,
        borderRadius: BorderRadius.circular(10.0.r),
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          Image(
            image: AssetImage(imageLink),
          ),
          // SizedBox(height: 10.0),
          Column(
            children: [
              Text(
                '\$$price',
                style: FontStyles.montserratSemiBold14(),
              ),
              // ignore: prefer_const_constructors
              Text(
                '$days days',
                style: FontStyles.montserratRegular12(),
              ),
            ],
          )
        ],
      ),
    );
  }

  Widget _buildPaymentCard(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(10.0),
      decoration: BoxDecoration(
        color: AppColors.white,
        borderRadius: BorderRadius.circular(10.0),
      ),
      child: ListTile(
        leading: Container(
          padding: EdgeInsets.only(top: 9.0.h),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Stack(
                clipBehavior: Clip.none,
                children: const [
                  Image(
                    image: AssetImage('assets/checkOut/v2.png'),
                  ),
                  Positioned(
                    left: -20,
                    top: -3,
                    // top: 4,
                    child: Image(image: AssetImage('assets/checkOut/v1.png')),
                  ),
                  Positioned(
                      right: -20,
                      top: -3,
                      child:
                          Image(image: AssetImage('assets/checkOut/v3.png'))),
                ],
              ),
              SizedBox(height: 2.0.h),
              const Text('mastercard'),
            ],
          ),
        ),
        title: Text(
          "***** **** ***** 5678",
          style: FontStyles.montserratSemiBold14(),
        ),
        trailing: const Icon(Icons.arrow_forward_ios),
      ),
    );
  }

  Widget _buildTitle(
    BuildContext context,
    IconData icon,
    String title,
  ) {
    return Row(
      children: [
        Icon(
          icon,
          color: AppColors.primaryLight,
        ),
        SizedBox(width: 10.0.w),
        Text(
          title,
          style: FontStyles.montserratBold19(),
        )
      ],
    );
  }

  Widget _buildBottomSheet(BuildContext context) {
    var size = MediaQuery.of(context).size;
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
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('Items', style: FontStyles.montserratSemiBold14()),
                Text('\$239.98', style: FontStyles.montserratSemiBold14()),
              ],
            ),
          ),
          Container(
            margin: const EdgeInsets.all(10.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('Delivery', style: FontStyles.montserratSemiBold14()),
                Text('\$18', style: FontStyles.montserratSemiBold14()),
              ],
            ),
          ),
          Container(
            margin: const EdgeInsets.all(10.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('Total price', style: FontStyles.montserratBold19()),
                Text('\$239.98', style: FontStyles.montserratBold19()),
              ],
            ),
          ),
          Container(
            margin: EdgeInsets.only(bottom: 10.0.h),
            child: AppButton.button(
              text: 'Pay',
              color: AppColors.secondary,
              height: 48,
              width: size.width - 20.w,
              onTap: () {
                showDialog(
                    context: context,
                    builder: (context) {
                      return makeAlert(context);
                    });
                // Navigator.pushNamed(context, CheckOut.routeName);
              },
            ),
          ),
        ],
      ),
    );
  }

  static Widget _buildPopContent(BuildContext context) {
    return Column(
      children: [
        Text(
          'Success',
          style: FontStyles.montserratBold25(),
        ),
        SizedBox(height: 10.0.h),
        Text(
          'Your order will be delivered soon.',
          style: FontStyles.montserratRegular14(),
        ),
        Text(
          'It can be tracked in the "Orders" section.',
          style: FontStyles.montserratRegular14(),
        ),
        SizedBox(height: 10.0.h),
        Container(
          margin: EdgeInsets.only(bottom: 10.0.h),
          child: AppButton.button(
            text: 'Continue Shopping',
            color: AppColors.secondary,
            height: 48.h,
            width: 200.w,
            onTap: () {
              // Navigator.pushReplacementNamed(context, Main.routeName);
              Navigator.pop(context);
            },
          ),
        ),
        SizedBox(height: 15.0.h),
        GestureDetector(
          onTap: () {
            Navigator.pushReplacementNamed(context, Orders.routeName);
          },
          child: Text(
            'Go to Orders',
            style: FontStyles.montserratBold17().copyWith(color: Colors.grey),
          ),
        ),
        SizedBox(height: 10.0.h),
      ],
    );
  }

  Widget makeAlert(BuildContext context) {
    return AlertDialog(
      shape: OutlineInputBorder(
          borderRadius: BorderRadius.circular(20.0.r),
          borderSide: const BorderSide(color: Colors.transparent)),
      content: Container(
        height: 120.0.h,
        width: 50.0.w,
        decoration: BoxDecoration(
            color: AppColors.primaryDark,
            gradient: const LinearGradient(
                colors: [AppColors.primaryDark, AppColors.primaryLight],
                begin: Alignment.bottomLeft,
                end: Alignment.topRight,
                stops: [0, 1]),
            borderRadius: BorderRadius.only(
                bottomLeft: Radius.circular(120.0.r),
                bottomRight: Radius.circular(120.0.r))),
        child: Center(
          child: Image(
            image: const AssetImage('assets/checkOut/check.png'),
            height: 70.0.h,
          ),
        ),
      ),
      contentPadding: EdgeInsets.only(left: 20.0.w, right: 20.0.w),
      // actionsAlignment: MainAxisAlignment.center,
      actions: [
        _buildPopContent(context),
      ],
    );
  }
}
