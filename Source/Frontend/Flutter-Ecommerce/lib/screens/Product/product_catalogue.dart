import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:smart_shop/Screens/Product/product.dart';

import '../../Utils/app_colors.dart';
import '../../service/product_service.dart';

class ProductListScreen extends StatefulWidget {
  final List<dynamic> products;
  final List<bool>? initialFavorites;
  static const String routeName = 'productListScreen';

  const ProductListScreen({Key? key, this.initialFavorites, required this.products}) : super(key: key);

  @override
  _ProductListScreenState createState() => _ProductListScreenState();
}

class _ProductListScreenState extends State<ProductListScreen> {
  // Danh sách lưu trữ trạng thái yêu thích của sản phẩm
  late List<bool> _favorites;

  @override
  void initState() {
    super.initState();
    // Khởi tạo danh sách với giá trị false cho mỗi sản phẩm
    _favorites = widget.initialFavorites ?? List<bool>.filled(widget.products.length, false);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: PreferredSize(
        preferredSize: Size.fromHeight(80.0),
        child: Container(
          decoration: BoxDecoration(
            gradient: LinearGradient(
              colors: [AppColors.primaryLight, AppColors.primaryDark],
              begin: Alignment.bottomRight,
              end: Alignment.topLeft,
              stops: [0, 1],
            ),
          ),
          child: AppBar(
            backgroundColor: Colors.transparent,
            title: Row(
              children: [
                const SizedBox(width: 40.0),
                Expanded(
                  child: Container(
                    alignment: Alignment.centerLeft,
                    child: Text(
                      'Danh sách sản phẩm',
                      style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                      textAlign: TextAlign.left,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
      body: _buildProductGrid(context),
    );
  }

  Widget _buildProductGrid(BuildContext context) {
    return Container(
      margin: const EdgeInsets.all(16.0),
      child: widget.products.isEmpty
          ? Center(child: Text('Không có sản phẩm nào.'))
          : GridView.builder(
        gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 2,
          mainAxisExtent: 260.0,
          crossAxisSpacing: 10.0,
          mainAxisSpacing: 10.0,
        ),
        itemCount: widget.products.length,
        itemBuilder: (context, index) {
          final product = widget.products[index];
          bool isInStock = product['productQuantity'] != 0;
          String stockStatus = isInStock ? 'Còn hàng' : 'Cháy hàng';

          return GestureDetector(
            onTap: () {
              Navigator.pushNamed(context, Product.routeName, arguments: product['productId']);
            },
            child: Card(
              elevation: 4.0,
              child: Stack(
                children: [
                  ClipRRect(
                    borderRadius: BorderRadius.only(
                      topLeft: Radius.circular(12.0),
                      topRight: Radius.circular(12.0),
                    ),
                    child: Image.network(
                      product['productImages'].isNotEmpty
                          ? product['productImages'][0]['imageUrl']
                          : '',
                      height: 120,
                      width: double.infinity,
                      fit: BoxFit.cover,
                    ),
                  ),
                  Positioned(
                    top: 8.0,
                    left: 8.0,
                    child: TweenAnimationBuilder<Color?>(
                      tween: ColorTween(
                        begin: isInStock ? Colors.green : Colors.red,
                        end: isInStock ? Colors.blue : Colors.orange,
                      ),
                      duration: const Duration(seconds: 1),
                      builder: (context, color, child) {
                        return Container(
                          decoration: BoxDecoration(
                            gradient: LinearGradient(
                              colors: [
                                color ?? Colors.transparent,
                                Colors.white,
                              ],
                            ),
                            borderRadius: BorderRadius.circular(8.0),
                          ),
                          padding: const EdgeInsets.symmetric(horizontal: 4.0, vertical: 2.0),
                          child: Text(
                            stockStatus,
                            style: TextStyle(
                              fontWeight: FontWeight.bold,
                              fontSize: 16.0,
                              color: Colors.white,
                            ),
                          ),
                        );
                      },
                    ),
                  ),
                  Positioned(
                    bottom: 0,
                    left: 0,
                    right: 0,
                    child: Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            product['productName'] ?? 'Tên sản phẩm',
                            style: TextStyle(fontWeight: FontWeight.bold),
                            maxLines: 2,
                            overflow: TextOverflow.ellipsis,
                          ),
                          SizedBox(height: 4.0),
                          Text(
                            'Số lượng: ${product['productQuantity'] ?? 0}',
                            style: TextStyle(color: Colors.grey),
                          ),
                          SizedBox(height: 4.0),
                          Text(
                            'Giá: ${formatCurrency(product['productPrice'] ?? 0)}',
                            style: TextStyle(color: Colors.blue),
                          ),
                        ],
                      ),
                    ),
                  ),
                  Positioned(
                    top: 8.0,
                    right: 8.0,
                    child: IconButton(
                      icon: Icon(
                        _favorites[index] ? Icons.favorite : Icons.favorite_border,
                        color: _favorites[index] ? Colors.red : Colors.grey,
                        size: 28,
                      ),
                      onPressed: () {
                        // Gọi hàm _handleFavorite để yêu thích sản phẩm
                        _handleFavorite(context, product['productId']);
                        setState(() {
                          // Đảo ngược trạng thái yêu thích
                          _favorites[index] = !_favorites[index];
                        });
                      },
                    ),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }

  Future<void> _handleFavorite(BuildContext context, int productId) async {
    // Lấy accessToken từ SharedPreferences
    final sharedPreferences = await SharedPreferences.getInstance();
    final accessToken = sharedPreferences.getString('accessToken') ?? '';

    // Gọi API để yêu thích sản phẩm
    ProductService productService = ProductService();
    bool isSuccess = await productService.favoriteProduct(productId, accessToken);

    if (isSuccess) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            'Thao tác thành công',
            style: TextStyle(color: Colors.black),
          ),
          backgroundColor: Colors.white,
        ),
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Yêu thích sản phẩm thất bại!')),
      );
    }
  }

  String formatCurrency(double price) {
    final formatter = NumberFormat.simpleCurrency(locale: 'vi_VN');
    return formatter.format(price);
  }
}