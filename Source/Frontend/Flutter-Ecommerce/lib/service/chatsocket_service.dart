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
    // Sử dụng URL từ .env hoặc mặc định
    final String socketUrl = dotenv.env['API_SOCKET_URL'] ?? 'http://192.168.1.14:8080/ws';
    print('Socket URL: $socketUrl'); // Log URL

    _socket = IO.io(
      socketUrl,
      IO.OptionBuilder()
          .setTransports(['polling', 'websocket']) // Đảm bảo 'polling' được sử dụng nếu WebSocket thất bại
          .enableForceNew()
          .setTimeout(5000) // Cấu hình timeout cho kết nối
          .build(),
    );

    // Kết nối thành công
    _socket?.onConnect((_) {
      print('Socket connected: ${_socket?.id}'); // Log id kết nối
    });

    // Mất kết nối
    _socket?.onDisconnect((_) {
      print('Socket disconnected');
    });

    // Nhận lỗi kết nối
    _socket?.on('connect_error', (error) {
      print('Connection Error: $error');
    });

    // Quá thời gian kết nối
    _socket?.on('connect_timeout', (data) {
      print('Connection Timeout');
    });

    // Lắng nghe tin nhắn từ server
    _socket?.on('receive_message', (data) {
      print('Received message: $data');
    });

    // Lắng nghe sự kiện phiên trò chuyện mới
    _socket?.on('new_chat_session_to_seller', (data) {
      print('New chat session received: $data');
    });

    // Thông tin thêm về trạng thái socket
    _socket?.on('reconnect_attempt', (data) {
      print('Reconnecting... Attempt: $data');
    });

    _socket?.on('reconnect', (attempt) {
      print('Reconnected successfully after $attempt attempts');
    });

    _socket?.on('error', (error) {
      print('General Error: $error');
    });

    print('Socket initialization completed'); // Log hoàn tất khởi tạo
  }


  // Emit message to backend
  void sendMessage(String messageContent, int senderId, int storeId) {
    final messageData = {
      'message_content': messageContent,
      'sender_id': senderId,
      'store_id': storeId,
    };

    _socket?.emit('send_message', messageData);
    print('Message sent: $messageData to storeId: $storeId');
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
