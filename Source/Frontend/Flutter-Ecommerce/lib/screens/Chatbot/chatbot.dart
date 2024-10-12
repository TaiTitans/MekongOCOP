import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import '../../Common/Widgets/custom_app_bar.dart';
import '../../service/chatbot_service.dart';

void main() {
  runApp(ChatbotApp());
}

class ChatbotApp extends StatelessWidget {
  static const String routeName = 'ChatbotApp';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Mekong OCOP Bot',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        primarySwatch: Colors.purple,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: ChatScreen(),
    );
  }
}

class ChatScreen extends StatefulWidget {
  @override
  _ChatScreenState createState() => _ChatScreenState();
}

class _ChatScreenState extends State<ChatScreen> {
  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();  // Thêm scaffoldKey để dùng cho CustomAppBar
  final TextEditingController _controller = TextEditingController();
  final List<Map<String, String>> _messages = [];
  final ChatbotService _chatbotService = ChatbotService();
  bool _isLoading = false;

  // Hàm gửi câu hỏi và nhận phản hồi từ API
  void _sendMessage() async {
    String question = _controller.text.trim();

    if (question.isEmpty) {
      return; // Không gửi nếu người dùng chưa nhập gì
    }

    // Thêm câu hỏi của người dùng vào danh sách tin nhắn
    setState(() {
      _messages.add({"sender": "user", "message": question});
      _isLoading = true;
    });

    _controller.clear();

    try {
      // Gửi câu hỏi đến API và nhận phản hồi
      Map<String, dynamic> response = await _chatbotService.sendQuestion(question);
      setState(() {
        String botResponse = response['response'];
        // Thêm phản hồi của chatbot vào danh sách tin nhắn
        _messages.add({"sender": "bot", "message": botResponse});
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _messages.add({"sender": "bot", "message": "Xin lỗi, đã xảy ra lỗi!"});
        _isLoading = false;
      });
    }
  }

  // Widget hiển thị một tin nhắn với avatar và kiểu dáng cải tiến
  Widget _buildMessage(String sender, String message) {
    bool isUser = sender == "user";
    return Row(
      mainAxisAlignment: isUser ? MainAxisAlignment.end : MainAxisAlignment.start,
      children: [
        if (!isUser)
          CircleAvatar(
            backgroundImage: AssetImage('assets/bot_avt.jpg'), // Thay bằng ảnh avatar bot
          ),
        SizedBox(width: 10),
        Flexible(
          child: Container(
            margin: const EdgeInsets.symmetric(vertical: 5.0),
            padding: const EdgeInsets.all(12.0),
            decoration: BoxDecoration(
              color: isUser ? Colors.blue[200] : Colors.grey[300],
              borderRadius: isUser
                  ? BorderRadius.only(
                topLeft: Radius.circular(12),
                topRight: Radius.circular(12),
                bottomLeft: Radius.circular(12),
              )
                  : BorderRadius.only(
                topLeft: Radius.circular(12),
                topRight: Radius.circular(12),
                bottomRight: Radius.circular(12),
              ),
            ),
            child: Text(
              message,
              style: TextStyle(color: isUser ? Colors.white : Colors.black87),
            ),
          ),
        ),
        if (isUser)
          CircleAvatar(
            backgroundImage: AssetImage('assets/user_avt.png'),
          ),
      ],
    );
  }

  // Widget danh sách tin nhắn
  Widget _buildMessageList() {
    return ListView.builder(
      reverse: true, // Để tin nhắn mới nhất nằm ở dưới cùng
      padding: EdgeInsets.symmetric(horizontal: 10),
      itemCount: _messages.length,
      itemBuilder: (context, index) {
        final message = _messages[_messages.length - 1 - index];
        return _buildMessage(message['sender']!, message['message']!);
      },
    );
  }

  // Widget ô nhập liệu và nút gửi tin nhắn
  Widget _buildInputArea() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 8.0, vertical: 10.0),
      child: Row(
        children: [
          Expanded(
            child: TextField(
              controller: _controller,
              decoration: InputDecoration(
                hintText: "Nhập câu hỏi của bạn...",
                filled: true,
                fillColor: Colors.white,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(30),
                  borderSide: BorderSide.none,
                ),
                contentPadding: EdgeInsets.symmetric(horizontal: 20, vertical: 10),
              ),
            ),
          ),
          SizedBox(width: 10),
          GestureDetector(
            onTap: _sendMessage,
            child: CircleAvatar(
              backgroundColor: Colors.deepPurple,
              child: Icon(Icons.send, color: Colors.white),
            ),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      key: _scaffoldKey,  // Sử dụng scaffoldKey cho CustomAppBar
      appBar: PreferredSize(
        preferredSize: Size.fromHeight(134.h),
        child: CustomAppBar(
          isHome: false,
          scaffoldKey: _scaffoldKey,
          title: "Mekong OCOP Bot",  // Tiêu đề AppBar
          enableSearchField: false,  // Tắt search field cho trang chatbot
        ),
      ),
      backgroundColor: Colors.grey[200],
      body: Column(
        children: [
          Expanded(child: _buildMessageList()), // Hiển thị danh sách tin nhắn
          if (_isLoading)
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: CircularProgressIndicator(),
            ), // Hiển thị vòng tròn tải khi đang xử lý
          _buildInputArea(), // Ô nhập liệu và nút gửi
        ],
      ),
    );
  }
}
