import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:smart_shop/Screens/Onboarding/onboarding.dart';
import 'package:smart_shop/Utils/Constants/app_constants.dart';
import 'package:smart_shop/Utils/app_theme.dart';
import 'package:smart_shop/screens/BottomBar/bottom_navigator_bar.dart';
import 'package:smart_shop/service/auth_service.dart';
import 'package:jwt_decoder/jwt_decoder.dart';

import 'Screens/Home/home.dart';




Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized(); // Đảm bảo Flutter đã khởi tạo

  // Tải tệp .env
  await dotenv.load(fileName: ".env");

  AppConstants.setSystemStyling();

  // Khởi tạo AuthService
  final AuthService _authService = AuthService();

  // Lấy access token từ SharedPreferences
  final sharedPreferences = await SharedPreferences.getInstance();
  String? token = sharedPreferences.getString('accessToken');

  if (token != null && !_authService.isTokenNotExpired(token)) {
    // Nếu token hết hạn, cố gắng làm mới token
    final String? newAccessToken = await _authService.refreshAccessToken();
    if (newAccessToken != null) {
      // Cập nhật token mới
      token = newAccessToken;
      await sharedPreferences.setString('accessToken', token);
    } else {
      // Nếu không thể làm mới token, điều hướng đến trang đăng nhập
      token = null;
    }
  }

  runApp(
    ScreenUtilInit(
      designSize: const Size(375, 812),
      minTextAdapt: true,
      splitScreenMode: true,
      builder: (context, child) {
        return MaterialApp(
          debugShowCheckedModeBanner: false,
          theme: AppTheme.lightTheme,
          initialRoute: token != null ? '/' : OnBoarding.routeName,
          routes: AppConstants.appRoutes,
        );
      },
    ),
  );
}

