<template>
          <NavbarS title="Người dùng" />
    <div class="flex flex-col h-screen bg-gray-100">
      <!-- Navbar -->

  
      <!-- Spacer for Navbar -->
      <div class="h-16"></div>
  
      <!-- Main Content Container -->
      <div class="flex-1 overflow-hidden relative pt-6 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto w-full">
        <!-- User Table Container -->
        <div class="bg-white shadow-xl rounded-lg overflow-hidden w-full mx-auto">
          <div class="p-10">
            <div class="sm:flex sm:items-center">
              <div class="sm:flex-auto">
                <h1 class="text-2xl font-semibold text-gray-900">Users</h1>
                <p class="mt-2 text-sm text-gray-700">Danh sách tất cả người dùng trong tài khoản bao gồm tên, chức danh, email và vai trò của họ.</p>
              </div>
              <div class="mt-4 sm:mt-0 sm:ml-16 sm:flex-none w-full sm:w-64">
                <div class="relative rounded-md shadow-sm">
                  <input
                    type="text"
                    v-model="searchQuery"
                    placeholder="Tìm kiếm ...   "
                    @input="filterUsers"
                    class="block w-full pr-10 sm:text-sm border-gray-300 rounded-md focus:ring-indigo-500 focus:border-indigo-500 h-8 ml-2"
                  />
                  <div class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                      <path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd" />
                    </svg>
                  </div>
                </div>
              </div>
            </div>
  
            <div class="mt-8 overflow-x-auto w-full">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">User ID</th>
                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Username</th>
                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Roles</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="user in paginatedUsers" :key="user.user_id" class="hover:bg-gray-50 transition duration-150 ease-in-out">
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ user.user_id }}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ user.username }}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ user.email }}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      <span v-for="role in user.roles" :key="role" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800 mr-1">
                        {{ role }}
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
  
          <!-- Pagination Controls -->
          <div class="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6">
            <div class="flex-1 flex justify-between sm:hidden">
              <button @click="prevPage" :disabled="currentPage === 1" class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50">
                Previous
              </button>
              <button @click="nextPage" :disabled="currentPage === totalPages" class="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50">
                Next
              </button>
            </div>
            <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
              <div>
                <p class="text-sm text-gray-700">
                  Showing
                  <span class="font-medium">{{ (currentPage - 1) * itemsPerPage + 1 }}</span>
                  to
                  <span class="font-medium">{{ Math.min(currentPage * itemsPerPage, filteredUsers.length) }}</span>
                  of
                  <span class="font-medium">{{ filteredUsers.length }}</span>
                  results
                </p>
              </div>
              <div>
                <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
                  <button @click="prevPage" :disabled="currentPage === 1" class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                    <span class="sr-only">Previous</span>
                    <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                      <path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd" />
                    </svg>
                  </button>
                  <button @click="nextPage" :disabled="currentPage === totalPages" class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                    <span class="sr-only">Next</span>
                    <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                      <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                    </svg>
                  </button>
                </nav>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  
  <script>
  import api from '../../services/api.service';
  import NavbarS from '../../components/NavbarS.vue';
  export default {
  name: 'UserAdmin',
  data() {
    return {
      users: [], // Mảng chứa thông tin người dùng
      searchQuery: '', // Trường tìm kiếm
      filteredUsers: [],// Mảng chứa người dùng đã lọc
      currentPage: 1,
      itemsPerPage: 10, //
    };
  },
    components: {
        NavbarS,
    },
  mounted() {
    this.fetchUsers(); // Gọi hàm để lấy dữ liệu người dùng khi component được gắn
  },
  computed:{
    paginatedUsers() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      return this.filteredUsers.slice(start, start + this.itemsPerPage);
    },
    totalPages() {
      return Math.ceil(this.filteredUsers.length / this.itemsPerPage);
    },
  },
  methods: {
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
    async fetchUsers() {
      try {
        const response = await api.get('/api/v1/admin/users'); // Gọi API để lấy dữ liệu
        this.users = response.data; // Lưu dữ liệu vào users
        this.filteredUsers = this.users; // Khởi tạo filteredUsers
      } catch (error) {
        console.error('Error fetching users:', error);
      }
    },
    filterUsers() {
      const query = this.searchQuery.toLowerCase(); // Chuyển đổi truy vấn thành chữ thường
      this.filteredUsers = this.users.filter(user => {
        return (
          user.username.toLowerCase().includes(query) || // Lọc theo tên người dùng
          user.email.toLowerCase().includes(query) || // Lọc theo email
          user.user_id.toString().includes(query) // Lọc theo User ID
        );
      });
    },
    fetchData() {
      this.fetchUsers(); // Gọi lại hàm lấy người dùng
    }
  }
};
  </script>
  
  <style>
  .fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}
  </style>
  