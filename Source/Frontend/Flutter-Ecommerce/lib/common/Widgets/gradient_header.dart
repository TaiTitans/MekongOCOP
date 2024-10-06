import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';

import '../../service/auth_service.dart';
class AppHeaderGradient extends StatefulWidget {
  const AppHeaderGradient(
      {this.text, this.isProfile, this.fixedHeight, Key? key})
      : super(key: key);
  final String? text;
  final bool? isProfile;
  final double? fixedHeight;

  @override
  _AppHeaderGradientState createState() => _AppHeaderGradientState();
}

class _AppHeaderGradientState extends State<AppHeaderGradient> {
  Map<String, dynamic>? profileData;
  bool isLoading = true;
  bool hasError = false;

  @override
  void initState() {
    super.initState();
    _fetchProfile();
  }

  // Gọi hàm fetchUserProfile từ service
  Future<void> _fetchProfile() async {
    try {
      final userProfileService = AuthService();
      final sharedPreferences = await SharedPreferences.getInstance();
      final accessToken = sharedPreferences.getString('accessToken') ?? '';

      if (accessToken.isEmpty) {
        throw Exception('Access token not found');
      }

      final fetchedData = await userProfileService.fetchUserProfile(accessToken);
      setState(() {
        profileData = fetchedData;
        isLoading = false;
      });
    } catch (error) {
      setState(() {
        hasError = true;
        isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      height: widget.fixedHeight ?? 220.h,
      width: MediaQuery.of(context).size.width,
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [AppColors.primaryLight, AppColors.primaryDark],
          stops: [0, 1],
          end: Alignment.topRight,
          begin: Alignment.bottomLeft,
        ),
        color: AppColors.primaryDark,
        borderRadius: BorderRadius.only(
          bottomRight: widget.isProfile!
              ? Radius.circular(150.0.r)
              : Radius.circular(250.0.r),
        ),
      ),
      child: widget.isProfile!
          ? _buildProfileContent()
          : Align(
        alignment: Alignment.centerLeft,
        child: Padding(
          padding: EdgeInsets.only(left: 24.w, right: 34.w),
          child: Text(
            widget.text!,
            style: FontStyles.montserratBold25()
                .copyWith(color: AppColors.white),
          ),
        ),
      ),
    );
  }

  Widget _buildProfileContent() {
    if (isLoading) {
      return const Center(child: CircularProgressIndicator());
    } else if (hasError || profileData == null) {
      return Center(child: Text('Error fetching profile'));
    } else {
      return _buildProfileInfo(profileData!);
    }
  }

  Widget _buildProfileInfo(Map<String, dynamic> profileData) {
    return Container(
      margin: EdgeInsets.only(left: 20.0.w, top: 40.0.h),
      child: Row(
        children: [
          Container(
            padding: EdgeInsets.all(2.0), // Tạo khoảng cách giữa ảnh và border
            decoration: BoxDecoration(
              color: Colors.white, // Màu border trắng
              shape: BoxShape.circle,
            ),
            child: CircleAvatar(
              radius: 40.0.r,
              backgroundImage: NetworkImage(
                profileData['user_profile_image'] ?? 'assets/product/profile.png',
              ),
            ),
          ),
          SizedBox(width: 10.0.w),
          Expanded( // Sử dụng Expanded để cho phép Text chiếm không gian còn lại
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  profileData['full_name'] ?? 'N/A',
                  style: FontStyles.montserratBold19()
                      .copyWith(color: AppColors.white),
                ),
                Text(
                  profileData['bio'] ?? 'No bio available',
                  style: FontStyles.montserratRegular14().copyWith(color: AppColors.lightGray),
                  softWrap: true,
                  overflow: TextOverflow.visible,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

}
