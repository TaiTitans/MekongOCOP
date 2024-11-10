<template>
  <div class="flex h-screen w-full bg-gray-100">
    <!-- Sidebar: Chat Sessions -->
    <div class="w-1/4 min-w-[300px] bg-white border-r border-gray-200">
      <div class="px-4 py-2 border-b border-gray-200 flex items-center justify-between">
        <h2 class="text-lg font-semibold">Cuộc trò chuyện</h2>
      </div>

      <!-- Chat Sessions List -->
      <div v-if="sessions.length > 0" class="h-[calc(100vh-3.5rem)] overflow-y-auto">
        <div v-for="session in sessions" 
             :key="session.session_id"
             @click="selectSession(session)"
             :class="['p-3 cursor-pointer flex items-center transition-all hover:bg-gray-100',
                     selectedSession && selectedSession.session_id === session.session_id ? 'bg-gray-200' : '']">
          <img :src="session.user_profile_image || defaultAvatar" 
               alt="Profile Image" 
               class="w-12 h-12 rounded-full mr-4 object-cover" />
          <div class="flex-1">
            <p class="text-sm font-medium text-gray-800">{{ session.full_name || session.user_id }}</p>
            <p class="text-xs text-gray-500">Cửa hàng: {{ session.store_id }}</p>
          </div>
          <span class="text-xs text-gray-500">{{ new Date(session.created_at).toLocaleDateString() }}</span>
        </div>
      </div>
    </div>

    <!-- Chat Box -->
    <div class="flex-1 bg-white flex flex-col">
      <!-- Messages Area -->
      <div class="flex-1 p-6 overflow-y-auto message-container">
        <div v-if="messages.length > 0" class="space-y-4">
          <div v-for="message in messages" 
               :key="message.message_id"
               :class="['max-w-[60%] p-4 rounded-lg flex items-start',
                        message.sender_id === storeId ? 'ml-auto bg-indigo-500 text-white flex-row-reverse' : 'bg-gray-100 text-gray-800']">
            <img :src="message.sender_id === storeId ? require('../../assets/default_avt.jpg') : selectedSession.user_profile_image || require('../../assets/default_avt.jpg')" 
                 alt="Profile Image" 
                 class="w-8 h-8 rounded-full object-cover"
                 :class="message.sender_id === storeId ? 'ml-2' : 'mr-2'" />
            <div class="flex-1">
              <div class="flex items-center mb-2">
                <span class="font-medium mr-2">{{ message.sender_id === storeId ? 'Tôi' : selectedSession.full_name || selectedSession.user_id }}</span>
                <span class="text-xs opacity-75">{{ new Date(message.created_at).toLocaleTimeString() }}</span>
              </div>
              <p>{{ message.message_content }}</p>
            </div>
          </div>
        </div>

        <!-- No messages yet -->
        <div v-else class="flex items-center justify-center h-full text-gray-500">
          <div class="text-center">
            <i class="fas fa-comments text-4xl mb-2"></i>
            <p>Chưa có tin nhắn nào</p>
          </div>
        </div>
      </div>

      <!-- Message Input -->
      <div class="border-t border-gray-200 p-4">
        <div class="flex gap-4">
          <input type="text" 
                 v-model="newMessage" 
                 @keyup.enter="sendMessage" 
                 placeholder="Nhập tin nhắn..." 
                 class="flex-1 border rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-indigo-500">
          <button @click="sendMessage" 
                  class="bg-indigo-500 text-white px-6 py-2 rounded-lg hover:bg-indigo-600 transition-colors">
            Gửi
          </button>
        </div>
      </div>
      
      <!-- Scroll to bottom button -->
      <div v-if="hasNewMessages" class="absolute bottom-10 right-10">
        <button @click="scrollToBottom" class="bg-indigo-500 text-white p-3 rounded-full shadow-lg">
          <i class="fas fa-arrow-down"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../../services/api.service';
import { io } from "socket.io-client";
import defaultAvatar from '../../assets/default_avt.jpg'; 

export default {
  name: 'Chat',
  data() {
    return {
      socket: null,
      sessions: [],
      messages: [],
      selectedSession: null,
      newMessage: '',
      storeId: null,
      userId: null,
      hasNewMessages: false, // Flag to detect new messages
    };
  },
  async mounted() {
    await this.getUserId();
    await this.getStoreId();

    this.socket = io('ws://localhost:8888', {
      query: { sessionId: this.storeId },
    });

    this.socket.on('connect', () => {
      console.log('Connected to Socket.IO server');
    });

    this.socket.on('disconnect', () => {
      console.log('Socket.IO connection closed');
    });

    // Handle receiving messages
    this.socket.on('receive_message', (messageData) => {
      console.log('Received new message:', messageData);
      if (messageData.session_id === this.selectedSession?.session_id) {
        this.messages.push(messageData);
        this.hasNewMessages = true; // Mark as new message received
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      } else {
        console.log('Message received for a different session:', messageData.session_id);
      }
    });

    await this.getChatSessions();
  },
  methods: {
    async getUserId() {
      try {
        const response = await api.get('api/v1/user/profile');
        this.userId = response.data.data.user_id;
      } catch (err) {
        console.error('Failed to fetch user_id.', err);
      }
    },
    async getStoreId() {
      try {
        const response = await api.get('api/v1/seller/store');
        this.storeId = response.data.data.store_id;
      } catch (err) {
        console.error('Failed to fetch store_id.', err);
      }
    },
    async getChatSessions() {
      try {
        const response = await api.get('api/v1/seller/chatSessions/store');
        this.sessions = await Promise.all(response.data.map(async (session) => {
          const profile = await this.fetchProfile(session.user_id);
          return { ...session, full_name: profile.full_name, user_profile_image: profile.user_profile_image };
        }));
      } catch (err) {
        console.error('Failed to load chat sessions.', err);
      }
    },
    async fetchProfile(userId) {
      try {
        const response = await api.get(`api/v1/seller/profile/${userId}`);
        return response.data;
      } catch (err) {
        console.error('Failed to fetch profile.', err);
        return { full_name: userId, user_profile_image: defaultAvatar };
      }
    },
    selectSession(session) {
      if (!this.selectedSession || this.selectedSession.session_id !== session.session_id) {
        if (this.selectedSession?.session_id) {
          this.socket.emit('leave_room', `chat_room_${this.selectedSession.session_id}`);
        }

        this.selectedSession = session;
        this.socket.emit('join_room', `chat_room_${session.session_id}`);
        this.getMessages(session.session_id);
      }
    },
    async getMessages(sessionId) {
      try {
        const response = await api.get(`api/v1/common/chatMessage/session/${sessionId}`);
        this.messages = response.data;
      } catch (err) {
        console.error('Failed to load messages.', err);
      }
    },
    sendMessage() {
      if (this.newMessage.trim() === '' || !this.selectedSession || !this.storeId) return;

      const messageData = {
        session_id: this.selectedSession.session_id,
        sender_id: this.storeId,
        message_content: this.newMessage,
        created_at: new Date(),
      };

      this.socket.emit('send_message', messageData);
      this.newMessage = '';
    },
    scrollToBottom() {
      const messageContainer = document.querySelector('.message-container');
      if (messageContainer) {
        messageContainer.scrollTop = messageContainer.scrollHeight;
      }
      this.hasNewMessages = false; // Reset the new message flag
    },
  },
  beforeDestroy() {
    if (this.socket) {
      this.socket.disconnect();
    }
  },
};
</script>

<style scoped>
/* Custom scrollbar */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #555;
}

img {
  object-fit: cover;
}
.message-container {
  padding-bottom: 80px; /* Adjust to allow for the input field */
}
</style>