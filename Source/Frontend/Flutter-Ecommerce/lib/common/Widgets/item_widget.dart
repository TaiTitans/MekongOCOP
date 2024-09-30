import 'package:flutter/material.dart';

class ItemWidget extends StatefulWidget {
  final int index;
  final bool favoriteIcon;
  final String productName;
  final String productPrice;
  final String productImageUrl;
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
    required this.onFavorite,
  }) : super(key: key);

  @override
  _ItemWidgetState createState() => _ItemWidgetState();
}

class _ItemWidgetState extends State<ItemWidget> {
  late bool isFavorite;

  @override
  void initState() {
    super.initState();
    isFavorite = widget.favoriteIcon; // Khởi tạo trạng thái yêu thích
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
            right: 8.0,
            top: 8.0,
            child: IconButton(
              icon: Icon(
                isFavorite ? Icons.star_border : Icons.star,
                color: isFavorite ? Colors.yellow : Colors.yellow,
              ),
              onPressed: toggleFavorite,
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
                  Text(
                    widget.productName,
                    style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                  ),
                  Text(widget.productPrice),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
