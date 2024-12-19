// Province.dart
class Province {
  final int id;
  final String name;
  final double minLatitude;
  final double maxLatitude;
  final double minLongitude;
  final double maxLongitude;

  Province(this.id, this.name, this.minLatitude, this.maxLatitude, this.minLongitude, this.maxLongitude);
}

class ProvinceUtils {
  static int getProvince(double latitude, double longitude) {
    final provinces = [
      Province(1, 'An Giang', 10.5, 11.5, 104.8, 105.3),
      Province(2, 'Bạc Liêu', 9.0, 9.5, 105.0, 105.5),
      Province(3, 'Bến Tre', 9.7, 10.2, 105.9, 106.4),
      Province(4, 'Cà Mau', 8.8, 9.3, 104.7, 105.2),
      Province(5, 'Cần Thơ', 9.9, 10.4, 105.5, 106.0),
      Province(6, 'Đồng Tháp', 10.1, 10.6, 105.5, 106.0),
      Province(7, 'Hậu Giang', 9.5, 10.0, 105.3, 105.8),
      Province(8, 'Kiên Giang', 9.8, 10.3, 104.8, 105.3),
      Province(9, 'Long An', 10.3, 10.8, 105.8, 106.3),
      Province(10, 'Sóc Trăng', 9.3, 9.8, 105.3, 105.8),
      Province(11, 'Tiền Giang', 10.1, 10.6, 105.8, 106.3),
      Province(12, 'Trà Vinh', 9.5, 10.0, 106.0, 106.5),
      Province(13, 'Vĩnh Long', 10.0, 10.5, 105.5, 106.0),
    ];

    for (final province in provinces) {
      if (latitude >= province.minLatitude &&
          latitude <= province.maxLatitude &&
          longitude >= province.minLongitude &&
          longitude <= province.maxLongitude) {
        return province.id;
      }
    }

    return -1; // Không xác định được
  }
}
