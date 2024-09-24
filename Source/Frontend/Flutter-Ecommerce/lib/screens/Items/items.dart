import 'package:flutter/material.dart';
import 'package:smart_shop/Common/Widgets/item_widget.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';

class Items extends StatelessWidget {
  static const String routeName = 'items';
  const Items({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _buildBody(context),
    );
  }

  Widget _buildBody(BuildContext context) {
    return SingleChildScrollView(
      child: Column(
        children: [
          Container(
            margin: const EdgeInsets.only(left: 20.0, top: 50.0),
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
      height: 50.0,
      child: ListView.builder(
        itemCount: titles.length,
        shrinkWrap: true,
        scrollDirection: Axis.horizontal,
        itemBuilder: (context, index) {
          return Container(
            // width: 50.0,
            margin: const EdgeInsets.only(right: 10.0),
            padding: const EdgeInsets.symmetric(horizontal: 25.0),
            decoration: BoxDecoration(
                color: index == 0 ? AppColors.secondary : AppColors.white,
                borderRadius: BorderRadius.circular(25.0)),
            child: Center(
              child: Text(
                titles[index],
                style: FontStyles.montserratRegular14().copyWith(
                    fontSize: 15.0,
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
          style: FontStyles.montserratBold17().copyWith(fontSize: 19.0),
        ),
        trailing: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'Sort by:',
              style: FontStyles.montserratBold17()
                  .copyWith(fontSize: 12.0, color: AppColors.textLightColor),
            ),
            Text(
              'Featured',
              style: FontStyles.montserratBold17()
                  .copyWith(fontSize: 12.0, color: AppColors.primaryDark),
            ),
            const Icon(
              Icons.keyboard_arrow_down,
              color: AppColors.primaryDark,
            )
          ],
        ));
  }

  Widget _buildItems(BuildContext context) {
    return Container(
      margin: const EdgeInsets.all(20.0),
      child: GridView.builder(
        shrinkWrap: true,
        itemCount: 4,
        physics: const NeverScrollableScrollPhysics(),
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 2, mainAxisExtent: 300.0, crossAxisSpacing: 10.0),
        itemBuilder: (_, index) {
          return ItemWidget(
            index: index,
          );
        },
      ),
    );
  }
}
