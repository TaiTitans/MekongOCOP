<template>
  <div class="flex flex-col min-h-screen mx-auto bg-gray-100">
    <NavbarS title="Duyệt đơn" />

    <div class="container mx-auto px-8 py-16 bg-white rounded-md shadow-xl justify-center items-center mt-24">
      <h1 class="text-2xl font-semibold text-gray-900 mb-6">Danh sách yêu cầu trở người bán hàng</h1>

      <!-- Ô nhập liệu tìm kiếm -->
      <input
        type="text"
        v-model="searchQuery"
        @input="searchSellers"
        placeholder="Tìm kiếm theo ID người bán..."
        class="border rounded p-2 mb-4"
      />

      <!-- Bảng hiển thị thông tin người bán -->
      <table class="min-w-full bg-white border border-gray-200 rounded">
        <thead>
          <tr>
            <th class="py-2 px-4 border-b">ID Người Bán</th>
            <th class="py-2 px-4 border-b">ID Người Dùng</th>
            <th class="py-2 px-4 border-b">Chứng Nhận</th>
            <th class="py-2 px-4 border-b">Trạng Thái</th>
            <th class="py-2 px-4 border-b">Ngày Yêu Cầu</th>
            <th class="py-2 px-4 border-b">Hành Động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="seller in paginatedSellers" :key="seller.seller_id">
            <td class="py-2 px-4 border-b">{{ seller.seller_id }}</td>
            <td class="py-2 px-4 border-b">
              <button @click="fetchUserProfile(seller.user_id)" class="text-blue-500 hover:underline">
                {{ seller.user_id }}
              </button>
            </td>
            <td class="py-2 px-4 border-b">
              <button @click="openModal(seller.certification)" class="text-blue-500 hover:underline">
                Xem Chứng Nhận
              </button>
            </td>
            <td class="py-2 px-4 border-b">
              {{ 
                seller.status === 'Pending' ? 'Đang chờ duyệt' : 
                seller.status === 'Accept' ? 'Đã duyệt' : 
                seller.status === 'Reject' ? 'Đã từ chối' : seller.status 
              }}
            </td>
            <td class="py-2 px-4 border-b">{{ formatDate(seller.request_date) }}</td>
            <td class="py-2 px-4 border-b">
              <button
                v-if="seller.status === 'Pending'"
                @click="approveSeller(seller.seller_id)"
                class="px-4 py-2 bg-cyan-500 text-white rounded-lg hover:bg-gray-200 shadow-md mr-2"
              >
                Duyệt
              </button>
              <button
                v-if="seller.status === 'Pending'"
                @click="rejectSeller(seller.seller_id)"
                class="px-4 py-2 bg-white-500 text-black rounded-lg hover:bg-red-600 shadow-md"
              >
                Từ chối
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Phân trang -->
      <div class="flex justify-between mt-4">
        <button @click="prevPage" :disabled="currentPage === 1" class="px-4 py-2 bg-gray-300 rounded">Trước</button>
        <span>Trang {{ currentPage }} trên {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages" class="px-4 py-2 bg-gray-300 rounded">Tiếp theo</button>
      </div>
    </div>

    <!-- Modal hiển thị chứng nhận -->
    <vue-easy-lightbox
      :visible="showModal"
      :imgs="[selectedImage]"
      @hide="closeModal"
    />

    <!-- Modal hiển thị thông tin người dùng -->
    <div v-if="showProfileModal" class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
      <div class="bg-white p-6 rounded-lg shadow-lg relative">
        <button @click="closeProfileModal" class="absolute top-1 right-2 text-gray-500 text-lg font-bold">
          X
        </button>
        <h2 class="text-xl font-bold">{{ userProfile.full_name }}</h2>
        <img :src="userProfile.user_profile_image" alt="Ảnh Người Dùng" class="w-32 h-32 rounded-full mt-2" />
        <p><strong>Ngày Sinh:</strong> {{ formatBirthday(userProfile.birthday) }}</p>
        <p><strong>Giới Tính:</strong> {{ formatGender(userProfile.sex) }}</p>
        <p><strong>Thông Tin:</strong> {{ userProfile.bio }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import NavbarS from '../../components/NavbarS.vue';
import api from '../../services/api.service';
import VueEasyLightbox from 'vue-easy-lightbox';

export default {
  name: 'ApproveAdmin',
  components: {
    NavbarS,
    VueEasyLightbox,
  },
  data() {
    return {
      sellers: [], // Danh sách người bán
      searchQuery: '', // Truy vấn tìm kiếm
      paginatedSellers: [], // Danh sách người bán phân trang
      currentPage: 1, // Trang hiện tại
      itemsPerPage: 10, // Số lượng người bán trên mỗi trang
      showModal: false, // Trạng thái hiển thị modal chứng nhận
      selectedImage: '', // Đường dẫn đến hình ảnh được chọn
      showProfileModal: false, // Trạng thái hiển thị modal thông tin người dùng
      userProfile: {} // Thông tin người dùng
    };
  },
  created() {
    this.fetchSellers();
  },
  methods: {
    formatGender(gender) {
      return gender === 'M' ? 'Nam' : gender === 'F' ? 'Nữ' : 'Không xác định';
    },
    formatBirthday(birthday) {
      const date = new Date(birthday);
      const day = String(date.getDate()).padStart(2, '0');
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const year = date.getFullYear();
      
      return `${day}/${month}/${year}`;
    },
    async fetchSellers() {
      try {
        const response = await api.get('/api/v1/admin/seller');
        this.sellers = response.data.data; // Lưu danh sách người bán vào state
        this.updatePaginatedSellers();
      } catch (error) {
        console.error('Error fetching sellers:', error);
      }
    },
    updatePaginatedSellers() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      this.paginatedSellers = this.sellers.slice(start, end);
    },
    async approveSeller(sellerId) {
      try {
        await api.patch(`/api/v1/admin/seller/${sellerId}/status?status=Accept`);
        this.fetchSellers(); // Cập nhật lại danh sách người bán sau khi duyệt
      } catch (error) {
        console.error('Error approving seller:', error);
      }
    },
    async rejectSeller(sellerId) {
      try {
        await api.patch(`/api/v1/admin/seller/${sellerId}/status?status=Reject`);
        this.fetchSellers(); // Cập nhật lại danh sách sau khi từ chối
      } catch (error) {
        console.error('Error rejecting seller:', error);
      }
    },
    openModal(imageUrl) {
      this.selectedImage = imageUrl; // Cập nhật hình ảnh được chọn
      this.showModal = true; // Hiển thị modal
    },
    closeModal() {
      this.showModal = false; // Ẩn modal
      this.selectedImage = ''; // Reset hình ảnh
    },
    closeProfileModal() {
      this.showProfileModal = false; // Ẩn modal thông tin người dùng
      this.userProfile = {}; // Reset thông tin người dùng
    },
    async fetchUserProfile(userId) {
      try {
        const response = await api.get(`/api/v1/admin/profile/${userId}`);
        this.userProfile = response.data; // Lưu thông tin người dùng
        this.showProfileModal = true; // Hiển thị modal thông tin người dùng
      } catch (error) {
        console.error('Error fetching user profile:', error);
      }
    },
    formatDate(timestamp) {
      const date = new Date(timestamp);
      return date.toLocaleString('vi-VN'); // Định dạng ngày giờ theo ngôn ngữ Việt
    },
    searchSellers() {
      if (this.searchQuery) {
        this.sellers = this.sellers.filter(seller =>
          seller.seller_id.toString().includes(this.searchQuery)
        );
      } else {
        this.fetchSellers(); // Nếu không có tìm kiếm, tải lại danh sách
      }
      this.currentPage = 1; // Đặt lại trang hiện tại về 1
      this.updatePaginatedSellers();
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
        this.updatePaginatedSellers();
      }
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
        this.updatePaginatedSellers();
      }
    },
  },
  computed: {
    totalPages() {
      return Math.ceil(this.sellers.length / this.itemsPerPage);
    },
  },
};
</script>

<style scoped>
/* Thêm bất kỳ kiểu nào ở đây */
</style>