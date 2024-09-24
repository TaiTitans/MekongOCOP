import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:smart_shop/Common/Widgets/app_button.dart';
import 'package:smart_shop/Common/Widgets/gradient_header.dart';
import 'package:smart_shop/Screens/Login/verification_screen.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';
import 'package:smart_shop/service/auth_service.dart';
class Login extends StatefulWidget {

  static String routeName = 'login';

  const Login({Key? key}) : super(key: key);

  @override
  _LoginState createState() => _LoginState();
}
class _LoginState extends State<Login>{
  final AuthService _authService = AuthService();
  final TextEditingController _emailController = TextEditingController();
  bool _isLoading = false;
  String? _errorMessage;

  Future<void> _sendOtp() async{
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });
    final email = _emailController.text.trim();
    if(email.isEmpty || !email.contains('@')){
      setState(() {
        _isLoading = false;
        _errorMessage = 'Vui lòng nhập email hợp lệ.';
      });
      return;
    }
    final success = await _authService.sendOTPSignUp(email);

    setState(() {
      _isLoading = false;
    });
    if(success){
      Navigator.pushReplacementNamed(context, Verification.routeName);
    }else{
      setState(() {
        _errorMessage = 'Gửi OTP không thành công. Vui lòng thử lại.';
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SingleChildScrollView(
        child: Column(
          children: [
            _buildHeader(context),
            _buildWidget(context),
          ],
        )
      )
    );
  }


  Widget _buildHeader(BuildContext context) {
    return const AppHeaderGradient(
      text: 'Cung cấp địa chỉ email của bạn',
      isProfile: false,
    );
  }

  Widget _buildWidget(BuildContext context) {
    return Container(
      margin: EdgeInsets.symmetric(horizontal: 20.0.w, vertical: 20.0.h),
      child: Column(
        children: [
          Text(
            'Hãy điền địa chỉ email của bạn vào ô bên dưới để xác thực đăng ký tài khoản',
            style: FontStyles.montserratRegular17(),
          ),
          _buildEmailField(context),
          SizedBox(
            height: 30.0.h,
          ),
          _buildSendButton(context),
          if(_errorMessage != null) _buildErrorText(),
          if(_isLoading) _buildLoadingIndicator(),
          SizedBox(
            height: 20.0.h,
          ),
          _buildSkipButton()
        ],
      ),
    );
  }


  Widget _buildEmailField(BuildContext context) {
    return Container(
      margin: EdgeInsets.only(top: 20.0.h),
      height: 60.h,
      width: MediaQuery.of(context).size.width,
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10.0.r),
        border: Border.all(color: Colors.grey),
      ),
      child: TextFormField(
        controller: _emailController,
        keyboardType: TextInputType.emailAddress,
        decoration: InputDecoration(
          border: InputBorder.none,
          contentPadding: EdgeInsets.symmetric(horizontal: 10.0.w, vertical: 18.0.w),
          hintText: 'Nhập email của bạn...',
          hintStyle: TextStyle(
            fontSize: 16.0.sp,
            color: Colors.grey,
          ),
        ),
      ),
    );
  }



  Widget _buildSendButton(BuildContext context) {
    return AppButton.button(
        height: 64.0.h,
        width: double.infinity,
        color: AppColors.secondary,
        onTap: _isLoading ? null : () => _sendOtp(),
        text: 'Gửi mã OTP xác thực');
  }

  Widget _buildSkipButton() {
    return TextButton(
      onPressed: () {},
      child: Text(
        'Bỏ qua',
        style: FontStyles.montserratBold17().copyWith(color: Colors.grey),
      ),
    );
  }

  Widget _buildLoadingIndicator(){
    return const CircularProgressIndicator();
  }

  Widget _buildErrorText(){
    return Text(
      _errorMessage ?? '',
      style: TextStyle(color: Colors.red, fontSize: 14.0.sp),
    );
  }


}
