// lib/flash_sale_widget.dart

import 'dart:async';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:animated_text_kit/animated_text_kit.dart';

class FlashSaleWidget extends StatefulWidget {
  @override
  _FlashSaleWidgetState createState() => _FlashSaleWidgetState();
}

class _FlashSaleWidgetState extends State<FlashSaleWidget> {
  late DateTime nextFlashSaleTime;
  late Duration timeRemaining;
  Timer? timer;

  @override
  void initState() {
    super.initState();
    _calculateNextFlashSaleTime();
    _startTimer();
  }

  void _calculateNextFlashSaleTime() {
    DateTime now = DateTime.now().toLocal();
    List<int> flashSaleHours = [9, 12, 15, 18, 21, 0];
    nextFlashSaleTime = flashSaleHours
        .map((hour) {
      if (hour == 0) {
        return DateTime(now.year, now.month, now.day + 1, hour);
      } else {
        return DateTime(now.year, now.month, now.day, hour);
      }
    }).firstWhere((time) => time.isAfter(now), orElse: () {
      return DateTime(now.year, now.month, now.day + 1, 9);
    });
    timeRemaining = nextFlashSaleTime.difference(now);
  }

  void _startTimer() {
    timer = Timer.periodic(Duration(seconds: 1), (timer) {
      setState(() {
        timeRemaining = nextFlashSaleTime.difference(DateTime.now().toLocal());
        if (timeRemaining.isNegative) {
          timer.cancel();
          _calculateNextFlashSaleTime();
          _startTimer();
        }
      });
    });
  }

  @override
  void dispose() {
    timer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    bool isFlashSaleActive = timeRemaining.inMinutes >= 0 && timeRemaining.inMinutes < 60;
    String formattedTimeRemaining = "${timeRemaining.inHours.toString().padLeft(2, '0')}:${(timeRemaining.inMinutes % 60).toString().padLeft(2, '0')}:${(timeRemaining.inSeconds % 60).toString().padLeft(2, '0')}";

    return Card(
      margin: EdgeInsets.all(14.0),
      child: Padding(
        padding: EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(Icons.flash_on, color: Colors.orange, size: 30),
                SizedBox(width: 8.0),
                // Tách riêng hiệu ứng chữ AnimatedTextKit để không bị ảnh hưởng bởi setState
                AnimatedTextKit(
                  animatedTexts: [
                    ColorizeAnimatedText(
                      'Flash Sale',
                      textStyle: TextStyle(
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                      ),
                      colors: [
                        Colors.red,
                        Colors.orange,
                        Colors.yellow,
                        Colors.red,
                      ],
                    ),
                  ],
                  isRepeatingAnimation: true,
                  repeatForever: true,
                ),
                SizedBox(width: 8.0),
                if (isFlashSaleActive)
                  Text(
                    'đang diễn ra!',
                    style: TextStyle(fontSize: 18, color: Colors.red),
                  )
                else
                  Row(
                    children: [
                      Text(
                        'tiếp theo sau:',
                        style: TextStyle(fontSize: 18),
                      ),
                      SizedBox(width: 8.0),
                      Text(
                        formattedTimeRemaining,
                        style: TextStyle(fontSize: 18, color: Colors.blue),
                      ),
                    ],
                  ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
