<template>
    <NavbarS title="Thêm Sản Phẩm Mới" />
  
    <div class="flex flex-col items-center w-full max-w-7xl mt-20  ml-4 mr-4 mb-4">
      <div class="bg-white p-8 rounded-lg shadow-lg w-full">
        <form @submit.prevent="submitForm" class="space-y-6">
          <div>
            <label for="productName" class="block text-sm font-medium text-gray-700">Tên sản phẩm</label>
            <input
              v-model="productName"
              type="text"
              id="productName"
              class="mt-1 block w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring focus:ring-indigo-200 focus:border-indigo-300"
              required
            />
          </div>
  
          <div>
            <label for="productDescription" class="block text-sm font-medium text-gray-700">Mô tả sản phẩm</label>
            <textarea
              v-model="productDescription"
              id="productDescription"
              class="mt-1 block w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring focus:ring-indigo-200 focus:border-indigo-300"
              required
            ></textarea>
          </div>
  
          <div>
            <label for="productPrice" class="block text-sm font-medium text-gray-700">Giá sản phẩm</label>
            <input
              v-model="productPrice"
              type="number"
              id="productPrice"
              class="mt-1 block w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring focus:ring-indigo-200 focus:border-indigo-300"
              required
            />
          </div>
  
          <div>
            <label for="productQuantity" class="block text-sm font-medium text-gray-700">Số lượng</label>
            <input
              v-model="productQuantity"
              type="number"
              id="productQuantity"
              class="mt-1 block w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring focus:ring-indigo-200 focus:border-indigo-300"
              required
            />
          </div>
  
          <div>
            <label for="provinceId" class="block text-sm font-medium text-gray-700">Tỉnh</label>
            <select
              v-model="provinceId"
              id="provinceId"
              class="mt-1 block w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring focus:ring-indigo-200 focus:border-indigo-300"
              required
            >
              <option v-for="province in provinces" :key="province.id" :value="province.id">{{ province.name }}</option>
            </select>
          </div>
  
          <div>
            <label for="categoryId" class="block text-sm font-medium text-gray-700">Danh mục</label>
            <select
              v-model="categoryId"
              id="categoryId"
              class="mt-1 block w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring focus:ring-indigo-200 focus:border-indigo-300"
              required
            >
              <option v-for="category in categories" :key="category.id" :value="category.id">{{ category.name }}</option>
            </select>
          </div>
  
          <div>
            <label for="images" class="block text-sm font-medium text-gray-700">Hình ảnh</label>
            <input
              type="file"
              id="images"
              multiple
              @change="handleFileUpload"
              class="mt-1 block w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring focus:ring-indigo-200 focus:border-indigo-300"
            />
          </div>


          <button
  type="submit"
  :disabled="isLoading"
  class="w-full px-4 py-3 bg-cyan-500 text-white rounded-md hover:bg-white hover:text-black focus:outline-none focus:ring-2 focus:ring-indigo-500"
>
  Thêm sản phẩm
</button>

        </form>
      </div>
  
      <!-- Notification -->
      <div v-if="notification.message" :class="`mt-4 p-4 rounded-md ${notification.type === 'success' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`">
        {{ notification.message }}
      </div>
      

          <!-- Toast Notifications -->
    <div v-if="toastVisible" class="absolute bottom-0 left-0 p-4 w-full max-w-xs" role="alert">
      <div :class="`flex items-center p-4 mb-4 text-cyan-500 bg-white rounded-lg shadow dark:text-black dark:bg-white transition-all duration-300 ease-out ${toastType === 'success' ? 'bg-green-100 text-green-500' : 'bg-red-100 text-red-500'}`">
        <div
          class="inline-flex items-center justify-center flex-shrink-0 w-8 h-8 rounded-full"
          :class="toastType === 'success' ? 'bg-green-100' : 'bg-red-100'"
        >
          <svg
            class="w-5 h-5"
            :fill="toastType === 'success' ? 'currentColor' : 'currentColor'"
            viewBox="0 0 20 20"
          >
            <path
              :d="toastType === 'success' ? 'M10 .5a9.5 9.5 0 1 0 9.5 9.5A9.51 9.51 0 0 0 10 .5Zm3.707 8.207-4 4a1 1 0 0 1-1.414 0l-2-2a1 1 0 0 1 1.414-1.414L9 10.586l3.293-3.293a1 1 0 0 1 1.414 1.414Z' : 'M10 .5a9.5 9.5 0 1 0 9.5 9.5A9.51 9.51 0 0 0 10 .5Zm3.707 11.793a1 1 0 1 1-1.414 1.414L10 11.414l-2.293 2.293a1 1 0 0 1-1.414-1.414L8.586 10 6.293 7.707a1 1 0 0 1 1.414-1.414L10 8.586l2.293-2.293a1 1 0 0 1 1.414 1.414L11.414 10l2.293 2.293Z'"
            />
            />
          </svg>
        </div>
        <div class="ml-3 text-sm font-normal">
          {{ toastMessage }}
        </div>
        <button @click="dismissToast" type="button" class="ml-auto -mx-1.5 -my-1.5 bg-white text-cyan-400 hover:text-gray-900 rounded-lg focus:ring-2 focus:ring-gray-300 p-1.5 hover:bg-gray-100 inline-flex items-center justify-center h-8 w-8 dark:text-cyan-500 dark:hover:text-white dark:bg-gray-200 dark:hover:bg-gray-700" aria-label="Close">
          <span class="sr-only">Đóng</span>
          <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6" />
          </svg>
        </button>
      </div>
    </div>
    </div>
  </template>
  
  <script>
  import NavbarS from '../../components/NavbarS.vue';
  import api from '../../services/api.service';
  
  export default {
    name: 'Product',
    components: { NavbarS },
    data() {
      return {
        productName: '',
        productDescription: '',
        productPrice: '',
        productQuantity: '',
        provinceId: '',
        categoryId: '',
        images: [],
        notification: {
          message: '',
          type: ''
        },
        provinces: [
          { id: 1, name: 'An Giang' },
          { id: 2, name: 'Bạc Liêu' },
          { id: 3, name: 'Bến Tre' },
          { id: 4, name: 'Cà Mau' },
          { id: 5, name: 'Cần Thơ' },
          { id: 6, name: 'Đồng Tháp' },
          { id: 7, name: 'Hậu Giang' },
          { id: 8, name: 'Kiên Giang' },
          { id: 9, name: 'Long An' },
          { id: 10, name: 'Sóc Trăng' },
          { id: 11, name: 'Tiền Giang' },
          { id: 12, name: 'Trà Vinh' },
          { id: 13, name: 'Vĩnh Long' }
        ],
        categories: [
          { id: 1, name: 'Thực phẩm' },
          { id: 2, name: 'Thủ công mỹ nghệ' },
          { id: 3, name: 'Mỹ phẩm' },
          { id: 4, name: 'Đồ uống' },
          { id: 5, name: 'Làng nghề truyền thống' },
          { id: 6, name: 'Du lịch' },
          { id: 7, name: 'Sản phẩm dược liệu' }
        ],
        toastVisible: false,
      toastMessage: '',
      toastType: '',
      };
    },
    methods: {
      handleFileUpload(event) {
  const files = Array.from(event.target.files);
  this.images = [...this.images, ...files];  // Thêm các file mới vào danh sách
},
  async submitForm() {
    this.toastVisible = false;
  const formData = new FormData();
  const dto = {
    productName: this.productName,
    productDescription: this.productDescription,
    productPrice: this.productPrice,
    productQuantity: this.productQuantity,
    provinceId: this.provinceId,
    categoryId: this.categoryId
  };
  formData.append('dto', JSON.stringify(dto));
  this.images.forEach((image) => {
    formData.append('image', image);
  });

  try {
    this.showToast('success', 'Đang tải dữ liệu');
    const response = await api.post('/api/v1/seller/store/product', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    // Hiển thị thông báo thành công
    this.notification.message = 'Thêm sản phẩm thành công!';
    this.notification.type = 'success';
  } catch (error) {
    this.showToast('success', 'Thêm sản phẩm thất bại');
    this.notification.message = 'Thêm sản phẩm thất bại';
    this.notification.type = 'error';
  } finally {
    setTimeout(() => {
      // Làm sạch dữ liệu form
      this.productName = '';
      this.productDescription = '';
      this.productPrice = '';
      this.productQuantity = '';
      this.provinceId = '';
      this.categoryId = '';
      this.images = []; // Xóa danh sách hình ảnh

      // Ẩn thông báo sau 2 giây
      this.notification.message = '';
      this.showToast('success', 'Thêm sản phẩm thành công');
    }, 2000);
  }
},

showToast(type, message) {
      this.toastType = type;
      this.toastMessage = message;
      this.toastVisible = true;

      setTimeout(() => {
        this.dismissToast();
      }, 5000); // Auto dismiss after 4 seconds
    },

    dismissToast() {
      this.toastVisible = false;
    },
}
  };
  </script>
  
  <style>
  /* Add any custom styles here */
  </style>