import 'package:smart_shop/model/product_image.dart';
import 'package:smart_shop/model/review.dart';

class ProductModel {
  final int productId;
  final String productName;
  final String productDescription;
  final double productPrice;
  final int productQuantity;
  final int categoryId;
  final String categoryName;
  final int provinceId;
  final String provinceName;
  final List<ProductImage> productImages;
  final List<ReviewModel> reviews;
  final int store;

  ProductModel({
    required this.productId,
    required this.productName,
    required this.productDescription,
    required this.productPrice,
    required this.productQuantity,
    required this.categoryId,
    required this.categoryName,
    required this.provinceId,
    required this.provinceName,
    required this.productImages,
    required this.reviews,
    required this.store,
  });

  String get mainImageUrl {
    var primaryImage = productImages.firstWhere((image) => image.isPrimary, orElse: () => productImages.first);
    return primaryImage.imageUrl;
  }

  List<ProductImage> get relatedProducts {
    return productImages.where((image) => !image.isPrimary).toList();
  }

  int get rating {
    if (reviews.isNotEmpty) {
      return reviews.first.rating;
    }
    return 0;
  }

  int get stock {
    return productQuantity;
  }

  String get title {
    return productName;
  }
  factory ProductModel.fromJson(Map<String, dynamic> json) {
    return ProductModel(
      productId: json['productId'],
      productName: json['productName'],
      productDescription: json['productDescription'],
      productPrice: json['productPrice'].toDouble(),
      productQuantity: json['productQuantity'],
      categoryId: json['categoryId'],
      categoryName: json['categoryName'],
      provinceId: json['provinceId'],
      provinceName: json['provinceName'],
      productImages: (json['productImages'] as List<dynamic>)
          .map((image) => ProductImage.fromJson(image))
          .toList(),
      reviews: (json['reviews'] as List<dynamic>)
          .map((review) => ReviewModel.fromJson(review))
          .toList(),
      store: json['store'],
    );
  }
}

