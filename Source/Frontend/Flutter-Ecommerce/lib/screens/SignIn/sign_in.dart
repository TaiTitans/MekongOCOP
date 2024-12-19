import 'package:flutter/material.dart';
import 'package:smart_shop/screens/Login/forgot_password.dart';
import 'package:smart_shop/service/auth_service.dart';

import '../../Common/Widgets/app_button.dart';
import '../../Common/Widgets/gradient_header.dart';
import '../../Utils/app_colors.dart';
import '../Home/home.dart';
import '../Profile/profile.dart';
import '../SignUp/sign_up.dart';
class SignIn extends StatefulWidget {
  const SignIn({Key? key}) : super(key: key);
  static const String routeName = 'signin';

  @override
  _SignInState createState() => _SignInState();
}

class _SignInState extends State<SignIn> with SingleTickerProviderStateMixin {
  final AuthService _authService = AuthService();
  final TextEditingController _usernameController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  bool _isPasswordVisible = false;
  bool _isLoading = false;
  String? _errorMessage;

  late AnimationController _controller;
  late Animation<Offset> _logoAnimation;
  late Animation<Offset> _formAnimation;
  late Animation<double> _opacityAnimation;

  @override
  void initState() {
    super.initState();

    // Initialize animation controller
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 2),
    );

    // Initialize animations
    _logoAnimation = Tween<Offset>(begin: Offset(0, -1), end: Offset(0, 0)).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeOut),
    );

    _formAnimation = Tween<Offset>(begin: Offset(0, 1), end: Offset(0, 0)).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeOut),
    );

    _opacityAnimation = Tween<double>(begin: 0, end: 1).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeIn),
    );

    // Start the animation
    _controller.forward();
  }

  @override
  void dispose() {
    _controller.dispose();
    _usernameController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  Future<void> _loginAccount() async {
    final String username = _usernameController.text;
    final String password = _passwordController.text;

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    print('Attempting to sign in with username: $username, password: $password');

    final response = await _authService.signIn(username, password);

    if (response['error'] == null) {
      final cookies = response['cookies'] as Map<String, String>;
      final hasProfile = response['hasProfile'] as bool;

      setState(() {
        _isLoading = false;
      });

      if (hasProfile) {
        _showSuccessSnackbar('Đăng nhập thành công!');
        print('hasProfile: ${response['hasProfile']}');
        Navigator.pushReplacementNamed(context, '/');
      } else {
        _showSuccessSnackbar('Bạn cần cập nhật thông tin cá nhân.');
        Navigator.pushReplacementNamed(context, SignUp.routeName);
      }
    } else {
      print('Đăng nhập thất bại. Lỗi: ${response['error']}');

      setState(() {
        _isLoading = false;
        _errorMessage = response['error'];
      });
      _showErrorSnackbar(_errorMessage!);
    }
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _buildBody(context),
    );
  }

  Widget _buildBody(BuildContext context) {
    return SingleChildScrollView(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeader(),
          _buildAnimatedLogo(),
          SizedBox(height: 30.0),
          _buildAnimatedLoginForm(context),
          SizedBox(height: 30.0),
          _buildLoginButton(),
          SizedBox(height: 20.0),
          _buildRegisterButton(),
        ],
      ),
    );
  }

  Widget _buildHeader() {
    return const AppHeaderGradient(
      text: 'Đăng nhập',
      isProfile: false,
    );
  }

  // Logo with fade-in and slide down animation
  Widget _buildAnimatedLogo() {
    return Center(
      child: SlideTransition(
        position: _logoAnimation,
        child: FadeTransition(
          opacity: _opacityAnimation,
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 40.0),
            child: Image.asset(
              'assets/mekongocop_logo.png',
              height: 200.0,
              width: 200.0,
            ),
          ),
        ),
      ),
    );
  }

  // Form with slide-up and fade-in animation
  Widget _buildAnimatedLoginForm(BuildContext context) {
    return SlideTransition(
      position: _formAnimation,
      child: FadeTransition(
        opacity: _opacityAnimation,
        child: _buildLoginForm(context),
      ),
    );
  }

  Widget _buildLoginForm(BuildContext context) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 5.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Tên đăng nhập',
            style: TextStyle(fontSize: 17, fontWeight: FontWeight.bold),
          ),
          SizedBox(height: 20.0),
          _buildUsernameField(),
          SizedBox(height: 10.0),
          const Text(
            'Mật khẩu',
            style: TextStyle(fontSize: 17, fontWeight: FontWeight.bold),
          ),
          SizedBox(height: 10.0),
          _buildPasswordField(),
          if (_errorMessage != null)
            Padding(
              padding: const EdgeInsets.only(top: 20.0),
              // child: Text(
              //   _errorMessage!,
              //   style: const TextStyle(color: Colors.red),
              // ),
            ),
        ],
      ),
    );
  }

  Widget _buildUsernameField() {
    return TextField(
      controller: _usernameController,
      decoration: const InputDecoration(
        labelText: 'Nhập tên đăng nhập...',
        border: OutlineInputBorder(),
      ),
    );
  }

  Widget _buildPasswordField() {
    return TextField(
      controller: _passwordController,
      obscureText: !_isPasswordVisible, // Điều chỉnh theo trạng thái của mật khẩu
      decoration: InputDecoration(
        labelText: 'Nhập mật khẩu...',
        border: const OutlineInputBorder(),
        suffixIcon: IconButton(
          icon: Icon(
            _isPasswordVisible ? Icons.visibility : Icons.visibility_off,
          ),
          onPressed: () {
            setState(() {
              _isPasswordVisible = !_isPasswordVisible; // Đổi trạng thái mật khẩu
            });
          },
        ),
      ),
    );
  }

  Widget _buildLoginButton() {
    return Center(
      child: AppButton.button(
        width: 327.0,
        height: 64.0,
        color: AppColors.secondary,
        text: 'Đăng nhập',
        onTap: () async {
          if (_usernameController.text.isEmpty) {
            _showErrorSnackbar('Vui lòng nhập tên đăng nhập');
          } else if (_passwordController.text.isEmpty) {
            _showErrorSnackbar('Vui lòng nhập mật khẩu');
          } else {
            await _loginAccount();
          }
        },
      ),
    );
  }

  Widget _buildRegisterButton() {
    return Center(
      child: TextButton(
        onPressed: () {
          Navigator.pushNamed(context, ForgotPasswordScreen.routeName);
        },
        child: const Text(
          'Quên mật khẩu ?',
          style: TextStyle(
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
