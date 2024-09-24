import 'package:flutter/material.dart';
import 'package:smart_shop/Common/Widgets/app_button.dart';
import 'package:smart_shop/Common/Widgets/gradient_header.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';
import 'package:smart_shop/screens/SignIn/sign_in.dart';
import 'package:smart_shop/service/auth_service.dart';

class Verification extends StatefulWidget {
  const Verification({
    Key? key,
    required this.email,
  }) : super(key: key);

  static const String routeName = 'verification';
  final String email;

  @override
  _VerificationState createState() => _VerificationState();
}

class _VerificationState extends State<Verification> {
  final AuthService _authService = AuthService();
  bool _isLoading = false;
  String? _errorMessage;

  // Controllers cho username, password và OTP
  final TextEditingController _usernameController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _otpController = TextEditingController();


  Future<void> _sendOtp(String email) async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    final messageResponse = await _authService.sendOTPSignUp(email);

    setState(() {
      _isLoading = false;
    });

    if (messageResponse == null) {
      _showSuccessSnackbar('Đã gửi lại OTP!');
    } else if (messageResponse == 'Email is already registered') {
      setState(() {
        _showErrorSnackbar('Email đã đăng ký');
      });}else if (messageResponse == 'Username already registered') {
      setState(() {
        _showErrorSnackbar('Tên đăng nhập đã được đăng ký');
      });
    } else {
      setState(() {
        _showErrorSnackbar('Gửi không thành công. Vui lòng thử lại sau 5p.');

      });
    }
  }

  Future<void> _registerAccount() async{
    final String username = _usernameController.text;
    final String password = _passwordController.text;
    final String email = widget.email;
    final String otpString = _otpController.text;
    final int otp;

    // Kiểm tra độ dài và chuyển đổi
    if (otpString.length != 6 || !RegExp(r'^\d+$').hasMatch(otpString)) {
      setState(() {
        _errorMessage = 'Mã OTP phải có 6 ký tự và là số.';
      });
      return;
    } else {
      otp = int.parse(otpString); // Chuyển đổi từ String sang int
    }
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    final response = await _authService.registerAccount(otp, username, password, email);
    setState(() {
      _isLoading = false;
    });

    if(response == null){
      _showSuccessSnackbar('Đăng ký tài khoản thành công!');
      Navigator.pushReplacementNamed(context, SignIn.routeName);
    }else if(response == 'Invalid OTP') {
      setState(() {
        _showErrorSnackbar('Mã OTP không chính xác');
      });
    }else{
      setState(() {
        _showErrorSnackbar(response);
      });
    }


  }

  @override
  Widget build(BuildContext context) {
    final String email = widget.email;

    return Scaffold(body: _buildBody(context, email));
  }

  Widget _buildBody(BuildContext context, String email) {
    return SingleChildScrollView(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeader(),
          _buildWidget(context, email),
          SizedBox(height: 30.0),
          _buildContinueButton(context, email),
          SizedBox(height: 30.0),
          _buildResendCodeButton(context, email),
        ],
      ),
    );
  }

  Widget _buildHeader() {
    return const AppHeaderGradient(
      text: 'Đăng ký tài khoản',
      isProfile: false,
    );
  }

  Widget _buildWidget(BuildContext context, String email) {
    return Container(
      margin: EdgeInsets.symmetric(horizontal: 20.0, vertical: 20.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Nhập thông tin',
            style: FontStyles.montserratRegular17(),
          ),
          SizedBox(height: 20.0),

          // Trường nhập username
      TextField(
        controller: _usernameController,
        decoration: InputDecoration(
          labelText: 'Tên đăng nhập',
          border: OutlineInputBorder(

          borderSide: BorderSide(color: Colors.grey),
        ),
      ),
    ),
    SizedBox(height: 20.0),

// Trường nhập password
    TextField(
    controller: _passwordController,
    decoration: InputDecoration(
    labelText: 'Mật khẩu',
    border: OutlineInputBorder(
    borderSide: BorderSide(color: Colors.grey),
    ),
    ),
    obscureText: true, // Ẩn văn bản khi nhập
    ),
          SizedBox(height: 20.0),

          // Hiển thị email đã nhập
          Text(
            'Email: $email',
            style: FontStyles.montserratBold14(),
          ),
          SizedBox(height: 30.0),
          // Trường nhập OTP
          _buildOTPField(),
          if (_errorMessage != null)
            Padding(
              padding: const EdgeInsets.only(top: 20.0),
              child: Text(
                _errorMessage!,
                style: TextStyle(color: Colors.red),
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildOTPField() {
    return TextField(
      controller: _otpController,
      keyboardType: TextInputType.number,
      decoration: InputDecoration(
        labelText: 'Nhập mã OTP',
        border: OutlineInputBorder(),
      ),
      maxLength: 6, // Giới hạn ký tự nhập
    );
  }


  Widget _buildContinueButton(BuildContext context, String email) {
    return Center(
      child: AppButton.button(
        width: 327.0,
        height: 64.0,
        color: AppColors.secondary,
        text: 'Xác thực',
        onTap: () async {
          await _registerAccount(); // Gọi _sendOtp với email
        },
      ),
    );
  }

  Widget _buildResendCodeButton(BuildContext context, String email) {
    return Center(
      child: TextButton(
        onPressed: () async {
          await _sendOtp(email); // Gọi lại _sendOtp khi nhấn "Gửi lại OTP"
        },
        child: Text(
          'Gửi lại OTP',
          style: FontStyles.montserratBold17().copyWith(
            color: Colors.grey,
            decoration: TextDecoration.underline,
          ),
        ),
      ),
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
            padding: EdgeInsets.symmetric(horizontal: 24, vertical: 12),
            decoration: BoxDecoration(
              color: backgroundColor,
              borderRadius: BorderRadius.circular(2),
            ),
            child: Text(
              message,
              style: TextStyle(color: Colors.black),
            ),
          ),
        ),
      ),
    );

    // Thêm thông báo vào Overlay
    overlay.insert(overlayEntry);

    // Tự động xóa thông báo sau 3 giây
    Future.delayed(Duration(seconds: 3), () {
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

