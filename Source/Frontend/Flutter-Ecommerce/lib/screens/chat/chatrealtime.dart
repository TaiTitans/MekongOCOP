import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:socket_io_client/socket_io_client.dart' as IO;
import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;

class ChatRealTime extends StatefulWidget {
  final int storeId;

  ChatRealTime({Key? key, required this.storeId}) : super(key: key);

  static const String routeName = 'chatrealtime';

  @override
  _ChatRealTimeState createState() => _ChatRealTimeState();
}

class _ChatRealTimeState extends State<ChatRealTime> {
  final TextEditingController _messageController = TextEditingController();
  final List<Map<String, dynamic>> _messages = []; // Danh sách tin nhắn với kiểu Map
  IO.Socket? _socket;
  int? userId;
  int? sessionId;

  @override
  void initState() {
    super.initState();
    _initializeChat();
  }

  Future<void> _fetchChatHistory() async {
    String apiBaseUrl = dotenv.env['API_URL'] ?? ''; // Lấy URL từ .env
    String apiUrl = '$apiBaseUrl/common/chatMessage/session/$sessionId'; // API lấy lịch sử tin nhắn

    // Lấy accessToken từ SharedPreferences
    String? accessToken = await _getAccessToken();
    print('Access Token: $accessToken'); // Kiểm tra accessToken

    if (accessToken == null) {
      print('AccessToken không tồn tại');
      return;
    }

    try {
      final response = await http.get(
        Uri.parse(apiUrl),
        headers: {
          'Authorization': 'Bearer $accessToken', // Thêm accessToken vào headers
          'Content-Type': 'application/json',
        },
      );

      if (response.statusCode == 200) {
        // Giải mã UTF-8 và sau đó decode JSON
        final decodedResponse = utf8.decode(response.bodyBytes);
        final List<dynamic> data = jsonDecode(decodedResponse);

        setState(() {
          // Đảm bảo rằng 'sender_id' là int và 'message_content' là String
          _messages.addAll(data.map((msg) => {
            'sender_id': msg['sender_id'] is int
                ? msg['sender_id']
                : int.parse(msg['sender_id'].toString()),  // Chuyển đổi về int nếu cần
            'message_content': msg['message_content'].toString() // Đảm bảo message_content là String
          }).toList());
        });
      } else {
        print('Lỗi khi tải lịch sử tin nhắn: ${response.statusCode}');
        print('Nội dung phản hồi: ${response.body}');  // In ra nội dung phản hồi nếu có lỗi
      }
    } catch (e) {
      print('Lỗi khi kết nối API để lấy lịch sử tin nhắn: $e');
    }
  }

  Future<void> _initializeChat() async {
    await _getUserIdFromAccessToken(); // Lấy userId từ Access Token

    if (userId != null) {
      await _createChatSession(userId!, widget.storeId); // Tạo phiên chat

      if (sessionId != null) {
        await _fetchChatHistory(); // Lấy lịch sử tin nhắn
        _connectWebSocket(); // Kết nối với WebSocket
      }
    } else {
      print('User ID không hợp lệ.');
    }
  }

  // Hàm để tạo phiên chat
  Future<void> _createChatSession(int userId, int storeId) async {
    String apiBaseUrl = dotenv.env['API_URL'] ?? '';
    String apiUrl = '$apiBaseUrl/common/chatSessions/create?user_id=$userId&store_id=$storeId';

    String? accessToken = await _getAccessToken();

    if (accessToken == null) {
      print('AccessToken không tồn tại');
      return;
    }

    try {
      final response = await http.post(
        Uri.parse(apiUrl),
        headers: {
          'Authorization': 'Bearer $accessToken',
          'Content-Type': 'application/json',
        },
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        final data = jsonDecode(response.body);
        setState(() {
          sessionId = data['session_id'];
        });
        print('Tạo phiên chat thành công: Session ID = $sessionId');
      } else {
        print('Lỗi khi tạo phiên chat: ${response.statusCode}');
      }
    } catch (e) {
      print('Lỗi khi kết nối API: $e');
    }
  }

  // Hàm để kết nối WebSocket
  Future<void> _connectWebSocket() async {
    String? apiSocketUrl = dotenv.env['API_SOCKET_URL'];
    String? accessToken = await _getAccessToken();

    if (apiSocketUrl != null && accessToken != null) {
      _socket = IO.io(
        apiSocketUrl,
        IO.OptionBuilder()
            .setTransports(['websocket'])
            .enableAutoConnect()
            .setExtraHeaders({'Authorization': 'Bearer $accessToken'})
            .setQuery({'userId': userId})
            .setTimeout(10000)
            .build(),
      );

      // Lắng nghe khi kết nối thành công
      _socket?.on('connect', (_) {
        print('Kết nối thành công');
        _joinRoom(sessionId!); // Tham gia phòng chat
        _socket?.on('receive_message', (data) {
          final message = jsonDecode(data);
          print('Received message: $message');
          setState(() {
            _messages.add({
              'sender_id': message['sender_id'],
              'message_content': message['message_content']
            });
          });
        });
      });

      // Xử lý lỗi kết nối
      _socket?.on('connect_error', (error) {
        print('Lỗi kết nối: $error');
      });

      // Lắng nghe sự kiện reconnect (kết nối lại)
      _socket?.on('reconnect', (_) {
        print('Socket reconnected');
        if (sessionId != null) {
          _joinRoom(sessionId!);
        }
      });
    } else {
      print('API_SOCKET_URL hoặc Access Token không được tìm thấy.');
    }
  }

  // Hàm để tham gia phòng chat
  void _joinRoom(int sessionId) {
    _socket?.emit('join_room', sessionId.toString());
    print('Tham gia phòng: $sessionId');
  }

  // Hàm để rời phòng chat
  void _leaveRoom(int sessionId) {
    _socket?.emit('leave_room', sessionId.toString());
    print('Rời khỏi phòng: $sessionId');
  }

  // Lấy Access Token từ SharedPreferences
  Future<String?> _getAccessToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('accessToken');
  }

  // Lấy userId từ Access Token
  Future<void> _getUserIdFromAccessToken() async {
    final prefs = await SharedPreferences.getInstance();
    String? accessToken = prefs.getString('accessToken');

    if (accessToken != null) {
      Map<String, dynamic> payload = _parseJwtPayload(accessToken);
      setState(() {
        userId = int.parse(payload['sub'].split(',')[0]);
      });
    }
  }

  // Helper để giải mã JWT Token
  Map<String, dynamic> _parseJwtPayload(String token) {
    final parts = token.split('.');
    final payload = utf8.decode(base64Url.decode(base64Url.normalize(parts[1])));
    return jsonDecode(payload);
  }

  // Hàm để gửi tin nhắn qua WebSocket
  void _sendMessage(String messageContent) {
    if (messageContent.isNotEmpty && userId != null && sessionId != null) {
      final message = {
        'session_id': sessionId,
        'sender_id': userId,
        'message_content': messageContent,
      };

      _socket?.emit('send_message', message); // Gửi tin nhắn qua WebSocket

      // Hiển thị tin nhắn của chính mình
      setState(() {
        _messages.add({
          'sender_id': userId,
          'message_content': messageContent
        });
      });

      _messageController.clear(); // Xóa nội dung sau khi gửi
    }
  }

  @override
  void dispose() {
    if (sessionId != null) {
      _leaveRoom(sessionId!); // Rời khỏi phòng khi đóng màn hình
    }
    _socket?.disconnect();
    _messageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Trò chuyện'),
        backgroundColor: Colors.grey,
        elevation: 0,
      ),
      body: Column(
        children: [
          Expanded(
            child: ListView.builder(
              padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 8),
              itemCount: _messages.length,
              itemBuilder: (context, index) {
                final message = _messages[index];
                bool isMine = message['sender_id'] == userId;

                return Align(
                  alignment: isMine ? Alignment.centerRight : Alignment.centerLeft,
                  child: Padding(
                    padding: const EdgeInsets.symmetric(vertical: 5.0),
                    child: Container(
                      constraints: BoxConstraints(maxWidth: MediaQuery.of(context).size.width * 0.75),
                      decoration: BoxDecoration(
                        color: isMine ? Colors.grey : Colors.grey[300],
                        borderRadius: BorderRadius.circular(10),
                      ),
                      padding: const EdgeInsets.all(10),
                      child: Text(
                        message['message_content'],
                        style: TextStyle(
                          color: isMine ? Colors.white : Colors.black87,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ),
                  ),
                );
              },
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _messageController,
                    decoration: InputDecoration(
                      hintText: 'Nhập tin nhắn...',
                      contentPadding: EdgeInsets.symmetric(vertical: 10, horizontal: 15),
                      filled: true,
                      fillColor: Colors.white,
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(30),
                        borderSide: BorderSide(color: Colors.grey.shade400),
                      ),
                      enabledBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(30),
                        borderSide: BorderSide(color: Colors.grey.shade400),
                      ),
                    ),
                  ),
                ),
                SizedBox(width: 8),
                CircleAvatar(
                  backgroundColor: Colors.grey,
                  child: IconButton(
                    icon: Icon(Icons.send, color: Colors.white),
                    onPressed: () {
                      _sendMessage(_messageController.text);
                    },
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}