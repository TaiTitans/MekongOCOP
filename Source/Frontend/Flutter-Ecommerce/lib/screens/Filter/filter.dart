import 'package:flutter/material.dart';
import 'package:smart_shop/Common/Widgets/app_button.dart';
import 'package:smart_shop/Common/Widgets/custom_app_bar.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';

class Filter extends StatefulWidget {
  static const String routeName = 'favorite';
  const Filter({Key? key}) : super(key: key);

  @override
  _FilterState createState() => _FilterState();
}

class _FilterState extends State<Filter> {
  RangeValues values = const RangeValues(0, 5000);
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.whiteLight,
      appBar: _buildAppBar(context),
      body: _buildBody(context),
    );
  }

  PreferredSize _buildAppBar(BuildContext context) {
    return PreferredSize(
      preferredSize:
          Size(double.infinity, MediaQuery.of(context).size.height * .20),
      child: CustomAppBar(
        isHome: false,
        title: 'Filter',
        fixedHeight: 88.0,
        enableSearchField: false,
        leadingIcon: Icons.arrow_back,
        leadingOnTap: () {
          Navigator.pop(context);
        },
      ),
    );
  }

  Widget _buildBody(BuildContext context) {
    return Container(
      color: AppColors.whiteLight,
      margin: const EdgeInsets.all(20.0),
      child: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildPriceSelector(context),
            const SizedBox(height: 20.0),
            _buildCard('Categories', ['Dresses']),
            const SizedBox(height: 20.0),
            _buildCard(
                'Brand', ['lark and Ro', 'Astylish', 'ECOWISH', 'Angashion']),
            const SizedBox(height: 20.0),
            _buildColorSelection(context),
            const SizedBox(height: 20.0),
            _buildSizes(context),
            const SizedBox(height: 20.0),
            _buildCard('Sort By', ['Featured']),
            const SizedBox(height: 20.0),
            _buildResultButton(context),
          ],
        ),
      ),
    );
  }

  Widget _buildPriceSelector(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisSize: MainAxisSize.min,
      children: [
        const Text('Price'),
        RangeSlider(
          activeColor: AppColors.secondary,
          inactiveColor: AppColors.white,
          min: 0,
          max: 5000,
          values: values,
          onChanged: (RangeValues newValue) {
            setState(() {
              values = newValue;
            });
          },
        ),
        Row(
          children: [
            Expanded(
              child: Container(
                  height: MediaQuery.of(context).size.height * .060,
                  decoration: BoxDecoration(
                    color: AppColors.white,
                    borderRadius: const BorderRadius.only(
                        topLeft: Radius.circular(10.0),
                        bottomLeft: Radius.circular(10.0)),
                    border: Border.all(color: AppColors.lightGray),
                  ),
                  child: Center(
                      child: Text('\$${values.start.round().toString()}'))),
            ),
            // const SizedBox(width: 1.0),
            Expanded(
              child: Container(
                  height: MediaQuery.of(context).size.height * .060,
                  decoration: BoxDecoration(
                    color: AppColors.white,
                    borderRadius: const BorderRadius.only(
                        bottomRight: Radius.circular(10.0),
                        topRight: Radius.circular(10.0)),
                    border: Border.all(
                      color: AppColors.lightGray,
                    ),
                  ),
                  child: Center(
                      child: Text('\$${values.end.round().toString()}'))),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildCard(String title, List<String> selections) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(title),
        const SizedBox(height: 10.0),
        _buildTile(selections),
      ],
    );
  }

  Widget _buildTile(List<String> selection) {
    return Container(
      decoration: BoxDecoration(
        color: AppColors.white,
        borderRadius: BorderRadius.circular(10.0),
      ),
      child: ListTile(
        leading: Text(selection.join(', ')),
        trailing: const Icon(Icons.arrow_forward_ios_rounded),
      ),
    );
  }

  Widget _buildColorSelection(BuildContext context) {
    List<MaterialColor> colors = [
      Colors.orange,
      Colors.red,
      Colors.blue,
      Colors.pink,
      Colors.purple,
      Colors.brown,
      Colors.yellow,
    ];
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisSize: MainAxisSize.min,
      children: [
        const Text('Colors'),
        const SizedBox(height: 20.0),
        SizedBox(
          height: 37.0,
          child: ListView.separated(
            itemCount: colors.length,
            scrollDirection: Axis.horizontal,
            shrinkWrap: true,
            itemBuilder: (context, index) {
              return Container(
                height: 37,
                width: 37,
                decoration: BoxDecoration(
                    color: colors[index],
                    borderRadius: BorderRadius.circular(37.0)),
              );
            },
            separatorBuilder: (context, index) {
              return const SizedBox(width: 10.0);
            },
          ),
        ),
      ],
    );
  }

  Widget _buildSizes(BuildContext context) {
    List<String> titles = ['XXS', 'XS', 'S', 'M', 'L', 'XL'];
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('Sizes'),
        const SizedBox(height: 20.0),
        SizedBox(
          width: MediaQuery.of(context).size.width,
          height: 50.0,
          child: ListView.builder(
            itemCount: titles.length,
            shrinkWrap: true,
            scrollDirection: Axis.horizontal,
            itemBuilder: (context, index) {
              return Container(
                margin: const EdgeInsets.only(right: 10.0),
                padding: const EdgeInsets.symmetric(horizontal: 15.0),
                decoration: BoxDecoration(
                    color: index == 0 ? AppColors.secondary : AppColors.white,
                    borderRadius: BorderRadius.circular(5.0)),
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
        ),
      ],
    );
  }

  Widget _buildResultButton(BuildContext context) {
    return AppButton.button(
        color: AppColors.secondary,
        width: MediaQuery.of(context).size.width,
        height: 48.0,
        text: 'Results (166)',
        onTap: () {});
  }
}
