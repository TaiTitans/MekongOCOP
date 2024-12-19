<template>
    <div class="flex flex-col min-h-screen mx-auto bg-gray-100">
      <NavbarS title="Sản phẩm" />
  
      <div class="container mx-auto px-4 py-6">
        <h1 class="text-3xl font-semibold text-gray-900 mb-6">Danh sách sản phẩm</h1>
  
<!-- Tìm kiếm và bộ lọc -->
<div class="mb-4 flex flex-wrap gap-4">
  <!-- Ô tìm kiếm -->
  <input
    type="text"
    v-model="searchQuery"
    @input="searchProducts"
    placeholder="Tìm kiếm sản phẩm..."
    class="w-full sm:w-1/3 p-2 border border-gray-300 rounded-lg"
  />

  <!-- Lọc theo tỉnh -->
  <select v-model="selectedProvince" @change="filterProducts" class="w-full sm:w-1/4 p-2 border border-gray-300 rounded-lg">
    <option value="">Tất cả tỉnh</option>
    <option v-for="province in uniqueProvinces" :key="province" :value="province">
      {{ province }}
    </option>
  </select>

  <!-- Lọc theo danh mục -->
  <select v-model="selectedCategory" @change="filterProducts" class="w-full sm:w-1/4 p-2 border border-gray-300 rounded-lg">
    <option value="">Tất cả danh mục</option>
    <option v-for="category in uniqueCategories" :key="category" :value="category">
      {{ category }}
    </option>
  </select>

  <!-- Lọc theo giá tiền -->
  <select v-model="selectedPriceRange" @change="filterProducts" class="w-full sm:w-1/4 p-2 border border-gray-300 rounded-lg">
    <option value="">Tất cả giá</option>
    <option value="0-500000">Dưới 500.000 VNĐ</option>
    <option value="500000-1000000">500.000 - 1.000.000 VNĐ</option>
    <option value="1000000-5000000">1.000.000 - 5.000.000 VNĐ</option>
    <option value="5000000">Trên 5.000.000 VNĐ</option>
  </select>
</div>

  
        <!-- Danh sách sản phẩm -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="product in paginatedProducts"
            :key="product.productId"
            class="bg-white shadow-md rounded-lg overflow-hidden"
          >
            <!-- Hình ảnh sản phẩm -->
            <img :src="getPrimaryImage(product.productImages)" alt="Product Image" class="w-full h-48 object-cover" />
  
            <div class="p-4">
              <h2 class="text-xl font-semibold text-cyan-600">{{ product.productName }}</h2>
              <p class="text-gray-700 mt-2">{{ product.productDescription }}</p>
              <p class="text-lg font-bold text-green-600 mt-4">{{ formatPrice(product.productPrice) }} VNĐ</p>
              <p class="text-gray-600 mt-2">Số lượng: {{ product.productQuantity }}</p>
              <p class="text-gray-500">Danh mục: {{ product.categoryName }}</p>
              <p class="text-gray-500">Tỉnh: {{ product.provinceName }}</p>
            </div>
          </div>
        </div>
  
        <!-- Phân trang -->
        <div class="flex justify-between items-center mt-6">
          <button
            @click="previousPage"
            :disabled="currentPage === 1"
            class="px-4 py-2 bg-gray-300 text-white rounded-lg"
          >
            Trang trước
          </button>
          <span class="text-lg">Trang {{ currentPage }} / {{ totalPages }}</span>
          <button
            @click="nextPage"
            :disabled="currentPage === totalPages"
            class="px-4 py-2 bg-gray-300 text-white rounded-lg"
          >
            Trang sau
          </button>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import NavbarS from '../../components/NavbarS.vue';
  import api from '../../services/api.service'
  export default {
    name: 'ProductAdmin',
    components: {
      NavbarS,
    },
    data() {
      return {
        products: [],         // Danh sách sản phẩm
        searchQuery: '',  
        selectedProvince: '',  // Lọc theo tỉnh
       selectedCategory: '',  // Lọc theo danh mục
    selectedPriceRange: '', // Lọc theo khoảng giá    // Giá trị tìm kiếm
        currentPage: 1,      // Trang hiện tại
        itemsPerPage: 6,     // Số sản phẩm trên mỗi trang
      };
    },
    computed: {
      uniqueProvinces() {
    return [...new Set(this.products.map(product => product.provinceName))];
  },
  uniqueCategories() {
    return [...new Set(this.products.map(product => product.categoryName))];
  },
  // Lọc sản phẩm theo tìm kiếm và các bộ lọc
  filteredProducts() {
    return this.products.filter(product => {
      const matchesSearch = product.productName.toLowerCase().includes(this.searchQuery.toLowerCase());
      const matchesProvince = this.selectedProvince === '' || product.provinceName === this.selectedProvince;
      const matchesCategory = this.selectedCategory === '' || product.categoryName === this.selectedCategory;
      const matchesPrice = this.matchesPriceRange(product.productPrice);

      return matchesSearch && matchesProvince && matchesCategory && matchesPrice;
    });
  },
      // Tính toán sản phẩm trên mỗi trang
      paginatedProducts() {
        const start = (this.currentPage - 1) * this.itemsPerPage;
        return this.filteredProducts.slice(start, start + this.itemsPerPage);
      },
      // Tính tổng số trang
      totalPages() {
        return Math.ceil(this.filteredProducts.length / this.itemsPerPage);
      },
    },
    created() {
      this.fetchProducts();
    },
    methods: {
      matchesPriceRange(price) {
    if (!this.selectedPriceRange) return true;

    const [min, max] = this.selectedPriceRange.split('-').map(Number);

    if (max) {
      return price >= min && price <= max;
    }
    return price >= min;
  },
      async fetchProducts() {
        try {
          const response = await api.get('/api/v1/admin/products');
          this.products = response.data; // Lưu danh sách sản phẩm vào state
        } catch (error) {
          console.error('Error fetching products:', error);
        }
      },
      getPrimaryImage(images) {
        const primaryImage = images.find(image => image.isPrimary);
        return primaryImage ? primaryImage.imageUrl : '';
      },
      formatPrice(price) {
        return new Intl.NumberFormat('vi-VN', {
          style: 'currency',
          currency: 'VND',
        }).format(price);
      },
      searchProducts() {
        // Reset current page về 1 khi thực hiện tìm kiếm
        this.currentPage = 1;
      },
      previousPage() {
        if (this.currentPage > 1) {
          this.currentPage--;
        }
      },
      nextPage() {
        if (this.currentPage < this.totalPages) {
          this.currentPage++;
        }
      },
    },
  };
  </script>
  
  <style scoped>
  /* Thêm bất kỳ kiểu nào ở đây nếu cần */
  </style>
  