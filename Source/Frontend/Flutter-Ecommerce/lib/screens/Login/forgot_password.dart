import 'package:flutter/material.dart';
import 'package:smart_shop/service/auth_service.dart';

import '../SignIn/sign_in.dart';

class ForgotPasswordScreen extends StatefulWidget {
  static const String routeName = 'forgotPass';
  const ForgotPasswordScreen({Key? key}) : super(key: key);

  @override
  _ForgotPasswordScreenState createState() => _ForgotPasswordScreenState();
}

class _ForgotPasswordScreenState extends State<ForgotPasswordScreen> {
  final AuthService _authService = AuthService();
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _otpController = TextEditingController();
  final TextEditingController _newPasswordController = TextEditingController();

  bool _isOTPSent = false; // Để theo dõi trạng thái OTP đã gửi
  bool _isLoading = false;
  String? _errorMessage;

  // Hàm gửi OTP
  Future<void> _sendOTP() async {
    final String email = _emailController.text;

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    final response = await _authService.sendOTPForgotPass(email);

    setState(() {
      _isLoading = false;
    });

    if (response == null) {
      setState(() {
        _isOTPSent = true;
      });
      _showSuccessSnackbar('OTP đã được gửi đến email của bạn.');
    }else if(response == 'Email is not registered') {
      setState(() {
      _showErrorSnackbar('Email chưa được đăng ký');
      });
    }else {
      setState(() {
        _errorMessage = response;
      });
    }
  }

  // Hàm đổi mật khẩu
  Future<void> _resetPassword() async {
    final String email = _emailController.text;
    final int otp = int.parse(_otpController.text);
    final String newPassword = _newPasswordController.text;

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    final response = await _authService.renewPassword(email, otp, newPassword);

    setState(() {
      _isLoading = false;
    });

    if (response == null) {
      _showSuccessSnackbar('Mật khẩu của bạn đã được đổi thành công.');
      Navigator.pushReplacementNamed(context, SignIn.routeName);
    }else if(response == 'User not found') {
      setState(() {
        _showErrorSnackbar('Email chưa được đăng ký');
      });
    } else {
      setState(() {
        _errorMessage = response;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF5F5F5),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            const SizedBox(height: 60.0),
            _buildHeader(),
            const SizedBox(height: 40.0),
            const Text(
              'Địa chỉ email',
              style: TextStyle(fontSize: 17, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 5.0),
            _buildEmailInput(),
            if (_isOTPSent) ...[
              const SizedBox(height: 10.0),
              const Text(
                'Mã OTP',
                style: TextStyle(fontSize: 17, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 5.0),
              _buildOTPInput(),
              const SizedBox(height: 10.0),
              const Text(
                'Mật khẩu mới',
                style: TextStyle(fontSize: 17, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 5.0),
              _buildNewPasswordInput(),
            ],
            const SizedBox(height: 20.0),
            _buildSubmitButton(),
            if (_errorMessage != null)
              Padding(
                padding: const EdgeInsets.only(top: 20.0),
                child: Text(
                  _errorMessage!,
                  style: const TextStyle(color: Colors.red),
                ),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildHeader() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.start,
      children: [
        IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.black),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
        const SizedBox(width: 10.0),
        const Text(
          'Quên mật khẩu',
          style: TextStyle(
            fontSize: 28.0,
            fontWeight: FontWeight.bold,
            color: Colors.black,
          ),
        ),
      ],
    );
  }

  Widget _buildEmailInput() {
    return TextField(
      controller: _emailController,
      decoration: InputDecoration(
        labelText: 'Email',
        labelStyle: const TextStyle(color: Colors.black),
        hintText: 'Nhập email của bạn',
        hintStyle: const TextStyle(color: Colors.black26),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12.0),
        ),
        prefixIcon: const Icon(Icons.email_outlined, color: Colors.black87),
      ),
      style: const TextStyle(color: Colors.black54),
      keyboardType: TextInputType.emailAddress,
    );
  }

  Widget _buildOTPInput() {
    return TextField(
      controller: _otpController,
      decoration: InputDecoration(
        labelText: 'OTP',
        labelStyle: const TextStyle(color: Colors.black),
        hintText: 'Nhập mã OTP',
        hintStyle: const TextStyle(color: Colors.black26),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12.0),
        ),
        prefixIcon: const Icon(Icons.lock_outline, color: Colors.black87),
      ),
      style: const TextStyle(color: Colors.black54),
      keyboardType: TextInputType.number,
    );
  }

  Widget _buildNewPasswordInput() {
    return TextField(
      controller: _newPasswordController,
      obscureText: true,
      decoration: InputDecoration(
        labelText: 'Mật khẩu',
        labelStyle: const TextStyle(color: Colors.black),
        hintText: 'Nhập mật khẩu mới',
        hintStyle: const TextStyle(color: Colors.black26),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12.0),
        ),
        prefixIcon: const Icon(Icons.lock_outline, color: Colors.black87),
      ),
      style: const TextStyle(color: Colors.black54),
    );
  }

  Widget _buildSubmitButton() {
    return ElevatedButton(
      style: ElevatedButton.styleFrom(
        padding: const EdgeInsets.symmetric(vertical: 16.0),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12.0),
        ),
      ),
      onPressed: _isLoading ? null : (_isOTPSent ? _resetPassword : _sendOTP),
      child: _isLoading
          ? const CircularProgressIndicator(
        valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
      )
          : Text(_isOTPSent ? 'Đổi mật khẩu' : 'Gửi OTP'),
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



}
