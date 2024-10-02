import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:smart_shop/Common/Widgets/custom_app_bar.dart';
import 'package:smart_shop/Common/Widgets/item_widget.dart';
import 'package:smart_shop/Screens/Product/product.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';

import '../../service/product_service.dart';
import '../Product/product_catalogue.dart';

class Favorite extends StatefulWidget {
  static const String routeName = 'filter';
  const Favorite({Key? key}) : super(key: key);

  @override
  _FavoriteState createState() => _FavoriteState();
}

class _FavoriteState extends State<Favorite> {
  late Future<List<dynamic>> _favoriteProducts;

  @override
  void initState() {
    super.initState();
    _initFavoriteProducts();
  }

  Future<void> _initFavoriteProducts() async {
    _favoriteProducts = _fetchFavoriteProducts();
  }

  Future<List<dynamic>> _fetchFavoriteProducts() async {
    ProductService productService = ProductService();
    final sharedPreferences = await SharedPreferences.getInstance();
    final accessToken = sharedPreferences.getString('accessToken') ?? '';
    return productService.fetchProductsByFavorite(accessToken);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.whiteLight,
      body: FutureBuilder<List<dynamic>>(
        future: _favoriteProducts,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            return ProductListScreen(
              products: snapshot.data!,
              initialFavorites: List<bool>.filled(snapshot.data!.length, true),
            );
          } else if (snapshot.hasError) {
            return Center(
              child: Text('Error: ${snapshot.error}'),
            );
          } else {
            return Center(
              child: CircularProgressIndicator(),
            );
          }
        },
      ),
    );
  }
}