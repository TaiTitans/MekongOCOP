<template>
    <div class="flex flex-col items-center justify-center min-h-screen px-10 sm:px-14 md:px-22 lg:px-34">
      <div class="bg-white shadow-lg rounded-lg w-full max-w-6xl mt-4">
        <div class="relative h-40 sm:h-60 md:h-84 lg:h-90 overflow-hidden rounded-t-lg">
          <img :src="store.store_banner" alt="Store Banner" class="w-full h-full object-cover" />
          <button @click="openEditBannerModal" class="absolute bottom-2 right-2 bg-white p-2 rounded-full shadow-md">
            <i class="fas fa-edit"></i>
          </button>
        </div>
        <div class="flex flex-col sm:flex-row items-center justify-between px-8 py-8">
          <div class="flex items-center mb-4 sm:mb-0">
            <div class="relative">
              <img :src="store.store_logo" alt="Store Logo" class="w-16 h-16 rounded-full object-cover mr-4 border-2 shadow-lg border-green-300" />
              <button @click="openEditLogoModal" class="absolute bottom-0 right-0 bg-white p-1 rounded-full shadow-md">
                <i class="fas fa-edit text-sm"></i>
              </button>
            </div>
            <div>
              <h2 class="text-2xl font-bold text-sky-500">{{ store.store_name }}</h2>
              <div class="flex">
                <i class="fas fa-map-marker-alt mr-2 mt-1 text-gray-500"></i>
                <p class="text-gray-400 font-style: italic">{{ store.store_address }}</p>
              </div>
              <p class="text-gray-600 max-w-3xl ml-2">{{ store.store_description }}</p>
            </div>
          </div>
          <button @click="openEditStoreModal" class="bg-white hover:bg-blue-600 text-black font-bold py-2 px-4 rounded mt-4 sm:mt-0 shadow-md">
            Chỉnh sửa cửa hàng
          </button>
        </div>
        <div class="w-full bg-gray-200 h-[2px] mb-2"></div>
        <div class="ml-9 font-bold text-[24px]">Sản phẩm</div>
        <!-- Search bar -->
        <div class="px-8 py-4">
          <input v-model="searchQuery" @input="handleSearch" type="text" placeholder="Tìm kiếm sản phẩm..." class="w-full p-2 border rounded-md">
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-8 px-8 py-8">
          <div v-for="product in paginatedProducts" :key="product.productId" class="bg-gray-100 rounded-lg p-4 shadow-md mt-2">
            <div class="flex items-center mb-2 justify-end">
              <i @click="deleteProduct(product.productId)" class="fa-solid fa-x float-right cursor-pointer"></i>
            </div>
            <div class="flex items-center mb-4">
              <img :src="product.productImages[0].imageUrl" alt="Product Image" class="w-16 h-16 rounded-full object-cover mr-4" />
              <div>
                <h3 class="text-lg font-bold">{{ product.productName }}</h3>
                <p class="text-gray-600">{{ product.productDescription }}</p>
              </div>
            </div>
            <div class="flex items-center justify-between">
              <div>
                <p class="text-gray-600">Giá: {{ formatPrice(product.productPrice) }}</p>
                <p class="text-gray-600">Số lượng: {{ product.productQuantity }}</p>
              </div>
              <div>
                <p class="text-gray-600">Danh mục: {{ product.categoryName }}</p>
                <p class="text-gray-600">Tỉnh thành: {{ product.provinceName }}</p>
              </div>
            </div>
            <div class="mt-4 flex justify-between items-center">
              <div>
                <p class="text-gray-600">Đánh giá: {{ product.reviews.length > 0 ? calculateAvgRating(product.reviews) : 'N/A' }}</p>
                <p class="text-gray-600">Bình luận: {{ product.reviews.length }}</p>
              </div>
              <button @click="openEditProductModal(product)" class="bg-gray-300 hover:bg-gray-200 text-black font-bold py-2 px-4 rounded">
                Chỉnh sửa
              </button>
            </div>
          </div>
        </div>
        <!-- Pagination -->
        <div class="flex justify-center py-4">
          <button @click="prevPage" :disabled="currentPage === 1" class="mx-1 px-3 py-1 bg-gray-200 rounded-md">Trước</button>
          <span class="mx-1 px-3 py-1">{{ currentPage }} / {{ totalPages }}</span>
          <button @click="nextPage" :disabled="currentPage === totalPages" class="mx-1 px-3 py-1 bg-gray-200 rounded-md">Sau</button>
        </div>
      </div>
  
  <!-- Edit Product Modal -->
  <div v-if="showEditProductModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full">
    <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
      <h3 class="text-lg font-bold mb-4">Chỉnh sửa sản phẩm</h3>
      <form @submit.prevent="updateProduct">
        <input v-model="editingProduct.productName" type="text" placeholder="Tên sản phẩm" class="w-full p-2 mb-2 border rounded">
        <textarea v-model="editingProduct.productDescription" placeholder="Mô tả sản phẩm" class="w-full p-2 mb-2 border rounded"></textarea>
        <input v-model="editingProduct.productPrice" type="number" placeholder="Giá" class="w-full p-2 mb-2 border rounded">
        <input v-model="editingProduct.productQuantity" type="number" placeholder="Số lượng" class="w-full p-2 mb-2 border rounded">
        <select v-model="editingProduct.provinceId" class="w-full p-2 mb-2 border rounded">
          <option v-for="province in provinces" :key="province.id" :value="province.id">
            {{ province.name }}
          </option>
        </select>
        <select v-model="editingProduct.categoryId" class="w-full p-2 mb-2 border rounded">
          <option v-for="category in categories" :key="category.id" :value="category.id">
            {{ category.name }}
          </option>
        </select>
        <input type="file" @change="handleImageUpload" multiple accept="image/*" class="w-full p-2 mb-2 border rounded">
        <div class="flex justify-end">
          <button type="button" @click="closeEditProductModal" class="mr-2 px-4 py-2 bg-gray-200 rounded">Hủy</button>
          <button type="submit" class="px-4 py-2 bg-blue-500 text-white rounded">Lưu</button>
        </div>
      </form>
    </div>
  </div>
  
      <!-- Edit Store Modal -->
      <div v-if="showEditStoreModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full">
        <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
          <h3 class="text-lg font-bold mb-4">Chỉnh sửa cửa hàng</h3>
          <form @submit.prevent="updateStore">
            <input v-model="editingStore.store_name" type="text" placeholder="Tên cửa hàng" class="w-full p-2 mb-2 border rounded">
            <textarea v-model="editingStore.store_description" placeholder="Mô tả cửa hàng" class="w-full p-2 mb-2 border rounded"></textarea>
            <input v-model="editingStore.store_address" type="text" placeholder="Địa chỉ cửa hàng" class="w-full p-2 mb-2 border rounded">
            <div class="flex justify-end">
              <button type="button" @click="closeEditStoreModal" class="mr-2 px-4 py-2 bg-gray-200 rounded">Hủy</button>
              <button type="submit" class="px-4 py-2 bg-blue-500 text-white rounded">Lưu</button>
            </div>
          </form>
        </div>
      </div>
  
      <!-- Edit Logo Modal -->
      <div v-if="showEditLogoModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full">
        <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
          <h3 class="text-lg font-bold mb-4">Cập nhật Logo</h3>
          <form @submit.prevent="updateLogo">
            <input type="file" @change="handleLogoUpload" accept="image/*" class="w-full p-2 mb-2 border rounded">
            <div class="flex justify-end">
              <button type="button" @click="closeEditLogoModal" class="mr-2 px-4 py-2 bg-gray-200 rounded">Hủy</button>
              <button type="submit" class="px-4 py-2 bg-blue-500 text-white rounded">Lưu</button>
            </div>
          </form>
        </div>
      </div>
  
      <!-- Edit Banner Modal -->
      <div v-if="showEditBannerModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full">
        <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
          <h3 class="text-lg font-bold mb-4">Cập nhật Banner</h3>
          <form @submit.prevent="updateBanner">
            <input type="file" @change="handleBannerUpload" accept="image/*" class="w-full p-2 mb-2 border rounded">
            <div class="flex justify-end">
              <button type="button" @click="closeEditBannerModal" class="mr-2 px-4 py-2 bg-gray-200 rounded">Hủy</button>
              <button type="submit" class="px-4 py-2 bg-blue-500 text-white rounded">Lưu</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import api from '../../services/api.service'
  
  export default {
    name: 'StoreProducts',
    data() {
      return {
        store: {},
        products: [],
        storeId: 2,
        searchQuery: '',
        currentPage: 1,
        itemsPerPage: 10,
        showEditProductModal: false,
        showEditStoreModal: false,
        showEditLogoModal: false,
        showEditBannerModal: false,
        editingProduct: {
            newImages: [], // Khởi tạo là mảng rỗng
            productId: null,
            productName: '',
            productDescription: '',
            productPrice: 0,
            productQuantity: 0,
            provinceId: '', 
            categoryId: '',
        },
        editingStore: {},
        newLogo: null,
        newBanner: null,
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
      }
    },
    computed: {
      filteredProducts() {
        return this.products.filter(product => 
          product.productName.toLowerCase().includes(this.searchQuery.toLowerCase())
        );
      },
      paginatedProducts() {
        const start = (this.currentPage - 1) * this.itemsPerPage;
        const end = start + this.itemsPerPage;
        return this.filteredProducts.slice(start, end);
      },
      totalPages() {
        return Math.ceil(this.filteredProducts.length / this.itemsPerPage);
      }
    },
    created() {
      this.fetchStoreData();
      this.fetchProducts();
    },
    methods: {
        openEditProductModal(product) {
      // Sao chép thông tin sản phẩm vào editingProduct
      this.editingProduct.productId = product.productId;
      this.editingProduct.productName = product.productName;
      this.editingProduct.productDescription = product.productDescription;
      this.editingProduct.productPrice = product.productPrice;
      this.editingProduct.productQuantity = product.productQuantity;
      this.editingProduct.provinceId = product.provinceId; // Lưu provinceId
      this.editingProduct.categoryId = product.categoryId; // Lưu categoryId
      this.showEditProductModal = true;
    },
      fetchStoreData() {
        api.get(`/api/v1/seller/store`)
          .then(response => {
            this.store = response.data.data;
          })
          .catch(error => {
            console.error('Error fetching store data:', error);
          });
      },
      fetchProducts() {
        api.get(`/api/v1/common/store/${this.storeId}/product`)
          .then(response => {
            this.products = response.data.data;
          })
          .catch(error => {
            console.error('Error fetching products:', error);
          });
      },
      formatPrice(price) {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
      },
      calculateAvgRating(reviews) {
        const totalRating = reviews.reduce((sum, review) => sum + review.rating, 0);
        return (totalRating / reviews.length).toFixed(1);
      },
      handleSearch() {
        this.currentPage = 1;
      },
      prevPage() {
        if (this.currentPage > 1) {
          this.currentPage--;
        }
      },
      nextPage() {
        if (this.currentPage < this.totalPages) {
          this.currentPage++;
        }
      },
      deleteProduct(productId) {
        if (confirm('Are you sure you want to delete this product?')) {
          api.delete(`/api/v1/seller/store/product/${productId}`)
            .then(() => {
              this.products = this.products.filter(product => product.productId !== productId);
            })
            .catch(error => {
              console.error('Error deleting product:', error);
            });
        }
      },
      openEditProductModal(product) {
        this.editingProduct = { ...product };
        this.showEditProductModal = true;
      },
      closeEditProductModal() {
        this.showEditProductModal = false;
        this.editingProduct = {};
      },
      async updateProduct() {
  try {
    const formData = new FormData();
    
    // Định dạng đối tượng DTO
    const dto = {
      productName: this.editingProduct.productName,
      productDescription: this.editingProduct.productDescription,
      productPrice: this.editingProduct.productPrice,
      productQuantity: this.editingProduct.productQuantity,
      provinceId: this.editingProduct.provinceId,
      categoryId: this.editingProduct.categoryId
    };
    
    // Chuyển DTO thành JSON string
    formData.append('dto', JSON.stringify(dto));
    
    // Xử lý các hình ảnh
    for (const image of this.editingProduct.newImages) {
      formData.append('image', image);
    }
    
    const productId = this.editingProduct.productId;

    // Gọi API PUT để cập nhật sản phẩm
    await api.put(`/api/v1/seller/store/product/${productId}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    this.closeEditProductModal(); // Đóng modal sau khi cập nhật
    this.fetchProducts(); // Tải lại danh sách sản phẩm
  } catch (error) {
    console.error('Error updating product:', error);
  }
},

      handleImageUpload(event) {
    const files = event.target.files; // Lấy danh sách file từ sự kiện
    this.editingProduct.newImages = []; // Đặt lại mảng hình ảnh

    // Duyệt qua tất cả các file và thêm vào mảng newImages
    for (let i = 0; i < files.length; i++) {
        this.editingProduct.newImages.push(files[i]);
    }
},
      openEditStoreModal() {
        this.editingStore = { ...this.store };
        this.showEditStoreModal = true;
      },
      closeEditStoreModal() {
        this.showEditStoreModal = false;
        this.editingStore = {};
      },
      updateStore() {
        const formData = new FormData();
        formData.append('dto', JSON.stringify(this.editingStore));
  
        api.put('/api/v1/seller/store', formData, {
  headers: {
    'Content-Type': 'multipart/form-data'
  }
})
          .then(() => {
            this.fetchStoreData();
            this.closeEditStoreModal();
          })
          .catch(error => {
            console.error('Error updating store:', error);
          });
      },
      openEditLogoModal() {
        this.showEditLogoModal = true;
      },
      closeEditLogoModal() {
        this.showEditLogoModal = false;
        this.newLogo = null;
      },
      handleLogoUpload(event) {
        this.newLogo = event.target.files[0];
      },
      updateLogo() {
        if (!this.newLogo) return;
  
        const formData = new FormData();
        formData.append('logo', this.newLogo);
  
        api.patch('/api/v1/seller/store/logo', formData, {
  headers: {
    'Content-Type': 'multipart/form-data'
  }
})
          .then(() => {
            this.fetchStoreData();
            this.closeEditLogoModal();
          })
          .catch(error => {
            console.error('Error updating logo:', error);
          });
      },
      openEditBannerModal() {
        this.showEditBannerModal = true;
      },
      closeEditBannerModal() {
        this.showEditBannerModal = false;
        this.newBanner = null;
      },
      handleBannerUpload(event) {
        this.newBanner = event.target.files[0];
      },
      updateBanner() {
        if (!this.newBanner) return;
  
        const formData = new FormData();
        formData.append('banner', this.newBanner);
  
        api.patch('/api/v1/seller/store/banner', formData, {
  headers: {
    'Content-Type': 'multipart/form-data'
  }
})
          .then(() => {
            this.fetchStoreData();
            this.closeEditBannerModal();
          })
          .catch(error => {
            console.error('Error updating banner:', error);
          });
      },
    },
  }
  </script>
  
  <style scoped>
  /* Add any custom styles here */
  </style>