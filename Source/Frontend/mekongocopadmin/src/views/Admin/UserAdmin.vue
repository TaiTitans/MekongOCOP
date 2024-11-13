<template>
  <NavbarS title="Người dùng" />
  <div class="flex flex-col h-screen bg-gray-100">
    <div class="h-16"></div>
    <div class="flex-1 overflow-hidden relative pt-6 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto w-full mb-10 justify-center items-center">
      <div class="bg-white shadow-xl rounded-lg overflow-hidden w-full mx-auto flex flex-col h-full justify-center">
        <div class="p-10 flex-shrink-0">
          <div class="sm:flex sm:items-center">
            <div class="sm:flex-auto">
              <h1 class="text-2xl font-semibold text-gray-900">Người dùng</h1>
              <p class="mt-2 text-sm text-gray-700">Danh sách tất cả người dùng trong tài khoản bao gồm tên, chức danh, email và vai trò của họ.</p>
            </div>
            <div class="mt-4 sm:mt-0 sm:ml-16 sm:flex-none w-full sm:w-64">
              <div class="relative rounded-md shadow-sm">
                <input
                  type="text"
                  v-model="searchQuery"
                  placeholder="Tìm kiếm ..."
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
        </div>

        <div class="flex-1 overflow-y-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th v-for="header in headers" :key="header.value" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  {{ header.text }}
                </th>
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
          <div v-if="loading" class="loader">Loading...</div>
        </div>

        <div class="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6 flex-shrink-0">
          <div class="flex-1 flex justify-between sm:hidden">
            <button @click="prevPage" :disabled="currentPage === 1" aria-label="Previous page" class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50">
              Previous
            </button>
            <button @click="nextPage" :disabled="currentPage === totalPages" aria-label="Next page" class="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50">
              Next
            </button>
          </div>
          <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
            <div>
              <p class="text-sm text-gray-700">
                Showing
                <span class="font-medium">{{ (currentPage - 1) * itemsPerPage + 1 }}</span>
                to
                <span class="font-medium">{{ Math.min(currentPage * itemsPerPage, users.length) }}</span>
                of
                <span class="font-medium">{{ users.length }}</span>
                results
              </p>
            </div>
            <div>
  <nav class="relative z-0 inline-flex -space-x-px rounded-md shadow-sm" aria-label="Pagination">
    <button @click="prevPage" :disabled="currentPage === 1" aria-label="Previous page" class="relative inline-flex items-center px-2 py-2 border border-gray-300 rounded-l-md text-sm font-medium text-gray-500 bg-white hover:bg-gray-50">
      <span class="sr-only">Previous</span>
      <svg class="h-5 w-5" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
        <path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd" />
      </svg>
    </button>
    <button @click="nextPage" :disabled="currentPage === totalPages" aria-label="Next page" class="relative inline-flex items-center px-2 py-2 border border-gray-300 rounded-r-md text-sm font-medium text-gray-500 bg-white hover:bg-gray-50">
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
import NavbarS from '../../components/NavbarS.vue';
import api from '../../services/api.service'; // Adjust the import based on your file structure

export default {
  components: { NavbarS },
  data() {
    return {
      users: [],
      filteredUsers: [],
      searchQuery: '',
      currentPage: 1,
      itemsPerPage: 50,
      totalUsers: 0,
      errorMessage: '',
      loading: false,
      headers: [
        { text: 'User ID', value: 'user_id' },
        { text: 'Username', value: 'username' },
        { text: 'Email', value: 'email' },
        { text: 'Roles', value: 'roles' },
      ],
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.filteredUsers.length / this.itemsPerPage);
    },
    paginatedUsers() {
      if (!this.filteredUsers) {
        return []; // Return an empty array if filteredUsers is not defined
      }
      const start = (this.currentPage - 1) * this.itemsPerPage;
      return this.filteredUsers.slice(start, start + this.itemsPerPage);
    },
  },
  methods: {
    async fetchUsers() {
      this.loading = true;
      try {
        const response = await api.get(`/api/v1/admin/users?page=${this.currentPage}`);
        console.log('API Response:', response.data); // Log the API response
        this.users = response.data || []; // Ensure it's always an array
        this.filteredUsers = [...this.users]; // Clone the array for filtering
        this.totalUsers = this.users.length; // Update total users count
      } catch (error) {
        console.error('Error fetching users:', error);
        this.errorMessage = 'Unable to load users. Please try again later.';
      } finally {
        this.loading = false;
      }
    },
    filterUsers() {
      const query = this.searchQuery.toLowerCase();
      this.filteredUsers = this.users.filter(user =>
        user.username.toLowerCase().includes(query) || 
        user.email.toLowerCase().includes(query)
      );
      this.currentPage = 1; // Reset to first page after filtering
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
        this.fetchUsers(); // Fetch previous page data
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
        this.fetchUsers(); // Fetch next page data
      }
    },
  },
  mounted() {
    this.fetchUsers(); // Fetch users when the component mounts
  },
};
</script>

<style scoped>
.loader {
  text-align: center;
  padding: 20px;
  font-size: 1.5em;
  color: #555;
}
</style>