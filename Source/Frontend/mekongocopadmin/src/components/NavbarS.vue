<template>
    <nav class="fixed top-0 left-0 right-0 bg-white border-b border-gray-200 shadow-md">
      <div class="mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <div class="flex-shrink-0 flex items-center">
            <img class="h-8 w-auto" src="../assets//mekongocop_logo.png" alt="Logo">
            <span class="ml-3 font-semibold text-xl text-gray-800">Mekong OCOP</span>
          </div>
          <div class="flex-1 flex justify-center">
            <span class="font-semibold text-xl text-indigo-600">{{ title }}</span>
          </div>
          <div class="flex items-center">
            <button
              class="p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-indigo-500"
              @click="fetchUserProfile"
            >
              <span class="sr-only">Refresh</span>
              <svg class="h-6 w-6" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
            </button>
            <div class="ml-3 relative">
              <div>
                <button
                  type="button"
                  class="bg-white rounded-full flex text-sm focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                  id="user-menu-button"
                  aria-expanded="false"
                  aria-haspopup="true"
                >
                  <span class="sr-only">Open user menu</span>
                  <img class="h-8 w-8 rounded-full" :src="userProfileImage" alt="Avatar">
                </button>
              </div>
            </div>
            <span class="ml-2 text-gray-800">{{ fullName }}</span>
          </div>
        </div>
      </div>
    </nav>
  </template>
  
  <script>
  import api from '../services/api.service';
  
  export default {
    name: 'NavbarS',
    props: {
    title: {
      type: String,
      default: 'Dashboard', 
    },
  },
    data() {
      return {
        fullName: '',
        userProfileImage: '',
      };
    },
    methods: {
      async fetchUserProfile() {
        try {
          const response = await api.get('/api/v1/user/profile');
          if (response.data.status === 'Success') {
            this.fullName = response.data.data.full_name;
            this.userProfileImage = response.data.data.user_profile_image;
          }
        } catch (error) {
          console.error('Error fetching user profile:', error);
        }
      },
    },
    mounted() {
      this.fetchUserProfile(); // Gọi API khi component được mount
    },
  };
  </script>
  
  <style scoped>
  /* Thêm các style cần thiết cho navbar ở đây */
  </style>
  