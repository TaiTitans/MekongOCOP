import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:smart_shop/Screens/Onboarding/onboarding.dart';
import 'package:smart_shop/Utils/Constants/app_constants.dart';
import 'package:smart_shop/Utils/app_theme.dart';
import 'package:smart_shop/service/auth_service.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Tải biến môi trường từ file .env
  await dotenv.load(fileName: ".env");

  // Khởi tạo AuthService và kiểm tra token khi bắt đầu ứng dụng
  final AuthService _authService = AuthService();
  await _authService.init();

  runApp(MyApp(authService: _authService));
}


class MyApp extends StatefulWidget {
  final AuthService authService;
  const MyApp({Key? key, required this.authService}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  late Timer _timer;

  @override
  void initState() {
    super.initState();
    // Đặt Timer để tự động làm mới token mỗi 59 phút
    _timer = Timer.periodic(Duration(minutes: 59), (timer) async {
      await widget.authService.refreshAccessTokenIfNeeded();
    });
  }

  @override
  void dispose() {
    _timer.cancel(); // Hủy timer khi ứng dụng bị đóng
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return ScreenUtilInit(
      designSize: Size(360, 690), // Điều chỉnh kích thước này theo thiết kế gốc của bạn
      minTextAdapt: true,
      splitScreenMode: true,
      builder: (context, child) {
        return MaterialApp(
          debugShowCheckedModeBanner: false,
          theme: AppTheme.lightTheme,
          initialRoute: '/',
          routes: AppConstants.appRoutes,
        );
      },
    );
  }
}
