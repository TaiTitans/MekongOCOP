class ProductImage {
  final int imageId;
  final String imageUrl;
  final bool isPrimary;
  final bool primary;

  ProductImage({
    required this.imageId,
    required this.imageUrl,
    required this.isPrimary,
    required this.primary,
  });

  factory ProductImage.fromJson(Map<String, dynamic> json) {
    return ProductImage(
      imageId: json['imageId'],
      imageUrl: json['imageUrl'],
      isPrimary: json['isPrimary'],
      primary: json['primary'],
    );
  }

}