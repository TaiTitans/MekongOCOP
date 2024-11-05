import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:smart_shop/Common/Widgets/custom_app_bar.dart';
import 'package:smart_shop/Screens/Notifications/widgets/notification_item.dart';
import 'dart:io' as platform;
import 'package:http/http.dart' as http; // Đừng quên import thư viện http
import 'package:shared_preferences/shared_preferences.dart';

import '../../model/notification.dart';

class NotificationScreen extends StatefulWidget {
  static const String routeName = 'notifications';
  const NotificationScreen({Key? key}) : super(key: key);
  @override
  _NotificationScreenState createState() => _NotificationScreenState();
}

class _NotificationScreenState extends State<NotificationScreen> {

  Future<String?> getAccessToken() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    return prefs.getString('accessToken');
  }

  Future<List<NotificationModel>> fetchNotifications(String accessToken) async {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    final response = await http.get(
      Uri.parse('$apiUrl/common/notification/history'),
      headers: {
        'Authorization': 'Bearer $accessToken',
        'Content-Type': 'application/json',
      },
    );

    print('Status Code: ${response.statusCode}');
    print('Response Body: ${response.body}');

    if (response.statusCode == 200) {
      // Giải mã body thành chuỗi UTF-8
      final utf8DecodedBody = utf8.decode(response.bodyBytes);
      final data = jsonDecode(utf8DecodedBody);
      List<NotificationModel> notifications = [];

      print('Data Received: ${data['data']}');

      for (var item in data['data']) {
        notifications.add(NotificationModel.fromJson(item));
      }
      return notifications;
    } else {
      throw Exception('Failed to load notifications');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _buildAppBar(context),
      body: FutureBuilder<String?>(
        future: getAccessToken(),
        builder: (context, tokenSnapshot) {
          if (tokenSnapshot.connectionState == ConnectionState.waiting) {
            return Center(child: CircularProgressIndicator());
          } else if (tokenSnapshot.hasError) {
            return Center(child: Text('Error: ${tokenSnapshot.error}'));
          } else if (!tokenSnapshot.hasData || tokenSnapshot.data == null) {
            return Center(child: Text('No access token found.'));
          }

          String accessToken = tokenSnapshot.data!;

          return FutureBuilder<List<NotificationModel>>(
            future: fetchNotifications(accessToken),
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return Center(child: CircularProgressIndicator());
              } else if (snapshot.hasError) {
                return Center(child: Text('Error: ${snapshot.error}'));
              } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
                return Center(child: Text('No notifications found.'));
              }

              // Đảo ngược danh sách thông báo
              final notifications = snapshot.data!.reversed.toList();

              return ListView.builder(
                itemCount: notifications.length,
                itemBuilder: (context, index) {
                  final notification = notifications[index];
                  return NotificationItem(
                    seen: false,
                    notificationMessage: notification.message,
                    notificationTimestamp: notification.sentAt, // Pass DateTime directly
                    onPressed: () {
                      // Action when notification is pressed
                    },
                  );
                },
              );
            },
          );
        },
      ),
    );
  }

  PreferredSize _buildAppBar(BuildContext context) {
    return PreferredSize(
      preferredSize: const Size(double.infinity, 100),
      child: CustomAppBar(
        isHome: false,
        fixedHeight: 100,
        enableSearchField: false,
        leadingIcon: platform.Platform.isIOS ? Icons.arrow_back_ios : Icons.arrow_back,
        leadingOnTap: () {
          Navigator.pop(context);
        },
        title: 'Notifications',
      ),
    );
  }
}