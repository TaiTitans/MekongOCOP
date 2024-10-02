import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../Common/Widgets/custom_app_bar.dart';
import '../../Utils/app_colors.dart';
import '../../Utils/font_styles.dart';
import '../../dummy/dummy_data.dart';
import '../../service/product_service.dart';
import '../Filter/filter.dart';
import 'dart:io' as plateform;

import '../Product/product_catalogue.dart';
class Province extends StatefulWidget {
  static const String routeName = 'province';

  const Province({Key? key}) : super(key: key);
  @override
  State<Province> createState() => _ProvinceState();
}

class _ProvinceState extends State<Province> {
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
          enableSearchField: false,
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
          title: isItemClicked ? 'OCOP' : 'Tỉnh thành'),
    );
  }

  Widget _buildBody(BuildContext context) {
    var screenHeight = MediaQuery.of(context).size.height;
    return Container(
      margin: EdgeInsets.only(
          left: 10.0,
          right: 10.0,
          top: 02.0,
          bottom: seeAllClicked ? 0.0 : screenHeight * .02),
      child: ListView.builder(
        itemCount: DummyData.provinceImagesLink.length,
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
      onTap: () async {
        try {
          final sharedPreferences = await SharedPreferences.getInstance();
          final accessToken = sharedPreferences.getString('accessToken') ?? '';
          // Gọi API để lấy sản phẩm theo danh mục
          ProductService productService = ProductService();
          List<dynamic> products = await productService.fetchProductsByProvince(index! + 1, accessToken);

          // Điều hướng đến màn hình hiển thị danh sách sản phẩm và truyền dữ liệu
          Navigator.pushNamed(
            context,
            ProductListScreen.routeName,
            arguments: products, // Truyền danh sách sản phẩm
          );
        } catch (error) {
          // Xử lý lỗi ở đây, có thể hiển thị thông báo cho người dùng
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Failed to load products: $error')),
          );
        }
      },
      child: Card(
        margin: const EdgeInsets.only(top: 10.0),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10.0),
        ),
        clipBehavior: Clip.antiAlias,
        child: Stack(
          children: [
            Image.asset(
              DummyData.provinceImagesLink[index!],
              fit: BoxFit.cover,
              width: screenWidth,
              height: 120,
              errorBuilder: (context, error, stackTrace) {
                return Container(
                  color: Colors.grey.shade300,
                  child: Icon(Icons.broken_image, color: Colors.red),
                );
              },
            ),
            Positioned.fill(
              child: Container(
                alignment: Alignment.center,
                color: Colors.black.withOpacity(0.5),
                child: Text(
                  DummyData.provinceTitles[index],
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






  Widget _buildItemsBody(BuildContext context) {
    return SingleChildScrollView(
      child: Column(
        children: [
          Container(
            margin: EdgeInsets.only(left: 20.0.w, top: 50.0.h),
            // child: _buildCategoriesTags(context),
          ),
          _buildItemAndSortTile(context),
          _buildItems(context),
        ],
      ),
    );
  }


  Widget _buildItemAndSortTile(BuildContext context) {
    return ListTile(
        title: Text(
          'Items',
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

        },
      ),
    );
  }
}
