class StoreData {
  final int storeId;
  final String storeName;
  final String storeAddress;
  final String storeLogo;

  StoreData({
    required this.storeId,
    required this.storeName,
    required this.storeAddress,
    required this.storeLogo,
  });

  factory StoreData.fromJson(Map<String, dynamic> json) {
    return StoreData(
      storeId: json['store_id'],
      storeName: json['store_name'],
      storeAddress: json['store_address'],
      storeLogo: json['store_logo'],
    );
  }
}
