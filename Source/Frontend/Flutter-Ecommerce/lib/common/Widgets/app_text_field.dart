import 'package:flutter/material.dart';
import 'package:smart_shop/Utils/app_colors.dart';

class AppTextField extends StatefulWidget {
  const AppTextField({
    this.hintText,
    this.labelText,
    this.onTap,
    this.controller,
    Key? key,
  }) : super(key: key);

  final String? hintText;
  final String? labelText;
  final Function()? onTap;
  final TextEditingController? controller;

  @override
  _AppTextFieldState createState() => _AppTextFieldState();
}

class _AppTextFieldState extends State<AppTextField> {
  @override
  Widget build(BuildContext context) {
    return TextFormField(
      controller: widget.controller,
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
