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
  bool _isLoadingMore = false; // Trạng thái tải thêm
  int _page = 0; // Trang hiện tại
  final int _size = 10; // Số lượng sản phẩm trên mỗi trang
  bool _hasMore = true; // Xác định xem còn dữ liệu để tải không

  final ScrollController _scrollController = ScrollController();

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

  // Kiểm tra nếu cuộn đến cuối danh sách
  void _scrollListener() {
    if (_scrollController.position.pixels == _scrollController.position.maxScrollExtent && !_isLoadingMore && _hasMore) {
      _loadMore();
    }
  }

  Future<void> _performSearch(String query) async {
    setState(() {
      _isLoading = true;
      _searchResults.clear(); // Xóa kết quả cũ khi bắt đầu tìm kiếm mới
      _page = 0;
      _hasMore = true;
    });

    try {
      final results = await _productService.fetchProductBySearchProductName(query, page: _page, size: _size);
      setState(() {
        _searchResults.addAll(results);
        _isLoading = false;
        if (results.length < _size) {
          _hasMore = false; // Nếu kết quả trả về ít hơn size, không còn trang nào để tải
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
      _page++; // Tăng số trang
      final results = await _productService.fetchProductBySearchProductName(_searchController.text, page: _page, size: _size);
      setState(() {
        _searchResults.addAll(results);
        _isLoadingMore = false;
        if (results.length < _size) {
          _hasMore = false; // Nếu không còn dữ liệu
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
        title: TextField(
          controller: _searchController,
          autofocus: true, // Tự động focus khi vào màn hình
          decoration: InputDecoration(
            hintText: 'Tìm kiếm sản phẩm...',
            border: InputBorder.none,
          ),
          onSubmitted: (query) {
            if (query.isNotEmpty) {
              _performSearch(query);
            }
          },
        ),
      ),
      body: _isLoading
          ? Center(child: CircularProgressIndicator())
          : _searchResults.isEmpty
          ? Center(child: Text('Không tìm thấy sản phẩm nào.'))
          : Column(
        children: [
          Expanded(
            child: GridView.builder(
              controller: _scrollController, // Gán controller cho GridView
              padding: const EdgeInsets.all(8.0),
              gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 2, // Hiển thị 2 sản phẩm trên mỗi hàng
                crossAxisSpacing: 10.0,
                mainAxisSpacing: 10.0,
                childAspectRatio: 0.7, // Điều chỉnh tỉ lệ giữa chiều rộng và chiều cao
              ),
              itemCount: _searchResults.length,
              itemBuilder: (context, index) {
                final product = _searchResults[index];
                return Card(
                  elevation: 4, // Hiệu ứng bóng đổ cho card
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(10), // Bo góc card
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Expanded(
                        child: ClipRRect(
                          borderRadius: BorderRadius.vertical(
                            top: Radius.circular(10),
                          ),
                          child: Image.network(
                            product.mainImageUrl, // Sử dụng mainImageUrl để hiển thị hình ảnh chính của sản phẩm
                            fit: BoxFit.cover,
                            width: double.infinity,
                            errorBuilder: (context, error, stackTrace) {
                              return Image.network(
                                'https://via.placeholder.com/150', // Hình ảnh thay thế nếu URL ảnh bị lỗi
                                fit: BoxFit.cover,
                                width: double.infinity,
                              );
                            },
                          ),
                        ),
                      ),
                      Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              product.productName,
                              style: TextStyle(
                                fontWeight: FontWeight.bold,
                                fontSize: 16,
                              ),
                              maxLines: 2,
                              overflow: TextOverflow.ellipsis,
                            ),
                            SizedBox(height: 4),
                            Text(
                              'Giá: ${formatCurrency(product.productPrice)}',
                              style: TextStyle(
                                color: Colors.lightBlueAccent,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            SizedBox(height: 4),
                            ElevatedButton(
                              onPressed: () {
                                // Điều hướng tới Product và truyền productId
                                Navigator.pushNamed(
                                  context,
                                  Product.routeName,
                                  arguments: product.productId,
                                );
                              },
                              child: Text('Chi tiết'),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                );
              },
            ),
          ),
          if (_isLoadingMore)
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Center(child: CircularProgressIndicator()), // Hiển thị biểu tượng tải khi tải thêm
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
