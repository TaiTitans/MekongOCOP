class ReviewModel {
  final int reviewId;
  final int rating;
  final String reviewContent;
  final int createdAt;
  final int userId;
  final String userName;
  final int productId;

  ReviewModel({
    required this.reviewId,
    required this.rating,
    required this.reviewContent,
    required this.createdAt,
    required this.userId,
    required this.userName,
    required this.productId,
  });

  String get reviewerName {
    return userName;
  }

  String get comment {
    return reviewContent;
  }

  DateTime get date {
    return DateTime.fromMillisecondsSinceEpoch(createdAt);
  }

  factory ReviewModel.fromJson(Map<String, dynamic> json) {
    return ReviewModel(
      reviewId: json['reviewId'],
      rating: json['rating'] as int,
      reviewContent: json['reviewContent'],
      createdAt: json['createdAt'],
      userId: json['user_id'],
      userName: json['userName'],
      productId: json['productId'],
    );
  }


}

