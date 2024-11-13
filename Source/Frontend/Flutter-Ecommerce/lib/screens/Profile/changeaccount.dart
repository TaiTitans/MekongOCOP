// change_account_screen.dart
import 'package:flutter/material.dart';

import '../../Common/Widgets/custom_app_bar.dart';
import 'changeemail.dart';
import 'changepassword.dart';

class ChangeAccountScreen extends StatelessWidget {
  static const String routeName = 'changeaccount';

  const ChangeAccountScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar:_buildAppBar(context),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 16.0),
            Text(
              'Thay đổi thông tin tài khoản',
              style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                fontWeight: FontWeight.bold,
                color: Colors.deepPurple, // Thay đổi màu chữ
              ),
            ),
            const SizedBox(height: 24.0),
            Card(
              elevation: 4, // Thêm độ bóng cho Card
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12.0),
              ),
              child: Column(
                children: [
                  ListTile(
                    leading: const Icon(
                      Icons.lock,
                      color: Colors.deepPurple, // Thay đổi màu Icon
                    ),
                    title: const Text('Đổi mật khẩu'),
                    onTap: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => const ChangePasswordScreen(),
                        ),
                      );
                    },
                  ),
                  const Divider(
                    height: 0,
                    thickness: 1,
                    color: Colors.grey, // Thay đổi màu Divider
                  ),
                  ListTile(
                    leading: const Icon(
                      Icons.email,
                      color: Colors.deepPurple, // Thay đổi màu Icon
                    ),
                    title: const Text('Đổi email'),
                    onTap: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => const ChangeEmailScreen(),
                        ),
                      );
                    },
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  PreferredSize _buildAppBar(BuildContext context) {
    return PreferredSize(
      preferredSize: Size(double.infinity, MediaQuery.of(context).size.height * .20),
      child: CustomAppBar(
        isHome: false,
        title: 'Cài đặt',
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