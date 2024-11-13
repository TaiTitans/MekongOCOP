<template>
    <div class="flex flex-col min-h-screen bg-gray-100">
      <NavbarS title="Sự kiện" />
      <div class="flex-1 container mx-auto px-4 py-8 mt-20 ml-20">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <!-- Special Days Section -->
          <div class="bg-white shadow-lg rounded-lg p-6">
            <h2 class="text-2xl font-semibold mb-4 text-gray-800">Ngày Đặc Biệt</h2>
            <div class="flex flex-col sm:flex-row items-center mb-4 space-y-2 sm:space-y-0 sm:space-x-2">
              <input
                type="date"
                v-model="newSpecialDay"
                class="border border-gray-300 rounded-md p-2 w-full sm:w-auto focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <button @click="addSpecialDay" class="bg-cyan-500 text-white shadow-md px-4 py-2 rounded-md hover:bg-gray-300 hover:text-white transition duration-300 flex items-center justify-center w-full sm:w-auto">
                <svg class="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                </svg>
                Thêm Ngày Đặc Biệt
              </button>
            </div>
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Ngày Đặc Biệt</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hành Động</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="day in specialDays" :key="day.id" class="hover:bg-gray-50 transition duration-150 ease-in-out">
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ day.id }}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ formatDate(day.special_day) }}</td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <button @click="deleteSpecialDay(day.id)" class="text-red-600 hover:text-red-900 transition duration-300 flex items-center">
                        <svg class="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                        </svg>
                        Xóa
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
  
          <!-- Send Notification Section -->
          <div class="bg-white shadow-lg rounded-lg p-6">
            <h2 class="text-2xl font-semibold mb-4 text-gray-800">Gửi Thông Báo</h2>
            <div class="space-y-4">
              <input
                type="text"
                v-model="notificationMessage"
                placeholder="Nhập thông điệp thông báo"
                class="border border-gray-300 rounded-md p-2 w-full focus:outline-none focus:ring-2 focus:ring-green-500"
              />
              <button @click="sendNotification" class="bg-cyan-500 text-white px-4 py-2 rounded-md hover:bg-gray-300 hover:text-white transition duration-300 flex items-center justify-center w-full shadow-md">
                <svg class="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"></path>
                </svg>
                Gửi Thông Báo
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import NavbarS from '../../components/NavbarS.vue';
  import api from '../../services/api.service';
  
  export default {
    name: 'EventAdmin',
    components: {
      NavbarS,
    },
    data() {
      return {
        specialDays: [],
        newSpecialDay: '',
        notificationMessage: '',
      };
    },
    methods: {
      async fetchSpecialDays() {
        try {
          const response = await api.get('/api/v1/admin/specialday');
          this.specialDays = response.data;
        } catch (error) {
          console.error('Error fetching special days:', error);
        }
      },
      async addSpecialDay() {
        if (!this.newSpecialDay) return;
        try {
          await api.post('/api/v1/admin/specialday', this.newSpecialDay);
          this.newSpecialDay = '';
          this.fetchSpecialDays();
        } catch (error) {
          console.error('Error adding special day:', error);
        }
      },
      async deleteSpecialDay(id) {
        try {
          await api.delete(`/api/v1/admin/specialday/${id}`);
          this.fetchSpecialDays();
        } catch (error) {
          console.error('Error deleting special day:', error);
        }
      },
      async sendNotification() {
        if (!this.notificationMessage) return;
        try {
          await api.post(`/api/v1/admin/notification/send?message=${this.notificationMessage}`);
          this.notificationMessage = '';
        } catch (error) {
          console.error('Error sending notification:', error);
        }
      },
      formatDate(specialDayArray) {
        const [year, month, day] = specialDayArray;
        return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
      },
    },
    mounted() {
      this.fetchSpecialDays();
    },
  };
  </script>
  
  <style scoped>
  /* Add any additional styling here */
  </style>