import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:smart_shop/Common/Widgets/shimmer_effect.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';
import 'package:smart_shop/dummy/dummy_data.dart';

class CatalogueWidget extends StatelessWidget {
  const CatalogueWidget({this.height, this.width, this.index, this.imagePath, Key? key})
      : super(key: key);

  final double? height, width;
  final int? index;
  final String? imagePath; // Thêm tham số imagePath để nhận đường dẫn ảnh từ assets

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: EdgeInsets.only(right: 5.0.w, top: 17.h),
      child: Stack(
        alignment: Alignment.center,
        children: [
          ClipRRect(
            borderRadius: BorderRadius.circular(10.0),
            child: Image.asset(
              imagePath ?? '', // Đọc ảnh từ assets
              fit: BoxFit.cover,
              width: width,
              height: height,
              color: const Color.fromRGBO(29, 35, 50, 0.2),
              colorBlendMode: BlendMode.srcOver,
              errorBuilder: (context, error, stackTrace) {
                // Xử lý khi không tìm thấy ảnh
                return ShimmerEffect(
                  borderRadius: 10.0,
                  height: height,
                  width: width,
                );
              },
            ),
          ),
          SizedBox(
            height: height,
            width: width,
            child: Center(
              child: Text(
                DummyData.catalogueTitles[index!],
                style: FontStyles.montserratBold14().copyWith(
                  color: AppColors.white,
                ),
                textAlign: TextAlign.center,
              ),
            ),
          ),
        ],
      ),
    );
  }
}
