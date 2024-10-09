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
            class="w-full px-4 py-3 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            Thêm sản phẩm
          </button>
        </form>
      </div>
  
      <!-- Notification -->
      <div v-if="notification.message" :class="`mt-4 p-4 rounded-md ${notification.type === 'success' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`">
        {{ notification.message }}
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
        ]
      };
    },
    methods: {
      handleFileUpload(event) {
        this.images = Array.from(event.target.files);
      },
      async submitForm() {
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
        this.images.forEach((image, index) => {
          formData.append('image', image);
        });
  
        try {
          const response = await api.post('/api/v1/seller/store/product', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          });
          this.notification.message = 'Thêm sản phẩm thành công!';
          this.notification.type = 'success';
          setTimeout(() => {
            this.notification.message = '';
            window.location.reload();
          }, 2000);
        } catch (error) {
          this.notification.message = 'Thêm sản phẩm thất bại';
          this.notification.type = 'error';
          setTimeout(() => {
            this.notification.message = '';
          }, 2000);
        }
      }
    }
  };
  </script>
  
  <style>
  /* Add any custom styles here */
  </style>