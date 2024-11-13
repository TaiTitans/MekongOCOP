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
    <div class="md:w-1/2 bg-white flex justify-center items-start mt-12 md:mt-20 relative z-99">
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
            <button 
              type="button" 
              @click="togglePassword" 
              class="ml-2 text-gray-500 hover:text-gray-700 focus:outline-none"
            >
              <svg v-if="showPassword" class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <!-- Eye open icon -->
                <path d="M12 5c-7 0-11 7-11 7s4 7 11 7 11-7 11-7-4-7-11-7zm0 12c-2.757 0-5-2.243-5-5s2.243-5 5-5 5 2.243 5 5-2.243 5-5 5z"/>
              </svg>
              <svg v-else class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <!-- Eye closed icon -->
                <path d="M12 5c-7 0-11 7-11 7s4 7 11 7c1.534 0 2.997-.256 4.354-.706l1.508 1.508 1.414-1.414-1.508-1.508c.846-1.099 1.548-2.296 2.028-3.562-.541-.707-1.133-1.355-1.756-1.94l1.28-1.28-1.414-1.414-1.28 1.28c-1.096-.896-2.348-1.618-3.742-2.116-.722-.205-1.479-.35-2.274-.35zm0 4c-2.757 0-5 2.243-5 5 0 .357.041.704.118 1.042l1.532-1.532a3 3 0 0 1 0-1.01c0-.382.045-.751.128-1.108l1.665 1.665a3 3 0 0 1 0 1.01c-.072.351-.118.714-.118 1.065 0 2.757 2.243 5 5 5 .35 0 .714-.046 1.066-.118l1.438 1.438c-.307.07-.618.118-.938.118-2.757 0-5-2.243-5-5 0-.35.046-.714.118-1.065l1.666-1.666a3.001 3.001 0 0 1 0 1.011c.072.353.118.717.118 1.066 0 2.757 2.243 5 5 5 .32 0 .631-.048.938-.118l1.438 1.438c.031-.065.065-.129.097-.197z"/>
              </svg>
            </button>
          </div>
        </div>
        <div class="flex flex-col space-y-2 mt-2">
            <div class="flex items-center justify-between">
              <canvas ref="captchaCanvas" width="150" height="50" class="border rounded"></canvas>
              <button type="button" @click="generateCaptcha" class="p-2 text-gray-500 hover:text-gray-700">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
              </button>
            </div>
            <div class="flex items-center border-2 py-2 px-3 rounded-md">
              <input 
                class="pl-2 outline-none border-none w-full" 
                type="text" 
                v-model="captchaInput"
                placeholder="Nhập mã CAPTCHA" 
                required
              >
            </div>
          </div>
        <div class="flex justify-between items-center mt-4">
          <label class="inline-flex items-center text-gray-700 font-medium text-xs">
            <input type="checkbox" v-model="rememberMe" class="mr-2">
            <span class="text-xs font-semibold">Ghi nhớ?</span>
          </label>
          <button type="submit" class="shadow-xl bg-cyan-500 hover:bg-gray-300 text-white py-2 px-4 rounded-md text-sm tracking-wide transition duration-1000">
            Đăng nhập
          </button>
        </div>
      </form>

      <div class="absolute bottom-0 w-full md:w-auto sm:display: none mt-2">
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
      refreshToken: null,
      captchaText: "",
      captchaInput: "",
      showPassword: false,
      rememberMe: false,
    };
  },
  mounted() {
    this.generateCaptcha();
  },
  created(){
    this.accessToken = authService.getAccessToken();
    this.refreshToken = authService.getRefreshToken();
  },
  methods: {
    togglePassword() {
      this.showPassword = !this.showPassword;
    },
    generateCaptcha() {
      const canvas = this.$refs.captchaCanvas;
      const ctx = canvas.getContext('2d');
      
      // Clear canvas
      ctx.fillStyle = '#f3f4f6';
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      
      // Generate random text
      this.captchaText = Math.random().toString(36).substring(2, 8).toUpperCase();
      
      // Draw text
      ctx.font = 'bold 24px Arial';
      ctx.fillStyle = '#374151';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      
      // Add noise
      for (let i = 0; i < 50; i++) {
        ctx.fillStyle = '#' + Math.floor(Math.random()*16777215).toString(16);
        ctx.fillRect(Math.random() * canvas.width, Math.random() * canvas.height, 3, 3);
      }
      
      // Draw distorted text
      let x = canvas.width/6;
      for(let i = 0; i < this.captchaText.length; i++) {
        const char = this.captchaText[i];
        const rotate = (Math.random() - 0.5) * 0.4;
        ctx.save();
        ctx.translate(x, canvas.height/2);
        ctx.rotate(rotate);
        ctx.fillText(char, 0, 0);
        ctx.restore();
        x += ctx.measureText(char).width + 5;
      }
    },
    dangnhap() {
      if (!this.username || !this.password) {
        this.errorMessage = "Vui lòng nhập tên đăng nhập và mật khẩu";
        return;
      }

      if (this.captchaInput.toUpperCase() !== this.captchaText) {
        this.errorMessage = "Mã CAPTCHA không đúng";
        this.generateCaptcha();
        this.captchaInput = "";
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
              this.$router.push({ path: '/admin' });
            } else if (userRoles.includes('ROLE_SELLER')) {
              this.$router.push({ path: '/seller' });
            } else {
              this.errorMessage = "Tài khoản không có quyền truy cập!";
            }
          }, 100);
        })
        .catch((error) => {
          console.error(error);
          this.errorMessage = "Tên đăng nhập hoặc mật khẩu sai!";
          this.generateCaptcha();
          this.captchaInput = "";
        });
    },
  },
};
</script>

  <style></style>