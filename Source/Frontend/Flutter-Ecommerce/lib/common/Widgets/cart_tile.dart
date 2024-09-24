import 'package:flutter/material.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';


class CartTile extends StatelessWidget {
  const CartTile({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Container(
          height: 80,
          width: 80,
          decoration: BoxDecoration(
              image: const DecorationImage(
                  image: AssetImage('assets/product/pic1.png')),
              borderRadius: BorderRadius.circular(10.0)),
        ),
        Expanded(
          child: Container(
            height: 80.h,
            padding: const EdgeInsets.all(5.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Astylish Women Open Front Long Sleeve Chunky Knit Cardigan',
                  style: FontStyles.montserratRegular14(),
                ),
                // SizedBox(height: 20.0),
                Text(
                  '\$89.99',
                  style: FontStyles.montserratBold17(),
                )
              ],
            ),
          ),
        ),
        Column(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: const [
            Icon(Icons.add_circle_outline, color: AppColors.lightGray),
            Text('1', style: TextStyle(color: AppColors.primaryLight)),
            Icon(
              Icons.remove_circle_outline,
              color: AppColors.lightGray,
            ),
          ],
        ),
      ],
    );
  }
}
