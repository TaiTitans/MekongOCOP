import 'package:flutter/material.dart';
import 'package:shimmer/shimmer.dart';

class ShimmerEffect extends StatelessWidget {

  final double? borderRadius;
  final double? height, width;
  final bool? isCircular;
  const ShimmerEffect({this.height, this.width, this.borderRadius,this.isCircular = false ,Key? key})
      : super(key: key);
  @override
  Widget build(BuildContext context) {
    return Shimmer.fromColors(
      baseColor: Colors.grey.shade300,
      highlightColor: Colors.grey.shade100,
      enabled: true,
      child: Container(
        decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(isCircular! ? height! : 8 )),
        height: height ?? height,
        width: width ?? width,
      ),
    );
  }
}
