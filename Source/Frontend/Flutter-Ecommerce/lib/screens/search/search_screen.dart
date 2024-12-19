import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

import '../../model/product.dart';
import '../../service/product_service.dart';
import '../Product/product.dart';


class SearchScreen extends StatefulWidget {
  static const String routeName = 'search';
  const SearchScreen({Key? key}) : super(key: key);

  @override
  _SearchScreenState createState() => _SearchScreenState();
}

class _SearchScreenState extends State<SearchScreen> {
  final TextEditingController _searchController = TextEditingController();
  final ProductService _productService = ProductService();
  List<ProductModel> _searchResults = [];
  bool _isLoading = false;
  bool _isLoadingMore = false;
  int _page = 0;
  final int _size = 10;
  bool _hasMore = true;
  String? _selectedPriceRange;

  final ScrollController _scrollController = ScrollController();

  // Định nghĩa các khoảng giá
  final Map<String, String> priceRanges = {
    '0-100000': '0 - 100.000₫',
    '100000-200000': '100.000₫ - 200.000₫',
    '200000-500000': '200.000₫ - 500.000₫',
    '500000+': '500.000₫+',
  };

  @override
  void initState() {
    super.initState();
    _scrollController.addListener(_scrollListener);
  }

  @override
  void dispose() {
    _searchController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  void _scrollListener() {
    if (_scrollController.position.pixels == _scrollController.position.maxScrollExtent &&
        !_isLoadingMore &&
        _hasMore) {
      _loadMore();
    }
  }

  Future<void> _performSearch({String? query, String? priceRange}) async {
    setState(() {
      _isLoading = true;
      _searchResults.clear();
      _page = 0;
      _hasMore = true;
    });

    try {
      List<ProductModel> results;
      if (priceRange != null) {
        results = await _productService.fetchProductBySearchPrice(
            priceRange,
            page: _page,
            size: _size
        );
      } else if (query != null && query.isNotEmpty) {
        results = await _productService.fetchProductBySearchProductName(
            query,
            page: _page,
            size: _size
        );
      } else {
        results = [];
      }

      setState(() {
        _searchResults.addAll(results);
        _isLoading = false;
        if (results.length < _size) {
          _hasMore = false;
        }
      });
    } catch (e) {
      print('Error: $e');
      setState(() {
        _isLoading = false;
      });
    }
  }

  Future<void> _loadMore() async {
    if (!_hasMore) return;

    setState(() {
      _isLoadingMore = true;
    });

    try {
      _page++;
      List<ProductModel> results;
      if (_selectedPriceRange != null) {
        results = await _productService.fetchProductBySearchPrice(
            _selectedPriceRange!,
            page: _page,
            size: _size
        );
      } else {
        results = await _productService.fetchProductBySearchProductName(
            _searchController.text,
            page: _page,
            size: _size
        );
      }

      setState(() {
        _searchResults.addAll(results);
        _isLoadingMore = false;
        if (results.length < _size) {
          _hasMore = false;
        }
      });
    } catch (e) {
      print('Error: $e');
      setState(() {
        _isLoadingMore = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        elevation: 0,
        backgroundColor: Colors.white,
        leading: IconButton(
          icon: Icon(Icons.arrow_back_ios, color: Colors.black87),
          onPressed: () => Navigator.pop(context),
        ),
        title: Container(
          height: 40,
          decoration: BoxDecoration(
            color: Colors.grey[100],
            borderRadius: BorderRadius.circular(20),
          ),
          child: TextField(
            controller: _searchController,
            autofocus: true,
            decoration: InputDecoration(
              hintText: 'Tìm kiếm sản phẩm...',
              prefixIcon: Icon(Icons.search, color: Colors.grey),
              border: InputBorder.none,
              contentPadding: EdgeInsets.symmetric(horizontal: 20, vertical: 10),
            ),
            onSubmitted: (query) {
              if (query.isNotEmpty) {
                _selectedPriceRange = null;
                _performSearch(query: query);
              }
            },
          ),
        ),
      ),
      body: Column(
        children: [
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: DropdownButtonFormField<String>(
              value: _selectedPriceRange,
              decoration: InputDecoration(
                labelText: 'Lọc theo giá',
                labelStyle: TextStyle(color: Colors.grey[600]),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(color: Colors.grey[300]!),
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(color: Colors.grey[300]!),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(color: Colors.blue),
                ),
                filled: true,
                fillColor: Colors.grey[50],
              ),
              items: [
                DropdownMenuItem<String>(
                  value: null,
                  child: Text('Tất cả giá'),
                ),
                ...priceRanges.entries.map((entry) => DropdownMenuItem<String>(
                  value: entry.key,
                  child: Text(entry.value),
                )).toList(),
              ],
              onChanged: (String? newValue) {
                setState(() {
                  _selectedPriceRange = newValue;
                  _searchController.clear();
                  if (newValue != null) {
                    _performSearch(priceRange: newValue);
                  }
                });
              },
            ),
          ),
          Expanded(
            child: _isLoading
                ? Center(child: CircularProgressIndicator())
                : _searchResults.isEmpty
                ? Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.search_off, size: 64, color: Colors.grey),
                  SizedBox(height: 16),
                  Text(
                    'Không tìm thấy sản phẩm nào.',
                    style: TextStyle(
                      fontSize: 16,
                      color: Colors.grey[600],
                    ),
                  ),
                ],
              ),
            )
                : GridView.builder(
              controller: _scrollController,
              padding: const EdgeInsets.all(16.0),
              gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 2,
                crossAxisSpacing: 16.0,
                mainAxisSpacing: 16.0,
                childAspectRatio: 0.75,
              ),
              itemCount: _searchResults.length,
              itemBuilder: (context, index) {
                final product = _searchResults[index];
                return GestureDetector(
                  onTap: () {
                    Navigator.pushNamed(
                      context,
                      Product.routeName,
                      arguments: product.productId,
                    );
                  },
                  child: Card(
                    elevation: 2,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(15),
                    ),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Expanded(
                          flex: 3,
                          child: ClipRRect(
                            borderRadius: BorderRadius.vertical(
                              top: Radius.circular(15),
                            ),
                            child: Stack(
                              fit: StackFit.expand,
                              children: [
                                Image.network(
                                  product.mainImageUrl,
                                  fit: BoxFit.cover,
                                  errorBuilder: (context, error, stackTrace) {
                                    return Image.network(
                                      'https://via.placeholder.com/150',
                                      fit: BoxFit.cover,
                                    );
                                  },
                                ),
                                Positioned(
                                  right: 8,
                                  top: 8,
                                  child: CircleAvatar(
                                    backgroundColor: Colors.white,
                                    radius: 16,
                                    child: Icon(
                                      Icons.favorite_border,
                                      size: 20,
                                      color: Colors.red,
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                        Expanded(
                          flex: 2,
                          child: Padding(
                            padding: const EdgeInsets.all(12.0),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Text(
                                  product.productName,
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    fontSize: 14,
                                  ),
                                  maxLines: 2,
                                  overflow: TextOverflow.ellipsis,
                                ),
                                Text(
                                  formatCurrency(product.productPrice),
                                  style: TextStyle(
                                    color: Colors.blue[700],
                                    fontWeight: FontWeight.bold,
                                    fontSize: 16,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),
          ),
          if (_isLoadingMore)
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Center(child: CircularProgressIndicator()),
            ),
        ],
      ),
    );
  }

  String formatCurrency(double price) {
    final formatter = NumberFormat.simpleCurrency(locale: 'vi_VN');
    return formatter.format(price);
  }
}