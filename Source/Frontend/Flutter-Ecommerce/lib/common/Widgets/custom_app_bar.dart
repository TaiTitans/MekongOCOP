import 'dart:async';

import 'package:flutter/material.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';
import '../../model/product.dart';
import '../../screens/search/search_screen.dart';
import '../../service/product_service.dart';
import 'app_title.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

class CustomAppBar extends StatefulWidget {
  const CustomAppBar({
    Key? key,
    this.isHome,
    this.leadingIcon,
    this.leadingOnTap,
    this.trailingIcon,
    this.trailingOnTap,
    this.title,
    this.scaffoldKey,
    this.enableSearchField,
    this.fixedHeight,
    this.onSearchSubmitted,
    this.notificationCount = 0, // Add this line
  }) : super(key: key);

  final GlobalKey<ScaffoldState>? scaffoldKey;
  final bool? isHome;
  final IconData? leadingIcon;
  final Function()? leadingOnTap;
  final IconData? trailingIcon;
  final Function()? trailingOnTap;
  final String? title;
  final bool? enableSearchField;
  final double? fixedHeight;
  final Function(String)? onSearchSubmitted;
  final int notificationCount; // Add this line

  @override
  State<CustomAppBar> createState() => _CustomAppBarState();
}


class _CustomAppBarState extends State<CustomAppBar> {
  final TextEditingController _searchController = TextEditingController();
  final ProductService _productService = ProductService();
  List<ProductModel> _searchResults = [];
  bool _isLoading = false;
  final FocusNode _focusNode = FocusNode();
  Timer? _debounce;

  @override
  void initState() {
    super.initState();
    _focusNode.addListener(() {
      setState(() {});
    });

    _searchController.addListener(_onSearchChanged);
  }

  @override
  void dispose() {
    _debounce?.cancel();
    _focusNode.dispose();
    _searchController.dispose();
    super.dispose();
  }

  void _onSearchChanged() {
    if (_debounce?.isActive ?? false) _debounce!.cancel();
    _debounce = Timer(const Duration(milliseconds: 500), () {
      if (_searchController.text.isNotEmpty) {
        _performSearch(_searchController.text);
      } else {
        setState(() {
          _searchResults = [];
        });
      }
    });
  }

  Future<void> _performSearch(String query) async {
    setState(() {
      _isLoading = true;
    });

    try {
      final results = await _productService.fetchProductBySearchProductName(query);
      setState(() {
        _searchResults = results;
        _isLoading = false;
      });
    } catch (e) {
      print('Error searching: $e');
      setState(() {
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      height: widget.fixedHeight ?? 134.h,
      width: double.infinity,
      decoration: const BoxDecoration(
        color: Colors.red,
        gradient: LinearGradient(
          colors: [AppColors.primaryLight, AppColors.primaryDark],
          begin: Alignment.bottomRight,
          end: Alignment.topLeft,
          stops: [0, 1],
        ),
      ),
      child: Column(
        children: [
          SizedBox(height: 45),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              _buildDrawerButton(context),
              widget.isHome! ? _buildAppTitle() : _title(),
              _buildNotificationIcon(context),
            ],
          ),
          if (widget.enableSearchField!)
            Padding(
              padding: EdgeInsets.only(top: 10),
              child: _buildSearchField(context),
            ),
        ],
      ),
    );
  }
  // Method to build the drawer button
  Widget _buildDrawerButton(BuildContext context) {
    return IconButton(
      padding: const EdgeInsets.only(top: 25.0),
      onPressed: widget.isHome!
          ? () {
        setState(() {
          widget.scaffoldKey!.currentState!.openDrawer();
        });
      }
          : widget.leadingOnTap,
      icon: Icon(
        widget.leadingIcon,
        color: Colors.white,
      ),
    );
  }

  // Method to build the app title
  Widget _buildAppTitle() {
    return AppTitle(
      fontStyle: FontStyles.montserratExtraBold18(),
      marginTop: 0.0,
    );
  }

  Widget _title() {
    return Text(
      widget.title!,
      style: FontStyles.montserratBold19().copyWith(
        color: AppColors.white,
      ),
    );
  }

  // Method to build the notification icon
// Method to build the notification icon
  Widget _buildNotificationIcon(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(right: 10.0),
      child: GestureDetector(
        onTap: widget.trailingOnTap,
        child: Stack(
          children: [
            Icon(
              widget.trailingIcon,
              color: Colors.white,
            ),
            if (widget.notificationCount > 0) // Check for unread notifications
              Positioned(
                right: 0,
                child: Container(
                  padding: EdgeInsets.all(2),
                  decoration: BoxDecoration(
                    color: Colors.red,
                    borderRadius: BorderRadius.circular(10),
                  ),
                  constraints: BoxConstraints(
                    maxWidth: 20,
                    maxHeight: 20,
                  ),
                  child: Text(
                    '${widget.notificationCount}', // Show notification count
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
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


  // Method to build the search field

  Widget _buildSearchField(BuildContext context) {
    return Stack(
      children: [
        Material(
          color: Colors.transparent,
          child: Container(
            height: 44.0,
            width: MediaQuery.of(context).size.width * 0.9,
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(50.0),
            ),
            child: GestureDetector(
              onTap: () {
                // Điều hướng đến màn hình tìm kiếm khi nhấn vào ô tìm kiếm
                Navigator.pushNamed(context, SearchScreen.routeName);
              },
              child: Row(
                children: [
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 10.0),
                    child: Icon(Icons.search, color: Colors.grey),
                  ),
                  Text(
                    'Bạn muốn tìm sản phẩm nào?',
                    style: const TextStyle(color: Colors.grey),
                  ),
                ],
              ),
            ),
          ),
        ),
      ],
    );
  }
}

  // Handle the search logic
  // void _onSearchSubmitted(String productName) async {
  //   if (productName.isEmpty) return;
  //
  //   setState(() {
  //     _isLoading = true;
  //   });
  //
  //   try {
  //     List<int> results = await _productService.fetchProductBySearchProductName(productName);
  //
  //     setState(() {
  //       _searchResults = results;
  //       _isLoading = false;
  //     });
  //   } catch (e) {
  //     setState(() {
  //       _isLoading = false;
  //     });
  //     print('Error searching products: $e');
  //   }
  // }

  // Method to display the search results
  // Widget _buildSearchResults() {
  //   return Container(
  //     padding: const EdgeInsets.all(8.0),
  //     child: Column(
  //       children: _searchResults.map((result) {
  //         return ListTile(
  //           title: Text('Product ID: $result'),
  //         );
  //       }).toList(),
  //     ),
  //   );
  // }
