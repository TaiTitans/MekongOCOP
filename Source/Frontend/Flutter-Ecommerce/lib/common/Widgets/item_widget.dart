import 'package:flutter/material.dart';

import 'package:flutter/material.dart';

class ItemWidget extends StatefulWidget {
  final int index;
  final bool favoriteIcon;
  final String productName;
  final String productPrice;
  final String productImageUrl;
  final String provinceName;
  final int productId;
  final Function(int) onFavorite;

  const ItemWidget({
    Key? key,
    required this.index,
    required this.favoriteIcon,
    required this.productName,
    required this.productPrice,
    required this.productImageUrl,
    required this.productId,
    required this.provinceName,
    required this.onFavorite,
  }) : super(key: key);

  @override
  _ItemWidgetState createState() => _ItemWidgetState();
}

class _ItemWidgetState extends State<ItemWidget> with SingleTickerProviderStateMixin {
  late bool isFavorite;
  late AnimationController _controller;

  @override
  void initState() {
    super.initState();
    isFavorite = widget.favoriteIcon; // Khởi tạo trạng thái yêu thích
    _controller = AnimationController(
      duration: const Duration(seconds: 2),
      vsync: this,
    )..repeat(reverse: true); // Tạo hiệu ứng động gradient
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void toggleFavorite() {
    setState(() {
      isFavorite = !isFavorite; // Đảo ngược trạng thái yêu thích
    });
    widget.onFavorite(widget.productId); // Gọi hàm onFavorite khi thay đổi trạng thái
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 4,
      child: Stack(
        children: [
          // Ảnh sản phẩm
          ClipRRect(
            borderRadius: BorderRadius.only(
              topLeft: Radius.circular(8.0),
              topRight: Radius.circular(8.0),
            ),
            child: Image.network(
              widget.productImageUrl,
              fit: BoxFit.cover,
              height: 150.0,
              width: double.infinity,
            ),
          ),
          // Nút yêu thích ở góc trên bên phải
          Positioned(
            right: 6.0,
            top: 2.0,
            child: IconButton(
              icon: Icon(
                isFavorite ? Icons.star_border : Icons.star,
                color: isFavorite ? Colors.yellow : Colors.yellow,
              ),
              onPressed: toggleFavorite,
            ),
          ),
          // Text "Sale" với hiệu ứng gradient và lửa
          Positioned(
            left: 8.0,
            top: 8.0,
            child: AnimatedBuilder(
              animation: _controller,
              builder: (context, child) {
                return ShaderMask(
                  shaderCallback: (bounds) {
                    return LinearGradient(
                      colors: [Colors.red, Colors.orange, Colors.yellow],
                      stops: [0.0, 0.5, 1.0],
                      transform: GradientRotation(_controller.value * 2 * 3.14),
                    ).createShader(bounds);
                  },
                  child: Text(
                    'Sale',
                    style: TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                      color: Colors.white, // Màu cơ bản để ShaderMask hoạt động
                      shadows: [
                        Shadow(
                          blurRadius: 18.0,
                          color: Colors.redAccent.withOpacity(0.6),
                          offset: Offset(0, 0), // Hiệu ứng lửa
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),
          ),
          // Thông tin sản phẩm
          Positioned(
            top: 150.0,
            left: 0,
            right: 0,
            child: Padding(
              padding: const EdgeInsets.all(8.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Tên sản phẩm
                  Text(
                    widget.productName,
                    style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                    overflow: TextOverflow.ellipsis,  // Cắt bớt khi quá dài
                    maxLines: 2,  // Giới hạn tối đa số dòng hiển thị
                  ),
                  SizedBox(height: 4),
                  // Tỉnh thành
                  Text(
                    widget.provinceName,
                    style: TextStyle(fontSize: 14, color: Colors.grey),
                  ),
                  // Giá sản phẩm
                  Text(widget.productPrice,
                  style: TextStyle(color: Colors.lightBlue),),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
