class ChatSession {
  final int sessionId;
  final int userId;
  final int storeId;
  final List<Message> messages;

  ChatSession({
    required this.sessionId,
    required this.userId,
    required this.storeId,
    required this.messages,
  });

  factory ChatSession.fromJson(Map<String, dynamic> json) {
    return ChatSession(
      sessionId: json['session_id'],
      userId: json['user_id'],
      storeId: json['store_id'],
      messages: (json['messages'] as List)
          .map((m) => Message.fromJson(m))
          .toList(),
    );
  }
}

class Message {
  final int messageId;
  final int sessionId;
  final int storeId;
  final int senderId;
  final String messageContent;
  final int createdAt;
  final bool isRead;

  Message({
    required this.messageId,
    required this.sessionId,
    required this.storeId,
    required this.senderId,
    required this.messageContent,
    required this.createdAt,
    required this.isRead,
  });

  factory Message.fromJson(Map<String, dynamic> json) {
    return Message(
      messageId: json['message_id'],
      sessionId: json['session_id'],
      storeId: json['store_id'],
      senderId: json['sender_id'],
      messageContent: json['message_content'],
      createdAt: json['created_at'],
      isRead: json['is_read'],
    );
  }
}
