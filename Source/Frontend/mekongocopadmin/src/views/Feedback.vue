<template>
  <div class="w-[100%] h-screen">
    <!-- Navbar -->
    <div class="flex flex-col flex-1 overflow-y-auto" id="navbar">
      <div class="flex items-center justify-between h-16 bg-white border-b border-gray-200">
        <div class="flex items-center px-4">
          <input class="mx-4 w-full border rounded-md px-4 py-2" type="text" placeholder="Search" v-model="searchQuery">
        </div>
        <div class="font-mono text-[18px]">
          Feedback
        </div>
        <div class="flex items-center pr-4">
          <button class="flex items-center text-gray-500 hover:text-gray-700 focus:outline-none focus:text-gray-700" @click="fetchFeedbackData">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l-7-7 7-7m5 14l7-7-7-7" />
            </svg>
          </button>
        </div>
      </div>
      <!-- Feedback list -->
      <div>
      <div v-if="pagedFeedbackList.length > 0" class="p-4">
        <div v-for="feedback in pagedFeedbackList" :key="feedback._id" class="bg-white rounded-lg shadow-md p-4 mb-4">
          <div class="flex justify-between items-center">
            <div class="flex items-center">
              <div class="font-semibold">{{ feedback.name }}</div>
              <div class="mx-2 text-blue-600">{{ feedback.email }}</div>
            </div>
            <div>
              <button @click="updateFeedbackStatus(feedback._id, !feedback.status)" class="px-3 py-1 bg-blue-500 text-white rounded-md">{{ feedback.status ? 'Hoàn thành' : 'Chưa hoàn thành' }}</button>
              <button @click="deleteFeedback(feedback._id)" class="px-3 py-1 bg-red-500 text-white rounded-md ml-2">Xoá</button>
            </div>
          </div>
          <div class="mt-2">{{ feedback.description }}</div>
          <div class="flex">
            <div class="mt-2 text-green-600">Trạng thái: </div>
            <div class="ml-2 mt-2">{{ feedback.status }}</div>
          </div>
        </div>
      </div>
      <div v-else class="p-4">Không có dữ liệu feedback.</div>

      <div class="flex justify-center mt-4">
        <button @click="prevPage" :disabled="currentPage === 1" class="px-3 py-1 bg-blue-500 text-white rounded-md mr-2">Trước</button>
        <span v-for="page in totalPages" :key="page" :class="{ 'bg-blue-500 text-white': currentPage === page }" class="px-3 py-1 bg-gray-200 rounded-md cursor-pointer mr-2" @click="goToPage(page)">{{ page }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages" class="px-3 py-1 bg-blue-500 text-white rounded-md ml-2">Sau</button>
      </div>
    </div>
    </div>
  </div>
</template>

<script>
import api from '../services/api.service';

export default {
  data() {
    return {
      searchQuery: '',
      feedbackList: [],
      currentPage: 1,
      pageSize: 4,
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.feedbackList.length / this.pageSize);
    },
    pagedFeedbackList() {
      const startIndex = (this.currentPage - 1) * this.pageSize;
      return this.feedbackList.slice(startIndex, startIndex + this.pageSize);
    },
  },
  methods: {
    async fetchFeedbackData() {
      try {
        const response = await api.get('/api/feedback/getAll');
        this.feedbackList = response.data.data;
      } catch (error) {
        console.error('Lỗi khi lấy dữ liệu feedback:', error);
      }
    },
    async updateFeedbackStatus(feedbackId, status) {
      try {
        const response = await api.put('/api/feedback/update', { _id: feedbackId, status });
        console.log(response.data);
        // Cập nhật trạng thái feedback sau khi gọi API thành công
        const updatedFeedbackIndex = this.feedbackList.findIndex(item => item._id === feedbackId);
        if (updatedFeedbackIndex !== -1) {
          this.feedbackList[updatedFeedbackIndex].status = status;
        }
      } catch (error) {
        console.error('Lỗi khi cập nhật trạng thái feedback:', error);
      }
    },
    async deleteFeedback(feedbackId) {
      try {
        const response = await api.delete(`/api/feedback/delete/${feedbackId}`);
        console.log(response.data);
        // Xóa feedback từ danh sách sau khi xóa thành công
        this.feedbackList = this.feedbackList.filter(item => item._id !== feedbackId);
        this.currentPage = 1; // Reset trang về 1 sau khi xóa một feedback
      } catch (error) {
        console.error('Lỗi khi xoá feedback:', error);
      }
    },
    goToPage(page) {
      this.currentPage = page;
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },
  },
  mounted() {
    this.fetchFeedbackData();
  },
};
</script>


<style>
/* Thêm các kiểu CSS tùy chỉnh của bạn nếu cần */
</style>