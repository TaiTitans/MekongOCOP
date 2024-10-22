import 'package:flutter/material.dart';

import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';

import '../../service/chatsocket_service.dart'; // For decoding JWT token

class ChatRealTime extends StatefulWidget {
  final int storeId;

  ChatRealTime({Key? key, required this.storeId}) : super(key: key);

  static const String routeName = 'chatrealtime';

  @override
  _ChatRealTimeState createState() => _ChatRealTimeState();
}

class _ChatRealTimeState extends State<ChatRealTime> {
  final TextEditingController _messageController = TextEditingController();
  final List<String> _messages = [];
  late ChatSocketService _chatSocketService;
  int? userId;

  @override
  void initState() {
    super.initState();
    _chatSocketService = ChatSocketService();
    _initializeChat();
  }

  // Initialize chat: fetch user ID, initialize socket, and create new session
  Future<void> _initializeChat() async {
    _chatSocketService.initializeSocket(); // Khởi tạo socket trước khi lấy userId

    await _getUserIdFromAccessToken(); // Lấy userId sau khi khởi tạo socket

    // Listen for incoming messages from the socket
    _chatSocketService.socket?.on('receive_message', (data) {
      setState(() {
        _messages.add(data['message_content']);
      });
    });

    // Khi socket kết nối thành công, tạo session chat mới
    _chatSocketService.socket?.on('connect', (_) {
      if (userId != null) {
        _chatSocketService.createNewChatSession(userId!, widget.storeId);
      } else {
        print('User ID is not available');
      }
    });
  }
  // Fetch user ID from the access token stored in SharedPreferences
  Future<void> _getUserIdFromAccessToken() async {
    final prefs = await SharedPreferences.getInstance();
    String? accessToken = prefs.getString('accessToken');

    if (accessToken != null) {
      Map<String, dynamic> payload = _parseJwtPayload(accessToken);
      setState(() {
        userId = int.parse(payload['sub'].split(',')[0]); // Extract the user_id from the token
      });
    }
  }

  // Helper function to decode JWT token
  Map<String, dynamic> _parseJwtPayload(String token) {
    final parts = token.split('.');
    final payload = utf8.decode(base64Url.decode(base64Url.normalize(parts[1])));
    return jsonDecode(payload);
  }

  @override
  void dispose() {
    _chatSocketService.disconnectSocket();
    _messageController.dispose();
    super.dispose();
  }

  // Function to send a message via the socket
  void _sendMessage(String messageContent) {
    if (messageContent.isNotEmpty && userId != null) {
      // Send message via socket
      _chatSocketService.sendMessage(messageContent, userId!, widget.storeId);

      // Add the sent message to the local list
      setState(() {
        _messages.add(messageContent);
      });

      // Clear the message input field
      _messageController.clear();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Trò chuyện'),
      ),
      body: Column(
        children: [
          // Display list of messages
          Expanded(
            child: ListView.builder(
              itemCount: _messages.length,
              itemBuilder: (context, index) {
                return ListTile(
                  title: Text(_messages[index]),
                );
              },
            ),
          ),
          // Message input field and send button
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _messageController,
                    decoration: InputDecoration(
                      hintText: 'Enter your message...',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(10),
                      ),
                    ),
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
}

