import 'package:flutter/material.dart';
import 'package:smart_shop/Screens/Cart/cart.dart';
import 'package:smart_shop/Screens/Catalogue/catalogue.dart';
import 'package:smart_shop/Screens/Favorite/favorite.dart';
import 'package:smart_shop/Screens/Home/home.dart';
import 'package:smart_shop/Screens/Profile/profile.dart';
import 'package:smart_shop/Utils/app_colors.dart';
import 'package:smart_shop/Utils/font_styles.dart';

import '../Catalogue/province_catalogue.dart';
import '../Chatbot/chatbot.dart';

class BottomNavigatorBar extends StatefulWidget {
  const BottomNavigatorBar({Key? key}) : super(key: key);
  static const String routeName = 'bottomNavigatorBar';

  @override
  _BottomNavigatorBarState createState() => _BottomNavigatorBarState();
}

class _BottomNavigatorBarState extends State<BottomNavigatorBar> {
  int currentIndex = 0;

  // Danh sách các màn hình
  List<Widget> myScreens = [
    const Home(),
    const Province(),
    ChatbotApp(),
    const Profile(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: myScreens.elementAt(currentIndex),
bottomNavigationBar: buildBottomSheet(),
      resizeToAvoidBottomInset: false,
    );
  }

  Widget buildBottomNavigation() {
    return BottomNavigationBar(
      currentIndex: currentIndex,
      items: const [
        BottomNavigationBarItem(
            icon: Image(
              image: AssetImage(
                'assets/bottomBarIcons/home 1.png',
              ),
            ),
            label: 'Home',
            tooltip: 'Home'),
        BottomNavigationBarItem(
            icon: Image(
              image: AssetImage(
                'assets/bottomBarIcons/view-grid 3.png',
              ),
            ),
            label: 'Catalogue',
            tooltip: 'Catalogue'),
        BottomNavigationBarItem(
            icon: Image(
              image: AssetImage(
                'assets/bottomBarIcons/heart 2.png',
              ),
            ),
            label: 'Favorite',
            tooltip: 'Favorite'),
        BottomNavigationBarItem(
            icon: Image(
              image: AssetImage(
                'assets/bottomBarIcons/user 2.png',
              ),
            ),
            label: 'Profile',
            tooltip: 'Profile'),
      ],
      selectedItemColor: AppColors.primaryDark,
      onTap: (value) {
        setState(() {
          currentIndex = value; // Cập nhật chỉ số khi người dùng nhấn
        });
      },
    );
  }


  Widget buildBottomSheet() {
    var screenWidth = MediaQuery.of(context).size.width;
    return Container(
      height: 50.0,
      margin: const EdgeInsets.symmetric(vertical: 10.0),
      child: Stack(
        clipBehavior: Clip.none,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Expanded(
                child: Container(
                  margin: const EdgeInsets.symmetric(horizontal: 10.0),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const SizedBox(
                        width: 1,
                      ),
                      GestureDetector(
                        onTap: () {
                          setState(() {
                            currentIndex = 0;
                          });
                        },
                        child: Column(
                          children: [
                            Icon(
                              Icons.home,
                              color: currentIndex == 0
                                  ? AppColors.primaryLight
                                  : Colors.grey,
                            ),
                            Text(
                              'Trang chủ',
                              style: TextStyle(
                                color: currentIndex == 0
                                    ? Colors.black
                                    : AppColors.textLightColor,
                              ),
                            ),
                          ],
                        ),
                      ),
                      GestureDetector(
                        onTap: () {
                          setState(() {
                            currentIndex = 1;
                          });
                        },
                        child: Column(
                          children: [
                            Icon(
                              Icons.category,
                              color: currentIndex == 1
                                  ? AppColors.primaryLight
                                  : Colors.grey,
                            ),
                            Text(
                              'Tỉnh thành',
                              style: TextStyle(
                                color: currentIndex == 1
                                    ? Colors.black
                                    : AppColors.textLightColor,
                              ),
                            ),
                          ],
                        ),
                      ),
                      GestureDetector(
                        onTap: () {
                          setState(() {
                            currentIndex = 2;
                          });
                        },
                        child: Column(
                          children: [
                            Icon(
                              Icons.chat_bubble_outline,
                              color: currentIndex == 2
                                  ? AppColors.primaryLight
                                  : Colors.grey,
                            ),
                            Text(
                              'Chatbot',
                              style: TextStyle(
                                color: currentIndex == 2
                                    ? Colors.black
                                    : AppColors.textLightColor,
                              ),
                            )
                          ],
                        ),
                      ),
                      GestureDetector(
                        onTap: () {
                          setState(() {
                            currentIndex = 3;
                          });
                        },
                        child: Column(
                          children: [
                            Icon(
                              Icons.person_outline,
                              color: currentIndex == 3
                                  ? AppColors.primaryLight
                                  : Colors.grey,
                            ),
                            Text(
                              'Cá nhân',
                              style: TextStyle(
                                color: currentIndex == 3
                                    ? Colors.black
                                    : AppColors.textLightColor,
                              ),
                            )
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              Container(
                width: screenWidth * .3,
              ),
            ],
          ),
          Positioned(
            right: 0.0,
            bottom: 15.0,
            child: GestureDetector(
              onTap: () {
                Navigator.pushNamed(context, Cart.routeName);
              },
              child: Container(
                width: 116.0,
                height: 56,
                decoration: const BoxDecoration(
                  gradient: LinearGradient(
                    colors:  [
                      AppColors.primaryLight,
                      AppColors.primaryDark
                    ],
                    end: Alignment.bottomLeft,
                    begin: Alignment.topRight,
                    stops:  [0, 1],
                  ),
                  color: AppColors.primaryDark,
                  borderRadius: BorderRadius.only(
                    topLeft: Radius.circular(30.0),
                    bottomLeft: Radius.circular(30.0),
                  ),
                ),
                child: ListTile(
                  contentPadding: const EdgeInsets.only(left: 10.0),
                  minLeadingWidth: 10.0,
                  leading: const Icon(
                    Icons.shopping_cart,
                    color: AppColors.white,
                    size: 21.0,
                  ),
                  title: RichText(
                    text: TextSpan(
                      text: 'Giỏ hàng',
                      style: FontStyles.montserratBold17()
                          .copyWith(fontSize: 11.0, color: AppColors.white),
                      // children: [
                      //   TextSpan(
                      //     text: 'OCOP',
                      //     style: FontStyles.montserratRegular14()
                      //         .copyWith(fontSize: 11.0, color: AppColors.white),
                      //   )
                      // ],
                    ),
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
