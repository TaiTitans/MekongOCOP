import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:intl/intl.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';

import '../../model/chatsession.dart';
import '../../model/storedata.dart';
import 'chatrealtime.dart';
class ChatListScreen extends StatefulWidget {
  static const String routeName = 'chatlistscreen';

  const ChatListScreen({Key? key}) : super(key: key);

  @override
  _ChatListScreenState createState() => _ChatListScreenState();
}

class _ChatListScreenState extends State<ChatListScreen> {
  List<ChatSession> chatSessions = [];
  Map<int, StoreData> storeDataMap = {};
  bool isLoading = true;
  String? accessToken;
  final String apiUrl = dotenv.env['API_URL'] ?? '';

  @override
  void initState() {
    super.initState();
    _loadToken();
  }

  Future<void> _loadToken() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      accessToken = prefs.getString('accessToken');
    });
    if (accessToken != null) {
      fetchChatSessions();
    } else {
      // Handle case when token is not available
      setState(() {
        isLoading = false;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please login first')),
      );
      // Optionally navigate to login screen
      // Navigator.of(context).pushReplacement(
      //   MaterialPageRoute(builder: (context) => LoginScreen()),
      // );
    }
  }
  Future<void> fetchStoreData(int storeId) async {
    try {
      final response = await http.get(
        Uri.parse('$apiUrl/common/store/data?storeId=$storeId'),
        headers: {
          'Authorization': 'Bearer $accessToken',
          'Content-Type': 'application/json',
        },
      );

      if (response.statusCode == 200) {
        final jsonResponse = json.decode(response.body);
        if (jsonResponse['status'] == 'Success') {
          final storeData = StoreData.fromJson(jsonResponse['data']);
          setState(() {
            storeDataMap[storeId] = storeData;
          });
        }
      }
    } catch (e) {
      print('Error fetching store data: $e');
    }
  }

  Future<void> fetchChatSessions() async {
    try {
      final response = await http.get(
        Uri.parse('$apiUrl/user/chatSessions'),
        headers: {
          'Authorization': 'Bearer $accessToken',
          'Content-Type': 'application/json',
        },
      );

      if (response.statusCode == 200) {
        final List<dynamic> data = json.decode(response.body);
        setState(() {
          chatSessions = data.map((json) => ChatSession.fromJson(json)).toList();
          isLoading = false;
        });

        // Fetch store data for each chat session
        for (var session in chatSessions) {
          await fetchStoreData(session.storeId);
        }
      } else if (response.statusCode == 401) {
        final prefs = await SharedPreferences.getInstance();
        await prefs.remove('accessToken');
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Session expired. Please login again')),
        );
      } else {
        throw Exception('Failed to load chat sessions');
      }
    } catch (e) {
      setState(() {
        isLoading = false;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Error: ${e.toString()}')),
      );
    }
  }

  Future<void> _refreshChats() async {
    setState(() {
      isLoading = true;
    });
    await fetchChatSessions();
  }

  String formatDateTime(int timestamp) {
    final DateTime date = DateTime.fromMillisecondsSinceEpoch(timestamp);
    return DateFormat('dd/MM/yyyy HH:mm').format(date);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Trò chuyện', style: TextStyle(color: Colors.white)),
        backgroundColor: Colors.deepPurple,
      ),
      body: RefreshIndicator(
        onRefresh: _refreshChats,
        child: isLoading
            ? const Center(child: CircularProgressIndicator())
            : chatSessions.isEmpty
            ? const Center(child: Text('No chat sessions available'))
            : ListView.builder(
          itemCount: chatSessions.length,
          itemBuilder: (context, index) {
            final session = chatSessions[index];
            final lastMessage = session.messages.isNotEmpty ? session.messages.last : null;
            final storeData = storeDataMap[session.storeId];

            return Card(
              margin: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              child: ListTile(
                leading: CircleAvatar(
                  backgroundColor: Colors.transparent,
                  child: storeData != null
                      ? ClipOval(
                    child: Image.network(
                      storeData.storeLogo,
                      width: 40,
                      height: 40,
                      fit: BoxFit.cover,
                      errorBuilder: (context, error, stackTrace) {
                        return Container(
                          color: Colors.blue.shade200,
                          child: Text('S${session.storeId}'),
                        );
                      },
                    ),
                  )
                      : Container(
                    color: Colors.blue.shade200,
                    child: Text('S${session.storeId}'),
                  ),
                ),
                title: Text(
                  storeData?.storeName ?? 'Store #${session.storeId}',
                  style: const TextStyle(fontWeight: FontWeight.bold),
                ),
                subtitle: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    if (storeData != null)
                      Text(
                        storeData.storeAddress,
                        style: const TextStyle(fontSize: 12),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                      ),
                    if (lastMessage != null) ...[
                      Text(
                        lastMessage.messageContent,
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                      ),
                      Text(
                        formatDateTime(lastMessage.createdAt),
                        style: const TextStyle(
                          fontSize: 12,
                          color: Colors.grey,
                        ),
                      ),
                    ],
                  ],
                ),
                trailing: lastMessage != null && !lastMessage.isRead
                    ? Container(
                  width: 12,
                  height: 12,
                  decoration: const BoxDecoration(
                    color: Colors.blue,
                    shape: BoxShape.circle,
                  ),
                )
                    : null,
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => ChatRealTime(
                        storeId: session.storeId,
                      ),
                    ),
                  );
                },
              ),
            );
          },
        ),
      ),
    );
  }
}