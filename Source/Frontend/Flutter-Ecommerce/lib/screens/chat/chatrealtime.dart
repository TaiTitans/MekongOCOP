import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:socket_io_client/socket_io_client.dart' as IO;
import 'package:flutter_dotenv/flutter_dotenv.dart';

import '../../Common/Widgets/custom_app_bar.dart';

class ChatRealTime extends StatefulWidget {
  final int storeId;

  ChatRealTime({Key? key, required this.storeId}) : super(key: key);

  static const String routeName = 'chatrealtime';

  @override
  _ChatRealTimeState createState() => _ChatRealTimeState();
}

class _ChatRealTimeState extends State<ChatRealTime> {
  final TextEditingController _messageController = TextEditingController();
  final List<Map<String, dynamic>> _messages = [];
  final ScrollController _scrollController = ScrollController();
  IO.Socket? _socket;
  int? userId;
  int? sessionId;
  bool _hasNewMessages = false; // Flag to track new messages

  @override
  void initState() {
    super.initState();
    _initializeChat();
  }

  Future<void> _initializeChat() async {
    await _getUserIdFromAccessToken();
    if (userId != null) {
      await _createChatSession(userId!, widget.storeId);
      if (sessionId != null) {
        await _fetchChatHistory();
        _connectWebSocket();
      }
    } else {
      print('User ID không hợp lệ.');
    }
  }

  Future<void> _fetchChatHistory() async {
    String apiBaseUrl = dotenv.env['API_URL'] ?? '';
    String apiUrl = '$apiBaseUrl/common/chatMessage/session/$sessionId';
    String? accessToken = await _getAccessToken();

    if (accessToken == null) return;

    try {
      final response = await http.get(
        Uri.parse(apiUrl),
        headers: {'Authorization': 'Bearer $accessToken', 'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200) {
        final decodedResponse = utf8.decode(response.bodyBytes);
        final List<dynamic> data = jsonDecode(decodedResponse);
        setState(() {
          _messages.addAll(data.map((msg) => {
            'sender_id': int.parse(msg['sender_id'].toString()),
            'message_content': msg['message_content'].toString()
          }).toList());
        });
      } else {
        print('Lỗi khi tải lịch sử tin nhắn: ${response.statusCode}');
      }
    } catch (e) {
      print('Lỗi khi kết nối API để lấy lịch sử tin nhắn: $e');
    }
  }

  Future<void> _createChatSession(int userId, int storeId) async {
    String apiBaseUrl = dotenv.env['API_URL'] ?? '';
    String apiUrl = '$apiBaseUrl/common/chatSessions/create?user_id=$userId&store_id=$storeId';
    String? accessToken = await _getAccessToken();

    if (accessToken == null) return;

    try {
      final response = await http.post(
        Uri.parse(apiUrl),
        headers: {'Authorization': 'Bearer $accessToken', 'Content-Type': 'application/json'},
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        final data = jsonDecode(response.body);
        setState(() {
          sessionId = data['session_id'];
        });
      } else {
        print('Lỗi khi tạo phiên chat: ${response.statusCode}');
      }
    } catch (e) {
      print('Lỗi khi kết nối API: $e');
    }
  }

  Future<void> _connectWebSocket() async {
    String? apiSocketUrl = dotenv.env['API_SOCKET_URL'];
    String? accessToken = await _getAccessToken();

    if (apiSocketUrl != null && accessToken != null) {
      _socket = IO.io(
        '$apiSocketUrl?sessionId=$sessionId', // Ensure sessionId is passed in the URL
        IO.OptionBuilder()
            .setTransports(['websocket'])
            .enableAutoConnect()
            .setExtraHeaders({'Authorization': 'Bearer $accessToken'})
            .setTimeout(10000)
            .build(),
      );

      _socket?.on('connect', (_) {
        print('Kết nối thành công');
        _joinRoom(sessionId!);
      });

      _socket?.on('receive_message', (data) {
        try {
          final message = data is String ? jsonDecode(data) : data;

          // Check if the message is already in the list before adding
          setState(() {
            if (!_messages.any((msg) => msg['message_content'] == message['message_content'] && msg['sender_id'] == message['sender_id'])) {
              _messages.add({
                'sender_id': message['sender_id'] ?? -1,
                'message_content': message['message_content'] ?? 'No content',
              });
              _hasNewMessages = true; // Set the flag when new message is received
            }
          });

          // Scroll to the bottom when a new message is received
          Future.delayed(Duration.zero, () {
            _scrollController.animateTo(
              _scrollController.position.maxScrollExtent,
              duration: const Duration(milliseconds: 300),
              curve: Curves.easeOut,
            );
          });
        } catch (e) {
          print('Failed to decode incoming message: $e');
        }
      });

      _socket?.on('connect_error', (error) {
        print('Lỗi kết nối: $error');
      });

      _socket?.on('reconnect_attempt', (attempt) {
        print('Đang thử kết nối lại, lần thử: $attempt');
      });

      _socket?.on('reconnect', (_) {
        print('Socket reconnected');
        if (sessionId != null) {
          _socket?.emit('leave_room', sessionId.toString());
          _joinRoom(sessionId!);
        }
      });
    } else {
      print('API_SOCKET_URL hoặc Access Token không được tìm thấy.');
    }
  }

  void _joinRoom(int sessionId) {
    _socket?.emit('join_room', sessionId.toString());
  }

  void _sendMessage(String messageContent) {
    if (messageContent.isNotEmpty && userId != null && sessionId != null) {
      final message = {
        'session_id': sessionId,
        'sender_id': userId,
        'message_content': messageContent,
      };

      // Send the message via WebSocket
      _socket?.emit('send_message', message);

      // Add the message to the local list, only if it's not already there
      setState(() {
        // Prevent duplicate messages by checking sender_id and content
        if (!_messages.any((msg) => msg['message_content'] == messageContent && msg['sender_id'] == userId)) {
          _messages.add({
            'sender_id': userId,
            'message_content': messageContent,
          });
        }
      });
      _messageController.clear();

      // Scroll to the bottom when a new message is sent
      Future.delayed(Duration.zero, () {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      });

      // Lắng nghe phản hồi từ server (nếu cần)
      _socket?.on('message_sent', (data) {
        print('Message sent successfully: $data');
      });

      _socket?.on('message_error', (error) {
        print('Error sending message: $error');
        // Có thể thêm logic xử lý lỗi ở đây
      });
    }
  }

  Future<String?> _getAccessToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('accessToken');
  }

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

  Map<String, dynamic> _parseJwtPayload(String token) {
    final parts = token.split('.');
    final payload = utf8.decode(base64Url.decode(base64Url.normalize(parts[1])));
    return jsonDecode(payload);
  }

  @override
  void dispose() {
    if (sessionId != null) {
      _leaveRoom(sessionId!);
    }
    _socket?.disconnect();
    _messageController.dispose();
    super.dispose();
  }

  void _leaveRoom(int sessionId) {
    _socket?.emit('leave_room', sessionId.toString());
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _buildAppBar(context),
      body: Column(
        children: [
          Expanded(
            child: ListView.builder(
              controller: _scrollController,
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
          if (_hasNewMessages)
            IconButton(
              icon: Icon(Icons.arrow_downward),
              onPressed: () {
                _scrollController.animateTo(
                  _scrollController.position.maxScrollExtent,
                  duration: const Duration(milliseconds: 300),
                  curve: Curves.easeOut,
                );
                setState(() {
                  _hasNewMessages = false; // Reset the flag after scrolling
                });
              },
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
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(20),
                        borderSide: BorderSide.none,
                      ),
                      filled: true,
                      fillColor: Colors.grey[200],
                    ),
                    onSubmitted: (messageContent) => _sendMessage(messageContent),
                  ),
                ),
                IconButton(
                  icon: Icon(Icons.send),
                  onPressed: () {
                    _sendMessage(_messageController.text);
                  },
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
  PreferredSize _buildAppBar(BuildContext context) {
    return PreferredSize(
      preferredSize: Size(double.infinity, MediaQuery.of(context).size.height * .20),
      child: CustomAppBar(
        isHome: false,
        title: '',
        fixedHeight: 120.0,
        enableSearchField: false,
        leadingIcon: Icons.arrow_back,
        leadingOnTap: () {
          Navigator.pop(context);
        },
      ),
    );
  }
}