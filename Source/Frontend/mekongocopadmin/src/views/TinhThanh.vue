<template>
    <div class="w-[100%] h-screen">
      <div class="flex flex-col h-full">
        <div
          class="flex items-center justify-between h-16 bg-white border-b border-gray-200 px-4"
        >
          <div class="flex items-center">
            <input
              class="mx-4 w-full border rounded-md px-4 py-2"
              type="text"
              placeholder="Tìm kiếm tỉnh thành"
              v-model="searchTerm"
            />
          </div>
          <div class="font-bold text-lg">Danh Sách Tỉnh Thành</div>
          <div class="flex items-center">
            <button
              class="flex items-center text-gray-500 hover:text-gray-700 focus:outline-none focus:text-gray-700"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-6 w-6"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 19l-7-7 7-7m5 14l7-7-7-7"
                />
              </svg>
            </button>
          </div>
        </div>
  
        <div class="flex-1 overflow-y-auto p-4">
          <div
            v-if="filteredProvinces.length > 0"
            class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4"
          >
            <div
              v-for="province in filteredProvinces"
              :key="province.maTinh"
              class="bg-white rounded-lg shadow-md p-4"
            >
              <h3 class="font-bold text-lg">{{ province.tenTinhThanh }}</h3>
              <p class="text-gray-600">Mã tỉnh: {{ province.maTinh }}</p>
            </div>
          </div>
          <div v-else class="text-center text-gray-500 py-8">
            Không có dữ liệu tỉnh thành để hiển thị.
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        provinces: [
          { tenTinhThanh: 'Cần Thơ', maTinh: 'CT' },
          { tenTinhThanh: 'Hậu Giang', maTinh: 'HG' },
          { tenTinhThanh: 'Trà Vinh', maTinh: 'TV' },
          { tenTinhThanh: 'Cà Mau', maTinh: 'CM' },
          { tenTinhThanh: 'Bạc Liêu', maTinh: 'BL' },
          { tenTinhThanh: 'Long An', maTinh: 'LA' },
          { tenTinhThanh: 'Bến Tre', maTinh: 'BT' },
          { tenTinhThanh: 'Vĩnh Long', maTinh: 'VL' },
          { tenTinhThanh: 'An Giang', maTinh: 'AG' },
          { tenTinhThanh: 'Tiền Giang', maTinh: 'TG' },
          { tenTinhThanh: 'Đồng Tháp', maTinh: 'DT' },
          { tenTinhThanh: 'Kiên Giang', maTinh: 'KG' },
          { tenTinhThanh: 'Sóc Trăng', maTinh: 'ST' },
        ],
        searchTerm: '',
      };
    },
    computed: {
      filteredProvinces() {
        if (!this.searchTerm) {
          return this.provinces;
        }
        return this.provinces.filter((province) =>
          province.tenTinhThanh.toLowerCase().includes(this.searchTerm.toLowerCase())
        );
      },
    },
  };
  </script>