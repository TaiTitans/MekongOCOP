import 'package:flutter/material.dart';
import 'package:smart_shop/Utils/app_colors.dart';

class AppTheme {
  static ColorScheme light = const ColorScheme.light(
    primary: AppColors.primaryLight,
    secondary: AppColors.secondary,
    onPrimary: AppColors.white,
    onSecondary: AppColors.white,
    background: AppColors.whiteLight,

  );

  static ThemeData lightTheme = ThemeData(
    colorScheme: AppTheme.light,
    scaffoldBackgroundColor:AppColors.whiteLight,
    bottomSheetTheme: const BottomSheetThemeData(
      backgroundColor: AppColors.white,
      shape: OutlineInputBorder(
        borderSide: BorderSide(color: Colors.transparent),
        borderRadius: BorderRadius.only(
          topLeft: Radius.circular(10.0),
          topRight: Radius.circular(10.0),
        ),
      ),
    ),
  );
}
