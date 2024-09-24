import 'package:flutter/material.dart';
import 'package:smart_shop/Utils/app_colors.dart';

class AppTextField extends StatefulWidget {
  const AppTextField({this.hintText, this.labelText, this.onTap, Key? key})
      : super(key: key);
  final String? hintText, labelText;
  final Function()? onTap;
  @override
  _AppTextFieldState createState() => _AppTextFieldState();
}

class _AppTextFieldState extends State<AppTextField> {
  @override
  Widget build(BuildContext context) {
    return TextFormField(
      autofocus: false,
      decoration: InputDecoration(
        fillColor: AppColors.white,
        filled: true,
        border: OutlineInputBorder(borderRadius: BorderRadius.circular(10.0)),
        hintText: widget.hintText,
        labelText: widget.labelText,
      ),
      onTap: widget.onTap,
    );
  }
}
