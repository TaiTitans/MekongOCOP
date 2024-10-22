import 'package:dio/dio.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'package:socket_io_client/socket_io_client.dart' as IO;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:dio/dio.dart';

class ChatSocketService {
  final Dio _dio = Dio();
  IO.Socket? _socket;

  // Expose the socket to other classes
  IO.Socket? get socket => _socket;

  // Initialize the Socket.IO connection
  void initializeSocket() {
    // Use the environment variable for the socket URL
    final String socketUrl = dotenv.env['API_SOCKET_URL'] ?? '';
    _socket = IO.io(
      socketUrl,
      IO.OptionBuilder()
          .setTransports(['websocket'])
          .enableForceNew()
          .build(), // Loại bỏ phần extraHeaders
    );

    _socket?.onConnect((_) {
      print('Socket connected');
    });

    _socket?.onDisconnect((_) {
      print('Socket disconnected');
    });

    // Listen for received messages
    _socket?.on('receive_message', (data) {
      print('Received message: $data');
    });

    // Listen for new chat sessions broadcasted to the seller
    _socket?.on('new_chat_session_to_seller', (data) {
      print('New chat session received: $data');
    });
  }

  // Emit message to backend
  void sendMessage(String messageContent, int senderId, int storeId) {
    final messageData = {
      'message_content': messageContent,
      'sender_id': senderId,
      'store_id': storeId,
    };

    _socket?.emit('send_message', messageData);
    print('Message sent: $messageData');
  }

  // Emit new chat session creation request to backend
  void createNewChatSession(int userId, int storeId) {
    final sessionData = {
      'user_id': userId,
      'store_id': storeId,
    };

    _socket?.emit('new_chat_session', sessionData);
    print('New chat session requested: $sessionData');
  }

  // Disconnect the socket when done
  void disconnectSocket() {
    _socket?.disconnect();
  }
  // The existing Dio API call methods
  String getChatSessionUrl() {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/user/chatSessions';
  }

  String getChatUrl(int sessionId) {
    final String apiUrl = dotenv.env['API_URL'] ?? '';
    return '$apiUrl/common/chatMessage/session/$sessionId';
  }

  Future<List<dynamic>> getChatSession() async {
    final prefs = await SharedPreferences.getInstance();
    String? accessToken = prefs.getString('accessToken');

    if (accessToken == null) {
      throw Exception('Access token is not available');
    }

    try {
      String url = getChatSessionUrl();
      final response = await _dio.get(
        url,
        options: Options(
          headers: {
            'Authorization': 'Bearer $accessToken',
          },
        ),
      );

      if (response.statusCode == 200) {
        return response.data as List<dynamic>;
      } else {
        throw Exception('Failed to load chat sessions');
      }
    } catch (e) {
      throw Exception('Error fetching chat sessions: $e');
    }
  }

  Future<List<dynamic>> getChat(int sessionId) async {
    final prefs = await SharedPreferences.getInstance();
    String? accessToken = prefs.getString('accessToken');

    if (accessToken == null) {
      throw Exception('Access token is not available');
    }

    try {
      String url = getChatUrl(sessionId);
      final response = await _dio.get(
        url,
        options: Options(
          headers: {
            'Authorization': 'Bearer $accessToken',
          },
        ),
      );

      if (response.statusCode == 200) {
        return response.data as List<dynamic>;
      } else {
        throw Exception('Failed to load chat messages');
      }
    } catch (e) {
      throw Exception('Error fetching chat messages: $e');
    }
  }
}
