<template>
  <NavbarS title="Đơn hàng" />

  <div class="flex flex-col items-center w-full max-w-7xl mt-20 ml-4 mr-4">
    <input
      v-model="searchQuery"
      @input="searchOrders"
      type="text"
      placeholder="Tìm kiếm đơn hàng..."
      class="w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring focus:ring-indigo-200 focus:border-indigo-300"
    />

    <div class="bg-white shadow-lg rounded-lg w-full mt-4">
      <div v-if="loading" class="text-center py-8">Loading...</div>

      <div v-if="!loading && orders.length === 0" class="text-center py-8">
        Không có đơn hàng nào.
      </div>

      <div v-for="order in filteredOrders" :key="order.order_id" class="p-4 border-b border-gray-200 mb-2">
        <div class="flex justify-between items-center">
          <div class="">
            <h2 class="text-lg font-semibold">Đơn hàng #{{ order.order_id }}</h2>
            <p>Ngày tạo: {{ formatDate(order.created_at) }}</p>
            <p>Trạng thái: {{ translateStatus(order.status) }}</p>
            <p>Ship: 30.000 đ</p>
            <p>Tổng giá: {{ order.total_price.toLocaleString() }} đ</p>
            <div class="flex">
              <!-- Chỉ hiển thị nút "Hủy" nếu trạng thái là Pending -->
              <button
                v-if="order.status === 'Pending'"
                @click="updateOrderStatus(order.order_id, 'cancel')"
                class="px-4 py-2 bg-white text-black rounded-md hover:bg-gray-300 mt-2 shadow-md"
              >
                Hủy
              </button>
            </div>
          </div>
        </div>

        <!-- Order items -->
        <div class="mt-4 space-y-2">
          <h3 class="font-semibold">Sản phẩm:</h3>
          <ul>
            <li
              v-for="item in order.items"
              :key="item.orderItemId"
              class="flex justify-between bg-gray-100 p-2 rounded-md cursor-pointer mt-2 hover:bg-gray-200"
              @click="fetchProductDetails(item.productId)"
            >
              <span class="text-blue-400">ID: {{ item.productId }}</span>
              <span>{{ item.quantity }} x {{ item.price.toLocaleString() }} đ</span>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div class="mt-6 mb-4 flex justify-center">
      <button
        @click="changePage(currentPage - 1)"
        :disabled="currentPage === 1"
        class="px-4 py-2 bg-gray-300 rounded-md mr-2"
      >
        Trước
      </button>
      <span class="mt-2">Trang {{ currentPage }} / {{ totalPages }}</span>
      <button
        @click="changePage(currentPage + 1)"
        :disabled="currentPage === totalPages"
        class="px-4 py-2 bg-gray-300 rounded-md ml-2"
      >
        Sau
      </button>
    </div>
  </div>

  <!-- Product Details Modal -->
  <div
    v-if="showModal"
    class="fixed inset-0 bg-gray-600 bg-opacity-50 flex justify-center items-center"
  >
    <div class="bg-white p-6 rounded-lg max-w-lg w-full relative">
      <button
        @click="showModal = false"
        class="absolute top-1 right-2 text-gray-800 hover:text-gray-700"
      >
        X
      </button>
      <h3 class="text-xl font-semibold">{{ productDetails.productName }}</h3>
      <p class="mt-2">{{ productDetails.productDescription }}</p>
      <p class="mt-2">Giá: {{ productDetails.productPrice.toLocaleString() }} đ</p>
      <p class="mt-2">Số lượng còn lại: {{ productDetails.productQuantity }}</p>
      <img
        v-if="productDetails.productImages.length > 0"
        :src="productDetails.productImages[0].imageUrl"
        class="mt-4 w-full h-64 object-cover rounded-md"
        alt="Product Image"
      />
    </div>
  </div>
</template>
  <script>
  import NavbarS from '../../components/NavbarS.vue';
  import api from '../../services/api.service';
  
  export default {
    name: 'OrderList',
    components: { NavbarS },
    data() {
      return {
        orders: [],
        searchQuery: '',
        currentPage: 1,
        perPage: 5,
        totalPages: 1,
        loading: false,
        showModal: false,
        productDetails: null,
      };
    },
    computed: {
      filteredOrders() {
        const filtered = this.orders.filter(order =>
          order.order_id.toString().includes(this.searchQuery)
        );
        this.totalPages = Math.ceil(filtered.length / this.perPage);
        return filtered.slice((this.currentPage - 1) * this.perPage, this.currentPage * this.perPage);
      },
    },
    mounted() {
      this.fetchOrders();
    },
    methods: {
      async fetchOrders() {
        this.loading = true;
        try {
          const response = await api.get('api/v1/seller/store/order');
          this.orders = response.data.data;
          this.totalPages = Math.ceil(this.orders.length / this.perPage);
        } catch (error) {
          console.error('Failed to fetch orders:', error);
        } finally {
          this.loading = false;
        }
      },
      async updateOrderStatus(orderId, status) {
        const endpoint =
          status === 'pending'
            ? `api/v1/seller/store/order/${orderId}/pending`
            : status === 'cancel'
            ? `api/v1/seller/store/order/${orderId}/cancel`
            : `api/v1/seller/store/order/${orderId}/success`;
        try {
          await api.patch(endpoint);
          this.fetchOrders();
        } catch (error) {
          console.error(`Failed to update order ${orderId}:`, error);
        }
      },
      formatDate(timestamp) {
        const date = new Date(timestamp);
        return date.toLocaleString();
      },
      translateStatus(status) {
        switch (status) {
          case 'Success':
            return 'Thành công';
          case 'Pending':
            return 'Đang giao';
          case 'Cancel':
            return 'Hủy';
          default:
            return status;
        }
      },
      changePage(page) {
        if (page > 0 && page <= this.totalPages) {
          this.currentPage = page;
        }
      },
      searchOrders() {
        this.currentPage = 1; // Reset to the first page on search
      },
      async fetchProductDetails(productId) {
        try {
          const response = await api.get(`api/v1/common/store/product/${productId}`);
          this.productDetails = response.data.data;
          this.showModal = true;
        } catch (error) {
          console.error('Failed to fetch product details:', error);
        }
      },
    },
  };
  </script>
  
  <style scoped>
  /* Add custom styles here */
  </style>
  