import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';


class CartTile extends StatefulWidget {
  final String productName; // Product name
  final int quantity; // Quantity of the product
  final double price; // Price of the product
  final VoidCallback onRemove; // Callback for removing the product
  final ValueChanged<int> onUpdateQuantity; // Callback for updating quantity

  const CartTile({
    Key? key,
    required this.productName,
    required this.quantity,
    required this.price,
    required this.onRemove,
    required this.onUpdateQuantity,
  }) : super(key: key);

  @override
  _CartTileState createState() => _CartTileState();
}

class _CartTileState extends State<CartTile> {
  late int _quantity;

  @override
  void initState() {
    super.initState();
    _quantity = widget.quantity;
  }

  void _increaseQuantity() {
    setState(() {
      _quantity++;
    });
    widget.onUpdateQuantity(_quantity);
  }

  void _decreaseQuantity() {
    if (_quantity > 1) {
      setState(() {
        _quantity--;
      });
      widget.onUpdateQuantity(_quantity);
    } else {
      widget.onRemove();
    }
  }
  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16.0),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8.0),
        boxShadow: [
          BoxShadow(
            color: Colors.grey.withOpacity(0.5),
            spreadRadius: 2,
            blurRadius: 5,
            offset: const Offset(0, 3),
          ),
        ],
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  widget.productName,
                  style: const TextStyle(
                    fontSize: 16.0,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 8.0),
                Text(
                  formatCurrency(widget.price),
                  style: const TextStyle(
                    fontSize: 14.0,
                    color: Colors.grey,
                  ),
                ),
              ],
            ),
          ),
          Row(
            children: [
              IconButton(
                icon: const Icon(Icons.remove_circle_outline, color: AppColors.lightGray),
                onPressed: _decreaseQuantity,
              ),
              Text(
                '$_quantity',
                style: const TextStyle(
                  fontSize: 16.0,
                  fontWeight: FontWeight.bold,
                  color: AppColors.primaryLight,
                ),
              ),
              IconButton(
                icon: const Icon(Icons.add_circle_outline, color: AppColors.lightGray),
                onPressed: _increaseQuantity,
              ),
            ],
          ),
        ],
      ),
    );
  }
}

String formatCurrency(double price) {
  final formatter = NumberFormat.simpleCurrency(locale: 'vi_VN');
  return formatter.format(price);
}