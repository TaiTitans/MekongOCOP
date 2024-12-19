import 'dart:async';
import 'dart:convert';
import 'dart:io';



import 'package:flutter/material.dart';
import 'package:flutter_carousel_slider/carousel_slider.dart';

import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:geolocator/geolocator.dart';
import 'package:image_picker/image_picker.dart';
import 'package:intl/intl.dart';
import 'package:location/location.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:smart_shop/Common/Widgets/app_title.dart';
import 'package:smart_shop/Common/Widgets/catalogue_widget.dart';
import 'package:smart_shop/Common/Widgets/custom_app_bar.dart';
import 'package:smart_shop/Common/Widgets/item_widget.dart';
import 'package:smart_shop/Common/Widgets/shimmer_effect.dart';
import 'package:smart_shop/Screens/Catalogue/catalogue.dart';
import 'package:smart_shop/Screens/Favorite/favorite.dart';
import 'package:smart_shop/Screens/Notifications/notifications.dart';
import 'package:smart_shop/Screens/Onboarding/onboarding.dart';
import 'package:smart_shop/Screens/Product/product.dart';
import 'package:smart_shop/Screens/Settings/settings.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';
import 'package:smart_shop/dummy/dummy_data.dart';
import 'package:smart_shop/service/seller_service.dart';
import 'package:url_launcher/url_launcher.dart';
import '../../common/Widgets/flash_sale.dart';
import '../../model/product.dart';
import '../../model/province.dart';
import '../../service/product_service.dart';
import '../search/search_screen.dart';


class Home extends StatefulWidget {
  const Home({Key? key}) : super(key: key);
  static const String routeName = 'home';

  @override
  State<Home> createState() => _HomeState();
}
// Test 1
class _HomeState extends State<Home> {
  final GlobalKey<ScaffoldState> _key = GlobalKey();
  List<dynamic>? _products;
  List<ProductModel> _searchResults = [];
  bool _isLoading = false;
  DateTime? _lastProductsUpdateTime;
  final productService = ProductService();
  final TextEditingController _searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _loadProducts();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _loadProducts() async {
    await _fetchProducts(context);
  }
  Future<void> _fetchProducts(BuildContext context) async {
    // Fetch latest products từ API
    final sharedPreferences = await SharedPreferences.getInstance();
    final accessToken = sharedPreferences.getString('accessToken') ?? '';
    _products = await productService.fetchProductsNewFeed(accessToken);
    _lastProductsUpdateTime = DateTime.now();

    setState(() {});
  }

  Future<void> _submitSellerLicense(File? licenseFile) async {
    final sellerService = SellerService();
    if (licenseFile != null) {
      final bool isSubmitted = await sellerService.submitSeller(imageFile: licenseFile);
      if (isSubmitted) {
        // Tải lên thành công
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Tải lên ảnh giấy phép thành công!')),
        );
      } else {
        // Tải lên thất bại
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Tải lên ảnh giấy phép thất bại.')),
        );
      }
    }
  }




  Future<void> _logout(BuildContext context) async {
    // Xóa access token và refresh token từ SharedPreferences
    final sharedPreferences = await SharedPreferences.getInstance();
    await sharedPreferences.remove('accessToken');
    await sharedPreferences.remove('refreshToken'); // Nếu bạn lưu refresh token

    // Điều hướng đến màn hình đăng nhập
    Navigator.pushReplacementNamed(context, OnBoarding.routeName);
  }


  Future<void> _handleFavorite(BuildContext context, int productId) async {
    // Lấy accessToken từ SharedPreferences
    final sharedPreferences = await SharedPreferences.getInstance();
    final accessToken = sharedPreferences.getString('accessToken') ?? '';
    bool isSuccess = await productService.favoriteProduct(productId, accessToken);

    if (isSuccess) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar( content: Text(
          'Thao tác thành công',
          style: TextStyle(color: Colors.black), // Đổi màu chữ
        ),
          backgroundColor: Colors.white, // Đổi màu nền của SnackBar
        ),
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Yêu thích sản phẩm thất bại!')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      key: _key,
      appBar: _buildCustomAppBar(context),  // AppBar có thanh tìm kiếm
      drawer: _buildDrawer(context, _showUploadLicenseDialog), // Drawer menu
      body: _buildBody(context), // Nội dung chính của trang
      resizeToAvoidBottomInset: false,
    );
  }


  Widget _buildBody(BuildContext context) {
    if (_isLoading) {
      // Hiển thị loading khi đang tải dữ liệu
      return const Center(child: CircularProgressIndicator());
    } else if (_searchResults.isNotEmpty) {
      // Khi có kết quả tìm kiếm, hiển thị danh sách sản phẩm tìm thấy
      return ListView.builder(
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        itemCount: _searchResults.length,
        itemBuilder: (context, index) {
          final product = _searchResults[index];
          return ListTile(
            title: Text(product.productName),
            subtitle: Text('Giá: ${product.productPrice} VND'),
            onTap: () {
              // Xử lý khi nhấn vào sản phẩm, ví dụ điều hướng đến chi tiết sản phẩm
            },
          );
        },
      );
    } else if (_searchResults.isEmpty && _searchController.text.isNotEmpty) {
      // Nếu không tìm thấy kết quả tìm kiếm
      return const Center(
        child: Text('Không tìm thấy sản phẩm nào khớp với từ khóa.'),
      );
    } else {
      // Nếu chưa tìm kiếm hoặc chưa có kết quả, hiển thị nội dung gốc
      return SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildSellerCard(),
            FlashSaleWidget(),
            _buildCatalogue(),
            _buildProductSuggestions(context),
            _buildFeatured(context),
          ],
        ),
      );
    }
  }


  Widget _buildDrawer(BuildContext context, Future<void> Function(BuildContext) showUploadLicenseDialog) {
    return SizedBox(
      width: MediaQuery.of(context).size.width * .60,
      child: Drawer(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            SizedBox(
              width: double.infinity,
              height: MediaQuery.of(context).size.height * .20,
              child: DrawerHeader(
                padding: EdgeInsets.zero,
                margin: EdgeInsets.zero,
                child: Align(
                  alignment: Alignment.centerLeft,
                  child: Container(
                    margin: const EdgeInsets.only(left: 20.0),
                    child: AppTitle(
                      fontStyle: FontStyles.montserratExtraBold18(),
                      marginTop: 0.0,
                    ),
                  ),
                ),
                decoration: const BoxDecoration(
                  gradient: LinearGradient(
                    colors: [AppColors.primaryDark, AppColors.primaryLight],
                    begin: Alignment.bottomLeft,
                    end: Alignment.topRight,
                    stops: [0, 1],
                  ),
                ),
              ),
            ),
            SizedBox(
              width: MediaQuery.of(context).size.width / 2,
              height: MediaQuery.of(context).size.height / 2.7,
              child: Column(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  ListTile(
                    onTap: () {
                      Navigator.pop(context);
                      Navigator.pushNamed(context, Favorite.routeName);
                    },
                    leading: const Icon(Icons.star,
                        color: AppColors.primaryLight),
                    title: Text(
                      'Yêu thích',
                      style: FontStyles.montserratRegular18(),
                    ),
                  ),
                  ListTile(
                    onTap: () {
                      Navigator.pop(context);
                      showUploadLicenseDialog(context);
                    },
                    leading: const Icon(Icons.supervised_user_circle,
                        color: AppColors.primaryLight),
                    title: Text(
                      'Trở thành người bán',
                      style: FontStyles.montserratRegular18(),
                    ),
                  ),
                  ListTile(
                    onTap: () async {
                      Navigator.pop(context);
                      String? apiUrl = "http://192.168.1.172:8081"; // Sử dụng biến API_URL từ file .env
                      if (apiUrl != null && await canLaunch(apiUrl)) {
                        await launch(apiUrl);
                      } else {
                        throw 'Không thể mở URL: $apiUrl';
                      }
                    },
                    leading: const Icon(Icons.manage_accounts, color: AppColors.primaryLight),
                    title: Text(
                      'Quản lý bán hàng',
                      style: FontStyles.montserratRegular18(),
                    ),
                  ),
                  ListTile(
                    onTap: () {
                      Navigator.pop(context);
                      Navigator.pushNamed(context, Settings.routeName);
                    },
                    leading: const Icon(Icons.settings,
                        color: AppColors.primaryLight),
                    title: Text(
                      'Cài đặt',
                      style: FontStyles.montserratRegular18(),
                    ),
                  ),
                  ListTile(
                    onTap: () async {
                      await _logout(context);
                    },
                    leading: const Icon(Icons.logout_outlined,
                        color: AppColors.primaryLight),
                    title: Text(
                      'Đăng xuất',
                      style: FontStyles.montserratRegular18(),
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
  PreferredSize _buildCustomAppBar(BuildContext context) {
    return PreferredSize(
      preferredSize: Size(double.infinity, MediaQuery.of(context).size.height * .20),
      child: CustomAppBar(
        isHome: true,
        enableSearchField: true,
        leadingIcon: Icons.menu,
        leadingOnTap: () {
          _key.currentState?.openDrawer();
        },
        trailingOnTap: () {
          Navigator.of(context).pushNamed(NotificationScreen.routeName);
        },
        trailingIcon: Icons.notifications_active_outlined,
        scaffoldKey: _key,
        onSearchSubmitted: (String searchTerm) {
          Navigator.pushNamed(context, SearchScreen.routeName);
        },
      ),
    );
  }



  Widget _buildSellerCard() {
    var screenHeight = MediaQuery.of(context).size.height;
    return Container(
      margin: EdgeInsets.only(left: 20.0.w, right: 20.w, top: 15.0.h),
      height: 88.h,
      width: 343.w,
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10.0.r),
      ),
      child: Stack(
        children: [
          ClipRRect(
            borderRadius: BorderRadius.circular(10.0.r),
            child: makeSlider(),
          ),
          Positioned(
              top: screenHeight * .020.h,
              left: 20.0,
              child: Text(
                'Sản phẩm giảm giá',
                style: FontStyles.montserratBold25()
                    .copyWith(color: AppColors.white),
              )),
          Positioned(
            top: screenHeight * .070.h,
            left: 20.0.w,
            child: Row(
              children: [
                Text(
                  'Xem thêm',
                  style: FontStyles.montserratBold12().copyWith(
                    color: AppColors.secondary,
                  ),
                ),
                Icon(
                  Icons.arrow_forward_ios_rounded,
                  size: 12.0.h,
                  color: AppColors.secondary,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildCatalogue() {
    return GestureDetector(
      onTap: () {
        Navigator.pushNamed(context, Catalogue.routeName, arguments: [true, true]);
      },
      child: Container(
        margin: EdgeInsets.only(top: 10.0.h, left: 20.h, right: 20.0.h, bottom: 17.h),
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  'Danh mục sản phẩm',
                  style: FontStyles.montserratBold19().copyWith(
                    color: const Color(0xFF34283E),
                  ),
                ),
                GestureDetector(
                  onTap: () {
                    Navigator.pushNamed(context, Catalogue.routeName, arguments: [true, true]);
                  },
                  child: Text(
                    'Xem thêm ',
                    style: FontStyles.montserratBold12().copyWith(color: const Color(0xFF9B9B9B)),
                  ),
                ),
              ],
            ),
            SizedBox(
              width: MediaQuery.of(context).size.width,
              height: 97.h,
              child: ListView.builder(
                scrollDirection: Axis.horizontal,
                itemCount: DummyData.catalogueImagesLink.length,
                shrinkWrap: true,
                itemBuilder: (context, index) {
                  return CatalogueWidget(
                    height: 88.h,
                    width: 88.w,
                    index: index,
                    imagePath: DummyData.catalogueImagesLink[index], // Truyền đường dẫn ảnh từ assets
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }


  Widget _buildFeatured(BuildContext context) {
    var screenHeight = MediaQuery.of(context).size.height;

    return Container(
      margin: EdgeInsets.only(
        left: 20.0.w,
        right: 20.0.w,
        top: 20.h,
        bottom: screenHeight * .09.h,
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Sản phẩm mới',
            style: FontStyles.montserratBold19().copyWith(color: const Color(0xFF34283E)),
          ),
          SizedBox(height: 10.0.h),
          _products == null // Kiểm tra xem đã có sản phẩm chưa
              ? const Center(child: CircularProgressIndicator())
              : (_products!.isEmpty
              ? const Center(child: Text('No products found.'))
              : SizedBox(
            child: GridView.builder(
              shrinkWrap: true,
              itemCount: _products!.length,
              physics: const NeverScrollableScrollPhysics(),
              gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 2,
                mainAxisExtent: 210.0.h,
                crossAxisSpacing: 10.0.w,
                mainAxisSpacing: 8.0.h,
              ),
              itemBuilder: (_, index) {
                final product = _products![index];
                String productImageUrl = product['productImages'].isNotEmpty
                    ? product['productImages'][0]['imageUrl']
                    : '';

                return GestureDetector(
                  onTap: () {
                    Navigator.pushNamed(context, Product.routeName, arguments: product['productId']);
                  },
                  child: ItemWidget(
                    index: index,
                    favoriteIcon: true,
                    productName: product['productName'],
                    provinceName: product['provinceName'],
                    productPrice: formatCurrency(product['productPrice']),
                    productImageUrl: productImageUrl,
                    productId: product['productId'],
                    onFavorite: (int productId) {
                      _handleFavorite(context, productId);
                    },
                  ),
                );
              },
            ),
          )),
        ],
      ),
    );
  }

  Future<void> _showUploadLicenseDialog(BuildContext context) async {
    File? _licenseFile;

    await showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Tải lên ảnh giấy phép'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextButton(
              onPressed: () async {
                final pickedFile = await ImagePicker().pickImage(source: ImageSource.gallery);
                if (pickedFile != null) {
                  _licenseFile = File(pickedFile.path);
                  Navigator.of(context).pop();
                  await _submitSellerLicense(_licenseFile);
                }
              },
              child: const Text('Chọn ảnh từ thư viện'),
            ),
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              child: const Text('Hủy'),
            ),
          ],
        ),
      ),
    );
  }
  // Sản phẩm gợi ý
  Widget _buildProductSuggestions(BuildContext context) {
    var screenHeight = MediaQuery.of(context).size.height;
    int? _currentProvinceId;
    List<dynamic>? _products;

    return FutureBuilder<List<dynamic>>(
      future: _getCurrentProvinceId().then((provinceId) {
        _currentProvinceId = provinceId;
        return _getCachedProductSuggestions(_currentProvinceId!);
      }),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        } else if (snapshot.hasError) {
          return Center(child: Text('Lỗi khi tải sản phẩm: ${snapshot.error}'));
        } else if (snapshot.hasData && snapshot.data!.isNotEmpty) {
          _products = snapshot.data!.take(4).toList();
          _lastProductsUpdateTime = DateTime.now();
          return Container(
            margin: EdgeInsets.only(
              left: 20.0.w,
              right: 20.0.w,
              top: 20.h,
              bottom: screenHeight * .01.h,
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      'Sản phẩm gợi ý',
                      style: FontStyles.montserratBold19()
                          .copyWith(color: const Color(0xFF34283E)),
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          '${_getCurrentProvinceName(_currentProvinceId ?? -1)}',
                          style: FontStyles.montserratRegular14()
                              .copyWith(color: Colors.lightBlue),
                        ),
                        SizedBox(width: 4.0.w),
                        Icon(
                          Icons.location_on,
                          color: Colors.green,
                          size: 16.0.h,
                        ),
                      ],
                    ),
                  ],
                ),
                SizedBox(height: 10.0.h),
                SizedBox(
                  child: GridView.builder(
                    shrinkWrap: true,
                    itemCount: _products!.length,
                    physics: const NeverScrollableScrollPhysics(),
                    gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 2,
                      mainAxisExtent: 210.0.h,
                      crossAxisSpacing: 10.0.w,
                      mainAxisSpacing: 8.0.h,
                    ),
                    itemBuilder: (_, index) {
                      final product = _products![index];
                      String productImageUrl = product['productImages'].isNotEmpty
                          ? product['productImages'][0]['imageUrl']
                          : '';

                      return GestureDetector(
                        onTap: () {
                          Navigator.pushNamed(context, Product.routeName, arguments: product['productId']);
                        },
                        child: ItemWidget(
                          index: index,
                          favoriteIcon: true,
                          productName: product['productName'],
                          provinceName: product['provinceName'],
                          productPrice: formatCurrency(product['productPrice']),
                          productImageUrl: productImageUrl,
                          productId: product['productId'],
                          onFavorite: (int productId) {
                            _handleFavorite(context, productId);
                          },
                        ),
                      );
                    },
                  ),
                ),
              ],
            ),
          );
        } else {
          return const Center(child: Text('Không có sản phẩm gợi ý'));
        }
      },
    );
  }
  Future<List<dynamic>> _getCachedProductSuggestions(int provinceId) async {
    final sharedPreferences = await SharedPreferences.getInstance();
    final cachedProductSuggestions = sharedPreferences.getString('cachedProductSuggestions$provinceId');
    final lastUpdateTime = sharedPreferences.getString('lastProductsUpdateTime$provinceId');

    if (cachedProductSuggestions != null && lastUpdateTime != null) {
      DateTime lastUpdate = DateTime.parse(lastUpdateTime);
      if (DateTime.now().difference(lastUpdate).inMinutes < 30) {
        return jsonDecode(cachedProductSuggestions); // Trả về dữ liệu cache
      }
    }

    // Nếu cache hết hạn, gọi API để lấy dữ liệu mới
    final products = await _fetchProductsByProvince(provinceId);
    await sharedPreferences.setString('cachedProductSuggestions$provinceId', jsonEncode(products));
    await sharedPreferences.setString('lastProductsUpdateTime$provinceId', DateTime.now().toIso8601String());

    return products;
  }

  String _getCurrentProvinceName(int provinceId) {
    switch (provinceId) {
      case 1:
        return 'An Giang';
      case 2:
        return 'Bạc Liêu';
      case 3:
        return 'Bến Tre';
      case 4:
        return 'Cà Mau';
      case 5:
        return 'Cần Thơ';
      case 6:
        return 'Đồng Tháp';
      case 7:
        return 'Hậu Giang';
      case 8:
        return 'Kiên Giang';
      case 9:
        return 'Long An';
      case 10:
        return 'Sóc Trăng';
      case 11:
        return 'Tiền Giang';
      case 12:
        return 'Trà Vinh';
      case 13:
        return 'Vĩnh Long';
      default:
        return 'Không xác định';
    }
  }
  Future<int> _getCurrentProvinceId() async {
    final sharedPreferences = await SharedPreferences.getInstance();
    final cachedLatitude = sharedPreferences.getDouble('lastLatitude');
    final cachedLongitude = sharedPreferences.getDouble('lastLongitude');
    final lastUpdateTime = sharedPreferences.getString('lastLocationUpdateTime');

    if (cachedLatitude != null && cachedLongitude != null && lastUpdateTime != null) {
      final lastUpdate = DateTime.parse(lastUpdateTime);
      if (DateTime.now().difference(lastUpdate).inMinutes < 30) {
        // Dùng dữ liệu cache nếu còn mới
        return ProvinceUtils.getProvince(cachedLatitude, cachedLongitude);
      }
    }

    final location = Location();

    try {
      bool serviceEnabled = await location.serviceEnabled();
      if (!serviceEnabled) {
        serviceEnabled = await location.requestService();
        if (!serviceEnabled) return -1;
      }

      PermissionStatus permissionGranted = await location.hasPermission();
      if (permissionGranted == PermissionStatus.denied) {
        permissionGranted = await location.requestPermission();
        if (permissionGranted != PermissionStatus.granted) return -1;
      }

      final locationData = await location.getLocation();
      await sharedPreferences.setDouble('lastLatitude', locationData.latitude!);
      await sharedPreferences.setDouble('lastLongitude', locationData.longitude!);
      await sharedPreferences.setString('lastLocationUpdateTime', DateTime.now().toIso8601String());

      return ProvinceUtils.getProvince(locationData.latitude!, locationData.longitude!);
    } catch (e) {
      print('Lỗi khi lấy vị trí: $e');
      return -1; // Lỗi khi lấy định vị
    }
  }




  Future<List<dynamic>> _fetchProductsByProvince(int provinceId) async {
    final sharedPreferences = await SharedPreferences.getInstance();
    final accessToken = sharedPreferences.getString('accessToken') ?? '';

    if (accessToken.isEmpty) {
      throw Exception('Access token not found in SharedPreferences');
    }

    // Kiểm tra nếu đã có sản phẩm và dữ liệu cache còn mới
    if (_products == null || DateTime.now().difference(_lastProductsUpdateTime!).inMinutes >= 30) {
      _products = await _fetchProductsByProvince(provinceId);
    }


    return await productService.fetchProductsByProvince(provinceId, accessToken);
  }




  String formatCurrency(double price) {
    final formatter = NumberFormat.simpleCurrency(locale: 'vi_VN');
    return formatter.format(price);
  }


  Widget makeSlider() {
    return CarouselSlider.builder(
      unlimitedMode: true,
      autoSliderDelay: const Duration(seconds: 5),
      enableAutoSlider: true,
      slideBuilder: (index) {
        return Image.asset(
          DummyData.sellerImagesLink[index],
          color: const Color.fromRGBO(42, 3, 75, 0.35),
          colorBlendMode: BlendMode.srcOver,
          fit: BoxFit.fill,
          errorBuilder: (context, error, stackTrace) {
            return ShimmerEffect(
              borderRadius: 10.0.r,
              height: 88.h,
              width: 343.w,
            );
          },
        );
      },
      slideTransform: const DefaultTransform(),
      slideIndicator: CircularSlideIndicator(
        currentIndicatorColor: AppColors.lightGray,
        alignment: Alignment.bottomCenter,
        padding: EdgeInsets.only(bottom: 10.h, left: 20.0.w),
      ),
      itemCount: DummyData.sellerImagesLink.length,
    );
  }

}