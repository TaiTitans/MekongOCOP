import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'dart:convert';

// ignore: must_be_immutable
class NotificationItem extends StatelessWidget {
  final String? notificationMessage;
  final DateTime? notificationTimestamp;
  final VoidCallback? onPressed;
  final bool seen;

  NotificationItem({
    Key? key,
    required this.notificationMessage,
    required this.notificationTimestamp,
    required this.onPressed,
    this.seen = false,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    // Kiểm tra xem notificationTimestamp có null không trước khi định dạng
    String formattedDate = notificationTimestamp != null
        ? DateFormat('dd/MM/yyyy HH:mm').format(notificationTimestamp!)
        : 'Unknown date'; // Giá trị mặc định nếu timestamp là null

    return InkWell(
      onTap: onPressed,
      child: Container(
        decoration: BoxDecoration(
          color: seen ? Colors.grey[200] : Colors.white,
          borderRadius: BorderRadius.circular(8.0),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.1),
              spreadRadius: 1,
              blurRadius: 5,
              offset: Offset(0, 3), // Thay đổi vị trí của bóng
            ),
          ],
        ),
        padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            Row(
              children: [
                Icon(
                  Icons.notifications,
                  color: seen ? Colors.grey : Colors.blue,
                  size: 20,
                ),
                SizedBox(width: 8),
                Expanded(
                  child: Text(
                    notificationMessage ?? 'No message', // Sử dụng message trực tiếp
                    style: TextStyle(
                      fontSize: 14.0,
                      fontWeight: FontWeight.bold,
                      color: Colors.black,
                    ),
                  ),
                ),
              ],
            ),
            SizedBox(height: 4),
            Text(
              formattedDate, // Sử dụng ngày đã định dạng ở đây
              style: TextStyle(fontSize: 12.0, color: Colors.grey),
            ),
            Divider(),
          ],
        ),
      ),
    );
  }
}
