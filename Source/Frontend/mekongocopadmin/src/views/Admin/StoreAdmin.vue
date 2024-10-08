<template>
    <div>
      <div class="flex flex-col min-h-screen mx-auto bg-gray-100">
        <NavbarS title="Cửa hàng" />
        <div class="container mx-auto px-12 py-6">
          <!-- Title -->
          <div class="mb-6 mt-12">
            <h1 class="text-3xl font-semibold text-gray-900">Danh sách cửa hàng</h1>
            <p class="text-gray-700">Quản lý các cửa hàng trong hệ thống</p>
          </div>
    
          <!-- Search Bar -->
          <div class="mb-6 flex justify-center">
            <div class="relative w-full max-w-md">
              <input
                type="text"
                v-model="searchQuery"
                @input="searchStores"
                placeholder="       Tìm kiếm theo tên hoặc ID cửa hàng..."
                class="w-full px-4 py-2 pr-10 border rounded-lg focus:outline-none focus:ring focus:ring-cyan-300"
              />
              <span class="absolute inset-y-0 left-0 flex items-center pl-3">
                <i class="fas fa-search text-gray-400"></i>
              </span>
            </div>
          </div>
    
          <!-- Store List -->
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div v-for="store in paginatedStores" :key="store.store_id" class="bg-white shadow-md rounded-lg overflow-hidden">
              <!-- Store Banner -->
              <img :src="store.store_banner" alt="Store Banner" class="w-full h-28 object-cover">
                
              <!-- Store Logo with dynamic border based on status -->
              <div class="flex justify-center items-center p-4">
                <img :src="store.store_logo" alt="Store Logo" 
                     :class="store.status === 'Banded' ? 'border-red-500' : 'border-green-300'"
                     class="w-24 h-24 rounded-full object-cover border-4 shadow-lg">
              </div>
    
              <h2 class="text-xl font-semibold text-cyan-600 text-center">{{ store.store_name }}</h2>
                
              <!-- Store Info -->
              <div class="p-4">
                <!-- Address with icon -->
                <p class="text-gray-700">
                  <i class="fas fa-map-marker-alt mr-2"></i> {{ store.store_address }}
                </p>
                <!-- Description with icon -->
                <p class="mt-2 text-gray-500">
                  <i class="fas fa-info-circle mr-2"></i> {{ store.store_description }}
                </p>
              </div>
    
              <!-- Store Status -->
              <div class="p-4 border-t border-gray-200 flex justify-between items-center">
                <span class="px-2 py-1 inline-block rounded-full text-sm font-semibold" 
                      :class="store.status === 'Active' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
                  {{ store.status }}
                </span>
                <!-- Banded Button -->
                <button @click="toggleStoreStatus(store)" 
                        class="ml-4 px-2 py-1 bg-gray-300 text-black rounded-md hover:bg-red-600">
                  {{ store.status === 'Active' ? 'Banded' : 'Active' }}
                </button>
              </div>
            </div>
          </div>
    
          <!-- Pagination -->
          <div class="mt-6 flex justify-between items-center">
            <button @click="prevPage" :disabled="currentPage === 1" class="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400 disabled:opacity-50">
              Trước
            </button>
            <span class="text-gray-700">Trang {{ currentPage }} / {{ totalPages }}</span>
            <button @click="nextPage" :disabled="currentPage === totalPages" class="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400 disabled:opacity-50">
              Sau
            </button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import api from '../../services/api.service';
  import NavbarS from '../../components/NavbarS.vue';
  
  export default {
    name: 'StoreAdmin',
    components: {
      NavbarS,
    },
    data() {
      return {
        stores: [],
        filteredStores: [], // Danh sách cửa hàng đã lọc
        currentPage: 1,
        itemsPerPage: 6, // Số lượng cửa hàng mỗi trang
        totalPages: 0,
        searchQuery: '', // Giá trị tìm kiếm
      };
    },
    computed: {
      // Lấy danh sách cửa hàng theo trang hiện tại
      paginatedStores() {
        const start = (this.currentPage - 1) * this.itemsPerPage;
        const end = start + this.itemsPerPage;
        return this.filteredStores.slice(start, end); // Sử dụng danh sách đã lọc
      },
    },
    methods: {
      // Fetch dữ liệu từ API
      async fetchStores() {
        try {
          const response = await api.get('/api/v1/admin/stores');
          this.stores = response.data;
          this.filteredStores = this.stores; // Khởi tạo danh sách đã lọc
          this.totalPages = Math.ceil(this.filteredStores.length / this.itemsPerPage); // Tính tổng số trang
        } catch (error) {
          console.error("Error fetching stores:", error);
        }
      },
      // Tìm kiếm cửa hàng
      searchStores() {
        const query = this.searchQuery.toLowerCase();
        this.filteredStores = this.stores.filter(store => 
          store.store_name.toLowerCase().includes(query) || 
          store.store_id.toString().includes(query) // Tìm theo ID
        );
        this.currentPage = 1; // Reset về trang đầu tiên
        this.totalPages = Math.ceil(this.filteredStores.length / this.itemsPerPage); // Cập nhật tổng số trang
      },
      // Chuyển đến trang trước
      prevPage() {
        if (this.currentPage > 1) {
          this.currentPage--;
        }
      },
      // Chuyển đến trang sau
      nextPage() {
        if (this.currentPage < this.totalPages) {
          this.currentPage++;
        }
      },
      // Gọi API để toggle trạng thái cửa hàng
      async toggleStoreStatus(store) {
        const newStatus = store.status === 'Active' ? 'Banded' : 'Active'; // Xác định trạng thái mới
        try {
          await api.patch(`/api/v1/admin/store/${store.store_id}/status`, { status: newStatus });
          // Reload danh sách cửa hàng sau khi cập nhật
          await this.fetchStores();
        } catch (error) {
          console.error("Error updating store status:", error);
        }
      },
    },
    // Fetch dữ liệu khi component được mount
    mounted() {
      this.fetchStores();
    },
  };
  </script>
  
  <style scoped>
  /* Tùy chỉnh CSS nếu cần */
  </style>
  