// change_email_screen.dart
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import '../../Common/Widgets/custom_app_bar.dart';

class ChangeEmailScreen extends StatefulWidget {
  const ChangeEmailScreen({Key? key}) : super(key: key);

  @override
  State<ChangeEmailScreen> createState() => _ChangeEmailScreenState();
}

class _ChangeEmailScreenState extends State<ChangeEmailScreen> {
  final _formKey = GlobalKey<FormState>();
  final _currentEmailController = TextEditingController();
  final _newEmailController = TextEditingController();
  final _otpController = TextEditingController();
  bool _isLoading = false;
  bool _otpSent = false;

  Future<void> _sendOTP() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isLoading = true);

    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('accessToken') ?? '';
      final baseUrl = dotenv.env['API_URL'] ?? '';

      final response = await http.post(
        Uri.parse('$baseUrl/otp/email?currentEmail=${Uri.encodeComponent(_currentEmailController.text)}&newEmail=${Uri.encodeComponent(_newEmailController.text)}'),
        headers: {
          'Authorization': 'Bearer $token',
        },
      );

      if (response.statusCode == 200) {
        setState(() => _otpSent = true);
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Mã OTP đã được gửi')),
        );
      } else {
        throw Exception('Gửi OTP thất bại');
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(e.toString())),
      );
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _changeEmail() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isLoading = true);

    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('accessToken') ?? '';
      final baseUrl = dotenv.env['API_URL'] ?? '';

      final response = await http.patch(
        Uri.parse('$baseUrl/user/email?otp=${Uri.encodeComponent(_otpController.text)}'),
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
        body: jsonEncode({
          'email': _newEmailController.text,
        }),
      );

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Đổi email thành công')),
        );
        Navigator.pop(context);
      } else {
        throw Exception('Đổi email thất bại');
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(e.toString())),
      );
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _buildAppBar(context),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Đổi Email',
                style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 20),
              _buildEmailField(
                controller: _currentEmailController,
                labelText: 'Email hiện tại',
                validator: (value) => value?.isEmpty ?? true ? 'Vui lòng nhập email hiện tại' : null,
              ),
              const SizedBox(height: 16),
              _buildEmailField(
                controller: _newEmailController,
                labelText: 'Email mới',
                validator: (value) => value?.isEmpty ?? true ? 'Vui lòng nhập email mới' : null,
              ),
              const SizedBox(height: 16),
              if (!_otpSent)
                ElevatedButton(
                  onPressed: _isLoading ? null : _sendOTP,
                  child: _isLoading
                      ? const CircularProgressIndicator(color: Colors.white)
                      : const Text('Gửi mã OTP'),
                  style: ElevatedButton.styleFrom(
                    minimumSize: Size(double.infinity, 50),
                    backgroundColor: Colors.deepPurple,
                    foregroundColor: Colors.white,  // Đổi màu chữ ở đây
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                  ),
                ),
              if (_otpSent) ...[
                _buildOtpField(),
                const SizedBox(height: 20),
                ElevatedButton(
                  onPressed: _isLoading ? null : _changeEmail,
                  child: _isLoading
                      ? const CircularProgressIndicator(color: Colors.white)
                      : const Text('Xác nhận đổi email'),
                  style: ElevatedButton.styleFrom(
                    minimumSize: Size(double.infinity, 50), backgroundColor: Colors.green,
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                  ),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildEmailField({required TextEditingController controller, required String labelText, String? Function(String?)? validator}) {
    return TextFormField(
      controller: controller,
      decoration: InputDecoration(
        labelText: labelText,
        labelStyle: TextStyle(color: Colors.grey[700]),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(10),
        ),
        contentPadding: const EdgeInsets.symmetric(vertical: 16, horizontal: 12),
      ),
      keyboardType: TextInputType.emailAddress,
      validator: validator,
    );
  }

  Widget _buildOtpField() {
    return TextFormField(
      controller: _otpController,
      decoration: const InputDecoration(
        labelText: 'Nhập mã OTP',
        border: OutlineInputBorder(),
        contentPadding: EdgeInsets.symmetric(vertical: 16, horizontal: 12),
      ),
      keyboardType: TextInputType.number,
      validator: (value) => value?.isEmpty ?? true ? 'Vui lòng nhập mã OTP' : null,
    );
  }

  PreferredSize _buildAppBar(BuildContext context) {
    return PreferredSize(
      preferredSize: Size(double.infinity, MediaQuery.of(context).size.height * .20),
      child: CustomAppBar(
        isHome: false,
        title: 'Đổi email',
        fixedHeight: 120.0,
        enableSearchField: false,
        leadingIcon: Icons.arrow_back,
        leadingOnTap: () {
          Navigator.pop(context);
        },
      ),
    );
  }
}
