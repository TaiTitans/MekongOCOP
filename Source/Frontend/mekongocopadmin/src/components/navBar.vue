<template>

    <div>
    
        <div class="flex bg-gray-100 h-[100%]">
    
    <!-- sidebar -->
    <div class="hidden md:flex flex-col w-64 bg-gray-800">
        <div class="flex items-center justify-center h-16 bg-gray-900">
            <span class="text-white font-bold uppercase">MekongTravel Admin</span>
        </div>
        <div class="flex flex-col flex-1 overflow-y-auto">
            <nav class="flex-1 px-2 py-4 bg-gray-800">
                <div  class="flex items-center px-4 py-2 text-gray-100 hover:bg-gray-700" @click="goToTinhThanh">
                    <svg class="w-6 h-6 text-yellow-600 mr-2 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 13a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z"/>
  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.8 13.938h-.011a7 7 0 1 0-11.464.144h-.016l.14.171c.1.127.2.251.3.371L12 21l5.13-6.248c.194-.209.374-.429.54-.659l.13-.155Z"/>
</svg>

    
                    Tỉnh Thành
    </div>
                <div @click="goToDiaDiem" class="flex items-center px-4 py-2 mt-2 text-gray-100 hover:bg-gray-700">
                    <svg class="w-6 h-6 text-yellow-600 mr-2 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m4 12 8-8 8 8M6 10.5V19a1 1 0 0 0 1 1h3v-3a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v3h3a1 1 0 0 0 1-1v-8.5"/>
</svg>

    
                    Địa Điểm
    </div>
                <div @click="goToAmThuc" class="flex items-center px-4 py-2 mt-2 text-gray-100 hover:bg-gray-700">
                    <svg class="w-6 h-6 text-yellow-600 mr-2 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18Z"/>
  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 13c0 2.038-2.239 4.5-5 4.5S7 15.038 7 13c0 1.444 10 1.444 10 0Z"/>
  <path fill="currentColor" d="m9 6.811.618 1.253 1.382.2-1 .975.236 1.377L9 9.966l-1.236.65L8 9.239l-1-.975 1.382-.2L9 6.811Zm6 0 .618 1.253 1.382.2-1 .975.236 1.377L15 9.966l-1.236.65L14 9.239l-1-.975 1.382-.2L15 6.811Z"/>
  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" d="m9 6.811.618 1.253 1.382.2-1 .975.236 1.377L9 9.966l-1.236.65L8 9.239l-1-.975 1.382-.2L9 6.811Zm6 0 .618 1.253 1.382.2-1 .975.236 1.377L15 9.966l-1.236.65L14 9.239l-1-.975 1.382-.2L15 6.811Z"/>
</svg>

    
                   Ẩm Thực
    </div>
    <div @click="goToFeedback" class="flex items-center px-4 py-2 mt-2 text-gray-100 hover:bg-gray-700">
        <svg class="w-6 h-6 text-yellow-600 mr-2 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
  <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 11v5m0 0 2-2m-2 2-2-2M3 6v1a1 1 0 0 0 1 1h16a1 1 0 0 0 1-1V6a1 1 0 0 0-1-1H4a1 1 0 0 0-1 1Zm2 2v10a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1V8H5Z"/>
</svg>

    
    
                    Ý Kiến
    </div>
                <div  @click="logout" class="flex items-center px-4 py-2 mt-2 text-gray-100 hover:bg-gray-700">
                    <svg class="w-6 h-6 text-blue-600 mr-2 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
      <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 12H4m12 0-4 4m4-4-4-4m3-4h2a3 3 0 0 1 3 3v10a3 3 0 0 1-3 3h-2"/>
    </svg>
    
                   Đăng Xuất
    </div>
            </nav>
        </div>
    </div>
    
    </div>
    
    
      
      </div>
    
    </template>
    
    <script>
    import router from "../router/index";
    export default {
        name:'NavBar',
        data() {
        return {
          username: localStorage.getItem("username"),
          isLoggedIn : localStorage.getItem("isAuthenticated")
        };
      },
        methods:{
            goToTinhThanh() {
          this.$router.push({ name: 'TinhThanh' });
        },
        goToDiaDiem() {
          this.$router.push({ name: 'DiaDiem' });
        },
        goToAmThuc() {
          this.$router.push({ name: 'AmThuc' });
        },
        goToFeedback() {
          this.$router.push({ name: 'Feedback' });
        },
    
        dangXuat() {
          console.log("logout");
          // localStorage.setItem("isAuthenticated", false);
          localStorage.removeItem("username");
          localStorage.removeItem("isAuthenticated");
          document.cookie =
            "accessToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
          // this.checkAuthentication();
          router.push('/login');
        },
        logout() {
    // Xóa cookie chứa access token và refresh token
    document.cookie = "accessToken=; path=/; max-age=0; secure;";
    document.cookie = "refreshToken=; path=/; max-age=0; secure;";
    
    // Cập nhật trạng thái ứng dụng (nếu cần thiết)
    // Ví dụ: gọi một hàm để cập nhật trạng thái người dùng hoặc chuyển hướng
    console.log("Đã đăng xuất thành công.");

    // Chuyển hướng về trang đăng nhập hoặc trang khác
    router.push('/login'); // Thay đổi đường dẫn nếu cần
}
    
    
      },
      
        }
    
    </script>
    
    <style>
    
    </style>