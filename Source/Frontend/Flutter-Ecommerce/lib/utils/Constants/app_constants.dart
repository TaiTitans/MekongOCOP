import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:smart_shop/Screens/Cart/cart.dart';
import 'package:smart_shop/Screens/Catalogue/catalogue.dart';
import 'package:smart_shop/Screens/CheckOut/check_out.dart';
import 'package:smart_shop/Screens/Favorite/favorite.dart';
import 'package:smart_shop/Screens/Filter/filter.dart';
import 'package:smart_shop/Screens/Home/home.dart';
import 'package:smart_shop/Screens/Items/items.dart';
import 'package:smart_shop/Screens/Login/phone_screen.dart';
import 'package:smart_shop/Screens/Login/verification_screen.dart';
import 'package:smart_shop/screens/BottomBar/bottom_navigator_bar.dart';
import 'package:smart_shop/Screens/Notifications/notifications.dart';
import 'package:smart_shop/Screens/Onboarding/onboarding.dart';
import 'package:smart_shop/Screens/Orders/order.dart';
import 'package:smart_shop/Screens/PrivacyPolicy/privacy_policy.dart';
import 'package:smart_shop/Screens/Product/product.dart';
import 'package:smart_shop/Screens/Profile/profile.dart';
import 'package:smart_shop/Screens/Settings/settings.dart';
import 'package:smart_shop/Screens/ShippingAddress/shipping_address.dart';
import 'package:smart_shop/Screens/SignUp/sign_up.dart';
import 'package:smart_shop/screens/Product/product_catalogue.dart';
import 'package:smart_shop/screens/SignIn/sign_in.dart';
import 'package:smart_shop/screens/Login/forgot_password.dart';
import 'package:smart_shop/screens/chat/chatrealtime.dart';
import 'package:smart_shop/screens/search/search_screen.dart';
import '../../screens/Catalogue/province_catalogue.dart';
import '../../screens/Chatbot/chatbot.dart';
class AppConstants {
  static Map<String, Widget Function(dynamic)> appRoutes = {
    '/': (_) => const BottomNavigatorBar(),
    Login.routeName: (_) => const Login(),
    Verification.routeName: (context) {
      final String email = ModalRoute.of(context)!.settings.arguments as String;
      return Verification(email: email);
    },
    Home.routeName: (_) => const Home(),
    BottomNavigatorBar.routeName: (_) => const BottomNavigatorBar(),
    Catalogue.routeName: (_) => const Catalogue(),
    Province.routeName: (_)=> const Province(),
    Items.routeName: (_) => const Items(),
    Filter.routeName: (_) => const Filter(),
    Product.routeName: (context) {
      final int productId = ModalRoute.of(context)!.settings.arguments as int;
      return Product(productId: productId);
    },
    ProductListScreen.routeName: (context) {
      final List<dynamic> products = ModalRoute.of(context)!.settings.arguments as List<dynamic>;
      return ProductListScreen(products: products);
    },
    Favorite.routeName: (_) => const Favorite(),
    Profile.routeName: (_) => const Profile(),
    Cart.routeName: (_) => const Cart(),
    CheckOut.routeName: (_) => const CheckOut(),
    SignUp.routeName: (_) => const SignUp(),
    Settings.routeName: (_) => const Settings(),
    Orders.routeName: (_) => const Orders(),
    PrivacyPolicy.routeName: (_) => const PrivacyPolicy(),
    OnBoarding.routeName: (_) => const OnBoarding(),
    NotificationScreen.routeName: (_) => const NotificationScreen(),
    ShippingAddress.routeName: (_) => const ShippingAddress(),
    SignIn.routeName: (_) => const SignIn(),
    ChatbotApp.routeName: (_) => ChatbotApp(),
    SearchScreen.routeName: (_) => SearchScreen(),
    ChatRealTime.routeName: (context) {
      final int storeId = ModalRoute.of(context)!.settings.arguments as int; // Get storeId from arguments
      return ChatRealTime(storeId: storeId); // Pass the storeId to ChatRealTime
    },
    ForgotPasswordScreen.routeName: (_) => const ForgotPasswordScreen(),
  };

  static setSystemStyling() {
    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
    ]);
    SystemChrome.setSystemUIOverlayStyle(
      SystemUiOverlayStyle.light,
    );
    SystemChrome.setSystemUIOverlayStyle(
      const SystemUiOverlayStyle(statusBarColor: Colors.transparent),
    );
  }

  static const privacyPolicyTxt =
      'Mekong OCOP .Sàn giao dịch sản phẩm hợp tác OCOP vùng Đồng bằng sông Cửu Long.';
}
