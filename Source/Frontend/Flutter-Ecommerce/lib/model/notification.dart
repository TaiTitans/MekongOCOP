class NotificationModel {
  final int id;
  final String message;
  final DateTime sentAt;
  final User? userId; // Make this nullable to handle the case where userId might not be present.

  NotificationModel({
    required this.id,
    required this.message,
    required this.sentAt,
    this.userId, // No longer required
  });

  factory NotificationModel.fromJson(Map<String, dynamic> json) {
    // Access the sent_at array and convert it to milliseconds since epoch
    final sentAtArray = json['sent_at'];
    final sentAt = DateTime(
      sentAtArray[0], // year
      sentAtArray[1], // month
      sentAtArray[2], // day
      sentAtArray[3], // hour
      sentAtArray[4], // minute
      sentAtArray[5], // second
      sentAtArray[6], // millisecond
    );

    // Safely parse the userId field if it's present, otherwise, use null
    User? user;
    if (json.containsKey('userId')) {
      user = User.fromJson(json['userId']);
    }

    return NotificationModel(
      id: json['id'],
      message: json['message'],
      sentAt: sentAt,
      userId: user, // Pass the user object or null if not present
    );
  }
}

class User {
  final int userId;
  final String username;

  User({required this.userId, required this.username});

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      userId: json['user_id'],
      username: json['username'],
    );
  }
}
