<template>
  <nav class="bg-gray-100 shadow-md">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between h-16">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <img class="h-8 w-auto" src="@/assets/mekongocop_logo.png" alt="Your Company">
          </div>
          <div class="ml-4 text-gray-600 font-semibold">Mekong OCOP - Manager</div>
        </div>
      </div>
    </div>
  </nav>

  <div class="flex flex-col md:flex-row h-screen ml-8 mt-6 mr-8 mb-6 shadow-2xl rounded-lg">
    <!-- Left side with image -->
    <div class="md:w-1/2 flex justify-center items-center shadow-l border-r border-gray-300">
      <img src="@/assets/img_login.png" alt="Login Image" class="object-contain w-[80%] h-[80%] md:w-[80%] md:h-[80%]">
    </div>

    <!-- Right side with login form -->
    <div class="md:w-1/2 bg-white flex justify-center items-start mt-20 md:mt-20 relative z-10">
      <form @submit.prevent="dangnhap" class="w-3/4 max-w-md">
        <div class="space-y-4">
          <h1 class="text-2xl font-semibold text-gray-600 text-center">Đăng nhập</h1>
          <div v-if="errorMessage" class="text-red-600 font-semibold">{{ errorMessage }}</div>
          <div v-if="successMessage" class="text-green-600 font-semibold">{{ successMessage }}</div>
          <hr>

          <div class="shadow-inner bg-gray-100 rounded-lg p-4">
            <span class="block font-semibold">Ví dụ:</span>
            <span class="block">Seller: seller - ******</span>
            <span class="block">Admin: admin - ******</span>
          </div>
          <div class="flex items-center border-2 py-2 px-3 rounded-md">
            <svg class="w-6 h-6 text-gray-400" aria-hidden="true" width="24" height="24" fill="none" viewBox="0 0 24 24">
              <path stroke="currentColor" stroke-width="2" d="M7 17v1a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1v-1a3 3 0 0 0-3-3h-4a3 3 0 0 0-3 3Zm8-9a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"/>
            </svg>
            <input class="pl-2 outline-none border-none w-full" type="name" name="name" v-model="username" placeholder="Tên đăng nhập" required>
          </div>

          <div class="flex items-center border-2 py-2 px-3 rounded-md">
            <svg class="h-5 w-5 text-gray-400" viewBox="0 0 20 20" fill="currentColor">
              <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd"/>
            </svg>
            <input class="pl-2 outline-none border-none w-full" type="password" name="password" v-model="password" placeholder="Mật khẩu" required>
          </div>
        </div>

        <div class="flex justify-between items-center mt-4">
          <div class="inline-flex items-center text-gray-700 font-medium text-xs">
            <input type="checkbox" id="rememberMeCheckbox" name="rememberMe" class="mr-2">
            <span class="text-xs font-semibold">Ghi nhớ?</span>
          </div>
          <button type="submit" class="shadow-xl bg-indigo-500 hover:bg-red-700 text-white py-2 px-4 rounded-md text-sm tracking-wide transition duration-1000">
            Đăng nhập
          </button>
        </div>
      </form>

      <div class="absolute bottom-0 w-full md:w-auto sm:display: none">
        <img src="@/assets/banner.png" alt="Doodle Image" class="w-full h-full md:w-full md:h-full">
      </div>
    </div>
  </div>
</template>
  
  <script>
  import api from '../services/api.service';
  import router from "../router/index";
import authService from '@/services/auth.service';

import axios from 'axios';
  export default {
    data() {
      return {
        username: "",
        password: "",
        errorMessage: null,
        successMessage: null,
        accessToken: null,
        refreshToken: null
      };
    },
    created(){
      this.accessToken = authService.getAccessToken();
      this.refreshToken = authService.getRefreshToken();
    },
    methods: {
      dangnhap() {
  if (!this.username || !this.password) {
    this.errorMessage = "Vui lòng nhập tên đăng nhập và mật khẩu";
    return;
  }
  
  const formData = {
    username: this.username,
    password: this.password,
  };

  api.post("/api/v1/login", formData)
    .then((response) => {
      console.log("Login response:", response.data);
      console.log("All cookies:", document.cookie);
    console.log("Access token:", authService.getAccessToken());
      
      setTimeout(() => {
        const userRoles = authService.getUserRoles();
        console.log("User roles:", userRoles);
        if (userRoles.includes('ROLE_ADMIN')) {
          this.$router.push({ name: 'AdminDashboard' });
        } else if (userRoles.includes('ROLE_SELLER')) {
          this.$router.push({ name: 'SellerDashboard' });
        } else {
          this.errorMessage = "Tài khoản không có quyền truy cập!";
        }
      }, 100);
    })
    .catch((error) => {
      console.error(error);
      this.errorMessage = "Tên đăng nhập hoặc mật khẩu sai!";
    });
},
    },
  };
  </script>
  
  <style></style>