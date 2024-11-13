// change_password_screen.dart
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;

import '../../Common/Widgets/custom_app_bar.dart';

class ChangePasswordScreen extends StatefulWidget {
  const ChangePasswordScreen({Key? key}) : super(key: key);

  @override
  State<ChangePasswordScreen> createState() => _ChangePasswordScreenState();
}

class _ChangePasswordScreenState extends State<ChangePasswordScreen> {
  final _formKey = GlobalKey<FormState>();
  final _oldPasswordController = TextEditingController();
  final _newPasswordController = TextEditingController();
  final _confirmPasswordController = TextEditingController();
  bool _isLoading = false;

  Future<void> _changePassword() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isLoading = true);

    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('accessToken') ?? '';
      final baseUrl = dotenv.env['API_URL'] ?? '';

      final response = await http.patch(
        Uri.parse('$baseUrl/user/password?oldPassword=${Uri.encodeComponent(_oldPasswordController.text)}&newPassword=${Uri.encodeComponent(_newPasswordController.text)}'),
        headers: {
          'Authorization': 'Bearer $token',
        },
      );

      if (response.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Đổi mật khẩu thành công')),
        );
        Navigator.pop(context);
      } else {
        throw Exception('Đổi mật khẩu thất bại');
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
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Thay đổi mật khẩu',
                style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  fontWeight: FontWeight.bold,
                  color: Colors.black87,
                ),
              ),
              const SizedBox(height: 24),
              _buildPasswordInput(
                controller: _oldPasswordController,
                labelText: 'Mật khẩu cũ',
                hintText: 'Nhập mật khẩu cũ',
                validatorText: 'Vui lòng nhập mật khẩu cũ',
              ),
              const SizedBox(height: 16),
              _buildPasswordInput(
                controller: _newPasswordController,
                labelText: 'Mật khẩu mới',
                hintText: 'Nhập mật khẩu mới',
                validatorText: 'Vui lòng nhập mật khẩu mới',
              ),
              const SizedBox(height: 16),
              _buildPasswordInput(
                controller: _confirmPasswordController,
                labelText: 'Xác nhận mật khẩu mới',
                hintText: 'Xác nhận mật khẩu mới',
                validatorText: 'Mật khẩu xác nhận không khớp',
                validateMatch: true,
              ),
              const SizedBox(height: 30),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: _isLoading ? null : _changePassword,
                  style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    backgroundColor: Colors.deepPurple,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8),
                    ),
                  ),
                  child: _isLoading
                      ? const CircularProgressIndicator(
                    valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                  )
                      : const Text(
                    'Đổi mật khẩu',
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: Colors.white
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildPasswordInput({
    required TextEditingController controller,
    required String labelText,
    required String hintText,
    required String validatorText,
    bool validateMatch = false,
  }) {
    return TextFormField(
      controller: controller,
      obscureText: true,
      decoration: InputDecoration(
        labelText: labelText,
        hintText: hintText,
        filled: true,
        fillColor: Colors.grey[200],
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide.none,
        ),
        contentPadding: const EdgeInsets.symmetric(vertical: 18, horizontal: 16),
      ),
      validator: (value) {
        if (value?.isEmpty ?? true) return validatorText;
        if (validateMatch && value != _newPasswordController.text) {
          return 'Mật khẩu xác nhận không khớp';
        }
        return null;
      },
    );
  }

  PreferredSize _buildAppBar(BuildContext context) {
    return PreferredSize(
      preferredSize: Size(double.infinity, MediaQuery.of(context).size.height * .20),
      child: CustomAppBar(
        isHome: false,
        title: 'Đổi mật khẩu',
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