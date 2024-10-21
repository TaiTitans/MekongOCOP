import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:smart_shop/Common/Widgets/shimmer_effect.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';
import 'package:smart_shop/screens/chat/chatrealtime.dart';


import '../../model/product.dart';
import '../../model/review.dart';
import '../../service/product_service.dart';


class Product extends StatefulWidget {
  static const String routeName = 'product';
  final int productId;

  Product({Key? key, required this.productId}) : super(key: key);

  @override
  _ProductState createState() => _ProductState();
}

class _ProductState extends State<Product> {
  ProductModel? _product;
  String? _accessToken;
  bool _isLoading = true;
  String? _error;
  bool _showReviews = false;
  late PageController _pageController;
  int _currentImageIndex = 0;

  @override
  void initState() {
    _pageController = PageController();
    super.initState();
    _loadData();
  }

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }
  Future<void> _addToCart(int quantity, String accessToken) async {
    ProductService productService = ProductService();
    final result = await productService.addToCart(widget.productId, quantity, accessToken);

    if (result.containsKey('error')) {
      // Show error message
      _showErrorSnackbar("Thêm vào giỏ hàng thất bại");
    } else {
      // Success
     _showSuccessSnackbar("Thêm vào giỏ hàng thành công!");
    }
  }

  Future<void> _loadData() async {
    try {
      await _fetchAccessToken();
      await _fetchProductDetails(widget.productId);
    } catch (e) {
      setState(() {
        _error = e.toString();
      });
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  Future<void> _fetchAccessToken() async {
    final prefs = await SharedPreferences.getInstance();
    _accessToken = prefs.getString('accessToken');
  }

  Future<void> _fetchProductDetails(int productId) async {
    try {
      if (_accessToken == null) {
        throw Exception('Access token is not available');
      }
      ProductService productService = ProductService();
      final productDetails = await productService.fetchProductDetails(productId, _accessToken!);
      if (productDetails != null) {
        setState(() {
          _product = ProductModel.fromJson(productDetails);
        });
      } else {
        throw Exception('Failed to load product details');
      }
    } catch (e) {
      throw Exception('Error: $e');
    }
  }

  String formatPrice(double price) {
    final formatter = NumberFormat.currency(locale: 'vi_VN', symbol: '₫');
    return formatter.format(price);
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    if (_error != null) {
      return Scaffold(
        body: Center(child: Text('Error: $_error')),
      );
    }

    if (_product == null) {
      return Scaffold(
        body: Center(child: Text('No product data available')),
      );
    }

    return Scaffold(
      backgroundColor: AppColors.whiteLight,
      body: _buildBody(context, _product!),
      bottomSheet: _buildBottomSheet(
        context: context,
        onTap: (quantity) {
          _addToCart(quantity, _accessToken!);
        },
        storeId: _product!.store,
      ),
    );
  }

  Widget _buildProductImages(ProductModel product) {
    return Container(
      height: 300,
      child: Stack(
        children: [
          PageView.builder(
            controller: _pageController,
            itemCount: product.productImages.length,
            onPageChanged: (index) {
              setState(() {
                _currentImageIndex = index;
              });
            },
            itemBuilder: (context, index) {
              return CachedNetworkImage(
                imageUrl: product.productImages[index].imageUrl,
                fit: BoxFit.cover,
              );
            },
          ),
          Positioned(
            left: 10,
            right: 10,
            bottom: 10,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                _buildNavigationButton(Icons.arrow_back_ios, () {
                  if (_currentImageIndex > 0) {
                    _pageController.previousPage(
                      duration: Duration(milliseconds: 300),
                      curve: Curves.easeInOut,
                    );
                  }
                }),
                Text(
                  '${_currentImageIndex + 1}/${product.productImages.length}',
                  style: TextStyle(
                    color: Colors.white,
                    backgroundColor: Colors.black54,
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                _buildNavigationButton(Icons.arrow_forward_ios, () {
                  if (_currentImageIndex < product.productImages.length - 1) {
                    _pageController.nextPage(
                      duration: Duration(milliseconds: 300),
                      curve: Curves.easeInOut,
                    );
                  }
                }),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildNavigationButton(IconData icon, VoidCallback onPressed) {
    return ElevatedButton(
      onPressed: onPressed,
      child: Icon(icon, color: Colors.white),
      style: ElevatedButton.styleFrom(
        shape: CircleBorder(),
        padding: EdgeInsets.all(8),
        backgroundColor: Colors.black54,
      ),
    );
  }

  Widget _buildBody(BuildContext context, ProductModel product) {
    return NestedScrollView(
      headerSliverBuilder: (BuildContext context, bool innerBoxIsScrolled) {
        return <Widget>[
          SliverToBoxAdapter(
            child: _buildProductImages(product),
          ),
        ];
      },
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildAboutProduct(context, product),
          Flexible(
            child: SingleChildScrollView(
              child: Column(
                children: [
                  SizedBox(height: 10.0),
                  _buildProductDetail(context, product),
                  SizedBox(height: 10.0),
                  _buildReviewsSection(context, product.reviews),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildAboutProduct(BuildContext context, ProductModel product) {
    return Container(
      color: AppColors.white,
      padding: const EdgeInsets.all(20.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              _buildRatings(context, product.reviews),
              Text(
                product.stock > 0 ? 'Còn hàng' : 'Hết hàng',
                style: FontStyles.montserratBold12().copyWith(
                  color: product.stock > 0 ? AppColors.green : Colors.red,
                ),
              ),
            ],
          ),
          SizedBox(height: 10),
          Text(
            product.title,
            style: FontStyles.montserratRegular19().copyWith(fontWeight: FontWeight.bold),
          ),
          SizedBox(height: 10),
          Row(
            children: [
              Text(
                'Giá: ',
                style: FontStyles.montserratBold17(),
              ),
              Text(
                formatPrice(product.productPrice),
                style: FontStyles.montserratBold17().copyWith(color: Colors.blue), // Thay đổi màu sắc cho số tiền
              ),
            ],
          ),
          SizedBox(height: 10),
          Text('Danh mục: ${product.categoryName}'),
          Text('Xuất xứ: ${product.provinceName}'),
          Text('Số lượng còn lại: ${product.productQuantity}'),
        ],
      ),
    );
  }



  Widget _buildRatings(BuildContext context, List<ReviewModel> reviews) {
    if (reviews.isEmpty) {
      return Text('Chưa có đánh giá');
    }

    double averageRating = reviews.map((review) => review.rating).reduce((a, b) => a + b) / reviews.length;

    return SizedBox(
      height: 20.0,
      child: Row(
        children: [
          ListView.builder(
            itemCount: averageRating.round(),
            shrinkWrap: true,
            scrollDirection: Axis.horizontal,
            itemBuilder: (context, index) {
              return const Icon(
                Icons.star,
                color: AppColors.secondary,
                size: 14.0,
              );
            },
          ),
          Text(
            ' (${reviews.length} đánh giá)',
            style: FontStyles.montserratRegular12(),
          ),
        ],
      ),
    );
  }

  Widget _buildProductDetail(BuildContext context, ProductModel product) {
    return Container(
      decoration: BoxDecoration(
        color: AppColors.white,
        borderRadius: BorderRadius.circular(10.0),
      ),
      padding: const EdgeInsets.all(20.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Thông tin',
            style: FontStyles.montserratBold19(),
          ),
          SizedBox(height: 10.0),
          Text(
            product.productDescription,
            style: FontStyles.montserratRegular14(),
          ),
        ],
      ),
    );
  }

  Widget _buildReviewsSection(BuildContext context, List<ReviewModel> reviews) {
    return Container(
      decoration: BoxDecoration(
        color: AppColors.white,
        borderRadius: BorderRadius.circular(10.0),
      ),
      padding: const EdgeInsets.all(20.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          GestureDetector(
            onTap: () {
              setState(() {
                _showReviews = !_showReviews;
              });
            },
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  'Đánh giá (${reviews.length})',
                  style: FontStyles.montserratBold19(),
                ),
                Icon(_showReviews ? Icons.expand_less : Icons.expand_more),
              ],
            ),
          ),
          if (_showReviews)
            Column(
              children: reviews.map((review) {
                return _buildReviewItem(review);
              }).toList(),
            ),
        ],
      ),
    );
  }

  Widget _buildReviewItem(ReviewModel review) {
    final formattedDate = DateFormat('dd-MM-yyyy').format(review.date);
    return Container(
      margin: const EdgeInsets.only(top: 10),
      padding: const EdgeInsets.all(10.0),
      decoration: BoxDecoration(
        border: Border.all(color: Colors.grey),
        borderRadius: BorderRadius.circular(8.0),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              for (int i = 0; i < review.rating; i++)
                const Icon(
                  Icons.star,
                  color: AppColors.secondary,
                  size: 14.0,
                ),
              for (int i = review.rating; i < 5; i++)
                const Icon(
                  Icons.star_border,
                  color: AppColors.secondary,
                  size: 14.0,
                ),
              SizedBox(width: 8.0),
              Text(
                review.userName,
                style: FontStyles.montserratRegular12(),
              ),
            ],
          ),
          const SizedBox(height: 5.0),
          Text(
            review.comment,
            style: FontStyles.montserratRegular14(),
          ),
          const SizedBox(height: 5.0),
          Text(
            formattedDate,
            style: FontStyles.montserratRegular12().copyWith(color: Colors.grey),
          ),
        ],
      ),
    );
  }



  Widget _buildBottomSheet({
    required BuildContext context,
    required Function(int) onTap,
    required int storeId, // Pass the storeId here
  }) {
    int quantity = 1;
    return Container(
      height: 80.0,
      padding: EdgeInsets.symmetric(horizontal: 20.0, vertical: 10.0),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.grey.withOpacity(0.3),
            spreadRadius: 1,
            blurRadius: 5,
            offset: Offset(0, -3),
          ),
        ],
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          IconButton(
            icon: Icon(Icons.arrow_back, color: Colors.grey),
            onPressed: () => Navigator.of(context).pop(),
          ),
          Expanded(
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween, // This aligns buttons evenly
              children: [
                // Ô nhập số lượng sản phẩm
                SizedBox(
                  width: 80.0,
                  height: 52.0,
                  child: TextField(
                    keyboardType: TextInputType.number,
                    textAlign: TextAlign.center,
                    onChanged: (value) {
                      quantity = int.tryParse(value) ?? 1;
                    },
                    decoration: InputDecoration(
                      hintText: '1',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(10),
                      ),
                    ),
                  ),
                ),
                SizedBox(width: 16.0),

                // Nút thêm vào giỏ hàng with reduced width
                SizedBox(
                  width: 150.0, // Set the width of the button
                  child: ElevatedButton(
                    onPressed: () => onTap(quantity),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.deepPurple,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10),
                      ),
                      padding: EdgeInsets.symmetric(vertical: 16),
                    ),
                    child: Text(
                      'Thêm vào giỏ',
                      style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                    ),
                  ),
                ),
                SizedBox(width: 2.0),

                // Nút Liên hệ with reduced width
                SizedBox(
                  width: 80.0, // Set the width of the button
                  child: ElevatedButton(
                    onPressed: () {
                      // Pass storeId to ChatRealTime screen
                      Navigator.pushNamed(
                        context,
                        ChatRealTime.routeName,
                        arguments: storeId,  // Pass the storeId here
                      );
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.blueGrey,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10),
                      ),
                      padding: EdgeInsets.symmetric(vertical: 16),
                    ),
                    child: Text(
                      'Liên hệ',
                      style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }



  void _showCustomSnackbar(String message, Color backgroundColor) {
    final overlay = Overlay.of(context);
    final overlayEntry = OverlayEntry(
      builder: (context) => Positioned(
        top: MediaQuery.of(context).viewInsets.top + 50,
        left: 0,
        right: 0,
        child: Material(
          color: Colors.transparent,
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
            decoration: BoxDecoration(
              color: backgroundColor,
              borderRadius: BorderRadius.circular(2),
            ),
            child: Text(
              message,
              style: const TextStyle(color: Colors.black),
            ),
          ),
        ),
      ),
    );

    overlay.insert(overlayEntry);
    Future.delayed(const Duration(seconds: 3), () {
      overlayEntry.remove();
    });
  }

  void _showErrorSnackbar(String message) {
    _showCustomSnackbar(message, Colors.white);
  }

  void _showSuccessSnackbar(String message) {
    _showCustomSnackbar(message, Colors.white);
  }



}


