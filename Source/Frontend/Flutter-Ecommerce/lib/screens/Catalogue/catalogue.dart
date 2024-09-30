import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:smart_shop/Common/Widgets/catalogue_widget.dart';
import 'package:smart_shop/Common/Widgets/item_widget.dart';
import 'package:smart_shop/Common/Widgets/custom_app_bar.dart';
import 'package:smart_shop/Screens/Filter/filter.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';
import 'package:smart_shop/dummy/dummy_data.dart';
import 'dart:io' as plateform;

class Catalogue extends StatefulWidget {
  static const String routeName = 'catalogue';

  const Catalogue({Key? key}) : super(key: key);
  @override
  State<Catalogue> createState() => _CatalogueState();
}

class _CatalogueState extends State<Catalogue> {
  final GlobalKey<ScaffoldState>? _key = GlobalKey();
  bool isItemClicked = false;
  bool seeAllClicked = false;
  bool showbackArrow = false;
  List<bool>? argList;

  @override
  Widget build(BuildContext context) {
    if (ModalRoute.of(context)!.settings.arguments != null) {
      argList = ModalRoute.of(context)!.settings.arguments as List<bool>;
      seeAllClicked = argList![0];
      showbackArrow = argList![1];

    }

    return Scaffold(
      backgroundColor: AppColors.whiteLight,
      key: _key,
      appBar: _buildAppBar(context),
      body: isItemClicked ? _buildItemsBody(context) : _buildBody(context),
      resizeToAvoidBottomInset: false,
    );
  }

  PreferredSize _buildAppBar(BuildContext context) {
    return PreferredSize(
      preferredSize:
          Size(double.infinity, MediaQuery.of(context).size.height * .20),
      child: CustomAppBar(
          scaffoldKey: _key,
          isHome: false,
          // fixedHeight: 50.0,
          enableSearchField: true,
          leadingIcon: showbackArrow ? plateform.Platform.isIOS
              ? Icons.arrow_back_ios
              : Icons.arrow_back : null,
          leadingOnTap: () {
            setState(() {
              isItemClicked = false;
              if (seeAllClicked) {
                Navigator.pop(context);
              }
            });
          },
          trailingIcon: isItemClicked ? Icons.filter_1_outlined : null,
          trailingOnTap: isItemClicked
              ? () {
                  Navigator.pushNamed(context, Filter.routeName);
                }
              : () {},
          title: isItemClicked ? 'OCOP' : 'Danh mục'),
    );
  }

  Widget _buildBody(BuildContext context) {
    var screenHeight = MediaQuery.of(context).size.height;
    return Container(
      margin: EdgeInsets.only(
          left: 10.0,
          right: 10.0,
          top: 30.0,
          bottom: seeAllClicked ? 0.0 : screenHeight * .10),
      child: ListView.builder(
        itemCount: DummyData.catalogueImagesLink.length,
        shrinkWrap: true,
        itemBuilder: (context, index) {
          return _buildCatalogueWidget(context, index: index); // Sử dụng widget ban đầu
        },
      ),
    );
  }



  Widget _buildCatalogueWidget(BuildContext context, {int? index}) {
    var screenWidth = MediaQuery.of(context).size.width;

    return GestureDetector(
      onTap: () {
        showCategories(context);
      },
      child: Card(
        margin: const EdgeInsets.only(top: 10.0),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10.0),
        ),
        clipBehavior: Clip.antiAlias, // Để Clip ảnh theo Card
        child: Stack(
          children: [
            // Sử dụng Image.asset thay vì CachedNetworkImage cho ảnh nội bộ
            Image.asset(
              DummyData.catalogueImagesLink[index!],
              fit: BoxFit.cover,
              width: screenWidth,
              height: 120, // Có thể điều chỉnh theo nhu cầu
              errorBuilder: (context, error, stackTrace) {
                // Xử lý nếu ảnh không tải được
                return Container(
                  color: Colors.grey.shade300,
                  child: Icon(Icons.broken_image, color: Colors.red),
                );
              },
            ),
            // Văn bản nằm trên ảnh
            Positioned.fill(
              child: Container(
                alignment: Alignment.center,
                color: Colors.black.withOpacity(0.5), // Màu nền đen mờ cho chữ nổi bật
                child: Text(
                  DummyData.catalogueTitles[index],
                  style: FontStyles.montserratBold17().copyWith(
                    fontSize: 17.0,
                    color: Colors.white,
                  ),
                  textAlign: TextAlign.center,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }



  showCategories(BuildContext context) {
    return showModalBottomSheet(
        shape: OutlineInputBorder(
            borderSide: const BorderSide(color: AppColors.white),
            borderRadius: BorderRadius.only(
              topLeft: Radius.circular(20.0.r),
              topRight: Radius.circular(20.0.r),
            )),
        context: context,
        builder: (_) {
          return Container(
            margin: const EdgeInsets.all(20.0),
            width: double.infinity,
            decoration: BoxDecoration(
              color: AppColors.white,
              borderRadius: BorderRadius.only(
                  topRight: Radius.circular(20.0.r),
                  topLeft: Radius.circular(20.0.r)),
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Center(
                  child: Container(
                    height: 5.0.h,
                    width: 60.0.w,
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(20.0.r),
                      color: AppColors.lightGray,
                    ),
                  ),
                ),
                SizedBox(height: 20.0.h),
                Center(
                  child: Text(
                    'Women\'s Fashions',
                    style: FontStyles.montserratBold19(),
                  ),
                ),
                const SizedBox(height: 20.0),
                _buildCategories(context, 'Clothing'),
                _buildCategories(context, 'Shoes'),
                _buildCategories(context, 'Jewelry'),
                _buildCategories(context, 'Watches'),
                _buildCategories(context, 'HandBags'),
                _buildCategories(context, 'Accessories'),
                _buildCategories(context, 'Men\'s Fashion'),
                _buildCategories(context, 'Girl\'s Fashion'),
                _buildCategories(context, 'Boy\'s Fashion'),
              ],
            ),
          );
        });
  }

  Widget _buildCategories(BuildContext context, String title) {
    return GestureDetector(
      onTap: () {
        Navigator.pop(context);
        setState(() {
          isItemClicked = true;
        });
      },
      child: Text(
        title,
        style: FontStyles.montserratRegular14()
            .copyWith(color: AppColors.textLightColor, height: 2.0),
      ),
    );
  }

  Widget _buildItemsBody(BuildContext context) {
    return SingleChildScrollView(
      child: Column(
        children: [
          Container(
            margin: EdgeInsets.only(left: 20.0.w, top: 50.0.h),
            child: _buildCategoriesTags(context),
          ),
          _buildItemAndSortTile(context),
          _buildItems(context),
        ],
      ),
    );
  }

  Widget _buildCategoriesTags(BuildContext context) {
    List<String> titles = ['All', 'Dresses', 'Tops', 'Sweaters', 'Jeans'];
    return SizedBox(
      width: MediaQuery.of(context).size.width,
      height: 32.0.h,
      child: ListView.builder(
        itemCount: titles.length,
        shrinkWrap: true,
        scrollDirection: Axis.horizontal,
        itemBuilder: (context, index) {
          return Container(
            // width: 50.0,
            margin: EdgeInsets.only(right: 10.0.w),
            padding: EdgeInsets.symmetric(horizontal: 15.0.w),
            decoration: BoxDecoration(
                color: index == 0 ? AppColors.secondary : AppColors.white,
                borderRadius: BorderRadius.circular(25.0.r)),
            child: Center(
              child: Text(
                titles[index],
                style: FontStyles.montserratRegular14().copyWith(
                    color: index == 0
                        ? AppColors.white
                        : AppColors.textLightColor),
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _buildItemAndSortTile(BuildContext context) {
    return ListTile(
        title: Text(
          '166 Items',
          style: FontStyles.montserratBold19(),
        ),
        trailing: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'Sort by:',
              style: FontStyles.montserratBold12()
                  .copyWith(color: AppColors.textLightColor),
            ),
            Text(
              'Featured',
              style: FontStyles.montserratBold12()
                  .copyWith(color: AppColors.primaryDark),
            ),
            const Icon(
              Icons.keyboard_arrow_down,
              color: AppColors.primaryDark,
            )
          ],
        ));
  }

  Widget _buildItems(BuildContext context) {
    var screenHeight = MediaQuery.of(context).size.height;
    return Container(
      margin: EdgeInsets.only(
          left: 15.0.w, right: 15.0.w, bottom: screenHeight * .08.h),
      child: GridView.builder(
        shrinkWrap: true,
        itemCount: 4,
        physics: const NeverScrollableScrollPhysics(),
        gridDelegate:  SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 2, mainAxisExtent: 260.0.h, crossAxisSpacing: 10.0),
        itemBuilder: (_, index) {
          return null;

          // return ItemWidget(
          //   index: index,
          //   favoriteIcon: true,
          //
          // );
        },
      ),
    );
  }
}
