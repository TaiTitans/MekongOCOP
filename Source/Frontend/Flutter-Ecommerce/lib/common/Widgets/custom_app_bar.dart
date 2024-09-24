import 'package:flutter/material.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';
import 'app_title.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

class CustomAppBar extends StatefulWidget {
  const CustomAppBar(
      {this.isHome,
      this.leadingIcon,
      this.leadingOnTap,
      this.trailingIcon,
      this.trailingOnTap,
      this.title,
      this.scaffoldKey,
      this.enableSearchField,
      this.fixedHeight,
      Key? key})
      : super(key: key);
  final GlobalKey<ScaffoldState>? scaffoldKey;
  final bool? isHome;
  final IconData? leadingIcon;
  final Function()? leadingOnTap;
  final IconData? trailingIcon;
  final Function()? trailingOnTap;
  final String? title;
  final bool? enableSearchField;
  final double? fixedHeight;
  @override
  State<CustomAppBar> createState() => _CustomAppBarState();
}

class _CustomAppBarState extends State<CustomAppBar> {
  @override
  Widget build(BuildContext context) {
    return Container(
      height: widget.fixedHeight ?? 134.h,
      // height: 143.h,
      width: double.infinity,
      decoration: const BoxDecoration(
        color: Colors.red,
        gradient: LinearGradient(
          colors: [AppColors.primaryLight, AppColors.primaryDark],
          begin: Alignment.bottomRight,
          end: Alignment.topLeft,
          stops: [0, 1],
        ),
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Stack(
            clipBehavior: Clip.none,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  _buildDrawerButton(context),
                  widget.isHome! ? _buildAppTitle() : _title(),
                  _buildNotificationIcon(context),
                ],
              ),
              widget.enableSearchField!
                  ? Positioned(
                      bottom: -85.h,
                      width: MediaQuery.of(context).size.width,
                      child: _buildSearchField(context))
                  : const SizedBox(
                      height: 0,
                      width: 0,
                    ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildDrawerButton(BuildContext context) {
    return IconButton(
      padding: const EdgeInsets.only(top: 25.0),
      onPressed: widget.isHome!
          ? () {
              setState(
                () {
                  widget.scaffoldKey!.currentState!.openDrawer();
                },
              );
            }
          : widget.leadingOnTap,
      icon: Icon(
        widget.leadingIcon,
        color: AppColors.white,
      ),
    );
  }

  Widget _buildAppTitle() {
    return AppTitle(
      fontStyle: FontStyles.montserratExtraBold18(),
      marginTop: 0.0,
    );
  }

  Widget _title() {
    return Text(
      widget.title!,
      style: FontStyles.montserratBold19().copyWith(
        color: AppColors.white,
      ),
    );
  }

  Widget _buildNotificationIcon(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(right: 10.0),
      child: GestureDetector(
        onTap: widget.trailingOnTap,
        child: Icon(
          widget.trailingIcon,
          color: AppColors.white,
        ),
      ),
    );
  }

  Widget _buildSearchField(BuildContext context) {
    return Container(
      height: 44.h,
      width: 335.w,
      decoration: BoxDecoration(
          color: AppColors.white, borderRadius: BorderRadius.circular(50.0.r)),
      margin: const EdgeInsets.all(20.0),
      child: TextField(
        decoration: InputDecoration(
          prefixIcon: const Icon(
            Icons.search,
            color: Colors.grey,
          ),
          hintText: 'Bạn muốn tìm sản phẩm nào ?',
          hintStyle: const TextStyle(
            color: Colors.grey,
          ),
          fillColor: AppColors.white,
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(50.0.r),
            borderSide: const BorderSide(color: AppColors.white),
          ),
          focusedBorder: InputBorder.none,
          enabledBorder: InputBorder.none,
        ),
      ),
    );
  }
}
