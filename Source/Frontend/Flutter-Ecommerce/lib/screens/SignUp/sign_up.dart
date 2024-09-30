import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';

import 'package:smart_shop/service/userprofile_service.dart';

import '../../Common/Widgets/app_button.dart';
import '../../Common/Widgets/app_text_field.dart';
import '../../Common/Widgets/gradient_header.dart';
import '../../Utils/app_colors.dart';
import '../../service/auth_service.dart';
import '../Home/home.dart';
import 'package:intl/intl.dart';


class SignUp extends StatefulWidget {
  static const String routeName = 'signup';
  const SignUp({Key? key}) : super(key: key);

  @override
  _SignUpState createState() => _SignUpState();
}

class _SignUpState extends State<SignUp> {
  final ImagePicker _picker = ImagePicker();
  File? _imageFile;

  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _birthDayController = TextEditingController();
  String _selectedSex = 'M'; // M=Nam, F=Nữ
  final TextEditingController _bioController = TextEditingController();

  final AuthService _authService = AuthService();
  late final ProfileService _profileService;// Service API
  @override
  void initState() {
    super.initState();
    _profileService = ProfileService(_authService.cookieJar);
  }
  Future<void> _chooseImage(ImageSource source) async {
    final XFile? pickedFile = await _picker.pickImage(source: source);
    if (pickedFile != null) {
      setState(() {
        _imageFile = File(pickedFile.path);
      });
    }
  }

  Future<void> _submit() async {
    try {
      // Kiểm tra và định dạng birthDay theo đúng định dạng "yyyy-MM-dd"
      String formattedBirthDay;

      try {
        formattedBirthDay = DateFormat('yyyy-MM-dd').format(DateTime.parse(_birthDayController.text));
      } catch (e) {
        _showErrorSnackbar('Ngày sinh không đúng định dạng. Vui lòng nhập theo định dạng yyyy-MM-dd.');
        return; // Thoát nếu ngày tháng sai định dạng
      }

      await _profileService.submitProfile(
        fullName: _nameController.text,
        birthDay: formattedBirthDay,
        sex: _selectedSex,
        bio: _bioController.text,
        imageFile: _imageFile,
      );
      _showSuccessSnackbar('Thêm thông tin thành công');
      Navigator.pushReplacementNamed(context, '/');
    } catch (e) {
      print(e);
      _showErrorSnackbar('Thêm thông tin thất bại');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _buildBody(context),
    );
  }

  Widget _buildBody(BuildContext context) {
    return SingleChildScrollView(
      child: Column(
        children: [
          _buildHeader(context),
          _buildForm(context),
        ],
      ),
    );
  }

  Widget _buildHeader(BuildContext context) {
    return AppHeaderGradient(
      fixedHeight: MediaQuery.of(context).size.height * .25,
      isProfile: false,
      text: 'Thông tin tài khoản',
    );
  }

  Widget _buildForm(BuildContext context) {
    return Form(
      child: Container(
        margin: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            GestureDetector(
              onTap: () {
                showModalSheet(context);
              },
              child: CircleAvatar(
                radius: 50.0,
                backgroundColor: AppColors.lightGray,
                backgroundImage: _imageFile != null ? FileImage(_imageFile!) : null,
                child: _imageFile == null ? const Icon(Icons.camera_alt) : null,
              ),
            ),
            const SizedBox(height: 5.0),
            const Text('Ảnh đại diện'),
            const SizedBox(height: 20.0),
            AppTextField(
              hintText: 'Tên',
              labelText: 'Họ và tên',
              controller: _nameController,
              onTap: () {},
            ),
            const SizedBox(height: 20.0),
            TextField(
              controller: _birthDayController,
              decoration: InputDecoration(
                labelText: 'Ngày sinh',
                hintText: 'dd-MM-yyyy', // Định dạng yêu cầu
                border: OutlineInputBorder(),
              ),
              readOnly: true, // Làm cho TextField chỉ đọc, tránh nhập trực tiếp
              onTap: () async {
                FocusScope.of(context).requestFocus(FocusNode()); // Ẩn bàn phím
                DateTime? pickedDate = await showDatePicker(
                  context: context,
                  initialDate: DateTime.now(),
                  firstDate: DateTime(1900),
                  lastDate: DateTime.now(),
                );
                if (pickedDate != null) {
                  setState(() {
                    // Định dạng lại ngày thành 'yyyy-MM-dd'
                    _birthDayController.text = DateFormat('yyyy-MM-dd').format(pickedDate);
                  });
                }
              },
            ),
            const SizedBox(height: 20.0),
            DropdownButtonFormField<String>(
              value: _selectedSex,
              decoration: InputDecoration(
                labelText: 'Giới tính',
                border: OutlineInputBorder(),
              ),
              items: [
                DropdownMenuItem(
                  value: 'M',
                  child: Text('Nam'),
                ),
                DropdownMenuItem(
                  value: 'F',
                  child: Text('Nữ'),
                ),
              ],
              onChanged: (value) {
                setState(() {
                  _selectedSex = value!;
                });
              },
            ),
            const SizedBox(height: 20.0),
            AppTextField(
              hintText: 'Giới thiệu',
              labelText: 'Giới thiệu',
              controller: _bioController,
              onTap: () {},
            ),
            const SizedBox(height: 20.0),
            AppButton.button(
              text: 'Lưu',
              width: MediaQuery.of(context).size.width,
              height: MediaQuery.of(context).size.height * .08,
              color: AppColors.secondary,
              onTap: _submit,
            ),
            const SizedBox(height: 10.0),
          ],
        ),
      ),
    );
  }

  showModalSheet(BuildContext context) {
    showModalBottomSheet(
      context: context,
      builder: (context) {
        return Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 10.0),
              child: ListTile(
                onTap: () {
                  _chooseImage(ImageSource.gallery);
                  Navigator.pop(context);
                },
                leading: const Icon(Icons.settings_rounded),
                title: const Text('Từ thư viện ảnh'),
              ),
            ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20.0),
              child: ListTile(
                onTap: () {
                  _chooseImage(ImageSource.camera);
                  Navigator.pop(context);
                },
                leading: const Icon(Icons.camera_alt_outlined),
                title: const Text('Chụp ảnh'),
              ),
            )
          ],
        );
      },
    );
  }


  void _showCustomSnackbar(String message, Color backgroundColor) {
    final overlay = Overlay.of(context);
    final overlayEntry = OverlayEntry(
      builder: (context) => Positioned(
        top: MediaQuery.of(context).viewInsets.top + 50,
        left: 0,
        right: 0,
        child: Material(
          color: Colors.transparent,
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
            decoration: BoxDecoration(
              color: backgroundColor,
              borderRadius: BorderRadius.circular(2),
            ),
            child: Text(
              message,
              style: const TextStyle(color: Colors.black),
            ),
          ),
        ),
      ),
    );

    overlay.insert(overlayEntry);
    Future.delayed(const Duration(seconds: 3), () {
      overlayEntry.remove();
    });
  }

  void _showErrorSnackbar(String message) {
    _showCustomSnackbar(message, Colors.white);
  }

  void _showSuccessSnackbar(String message) {
    _showCustomSnackbar(message, Colors.white);
  }
}