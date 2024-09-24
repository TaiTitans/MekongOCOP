import 'package:flutter/material.dart';
import 'package:smart_shop/Utils/app_colors.dart';

class AppTitle extends StatelessWidget {
  const AppTitle({this.marginTop, Key? key, this.fontStyle}) : super(key: key);
  final double? marginTop;
  final TextStyle? fontStyle;
  @override
  Widget build(BuildContext context) {
    return Container(
      margin: EdgeInsets.only(top: marginTop!),
      child: RichText(
        text: TextSpan(
          text: 'Mekong',
          style: fontStyle!.copyWith(color: AppColors.white),
          children: [
            TextSpan(
              text: 'OCOP',
              style: fontStyle!.copyWith(color: AppColors.secondary),
            )
          ],
        ),
      ),
    );
  }
}
