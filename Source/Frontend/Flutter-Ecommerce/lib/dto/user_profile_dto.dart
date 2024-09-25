class UserProfileDTO {
  final String fullName;
  final String profileId;
  final String sex;
  final String bio;
  final String birthday;
  final String userId;

  UserProfileDTO({
    required this.fullName,
    required this.profileId,
    required this.sex,
    required this.bio,
    required this.birthday,
    required this.userId,
  });

  // Hàm ánh xạ từ JSON
  factory UserProfileDTO.fromJson(Map<String, dynamic> json) {
    return UserProfileDTO(
      fullName: json['full_name'] ?? '', // Ánh xạ từ 'full_name' thành 'fullName'
      profileId: json['profileId'] ?? '',
      sex: json['sex'] ?? '',
      bio: json['bio'] ?? '',
      birthday: json['birthday'] ?? '',
      userId: json['user_id'] ?? '',
    );
  }

  // Hàm chuyển đối tượng thành JSON
  Map<String, dynamic> toJson() {
    return {
      'full_name': fullName, // Ánh xạ từ 'fullName' thành 'full_name'
      'profileId': profileId,
      'sex': sex,
      'bio': bio,
      'birthday': birthday,
      'user_id': userId,
    };
  }
}
