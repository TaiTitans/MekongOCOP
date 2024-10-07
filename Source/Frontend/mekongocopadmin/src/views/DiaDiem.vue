<template>
  <div class="w-[100%]">
    <!-- Navbar -->
    <div class="flex flex-col flex-1 overflow-y-auto relative" id="navbar">
      <div class="flex items-center justify-between h-16 bg-white border-b border-gray-200">
        <!-- Search input and title -->
        <div class="flex items-center px-4">
          <input class="mx-4 w-full border rounded-md px-4 py-2" type="text" v-model="searchQuery" placeholder="Search">
        </div>
        <div class="font-mono text-[18px]">Địa Điểm</div>
        <div class="flex items-center pr-4">
          <!-- Fetch data button -->
          <button class="flex items-center text-gray-500 hover:text-gray-700 focus:outline-none focus:text-gray-700" @click="fetchData">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l-7-7 7-7m5 14l7-7-7-7" />
            </svg>
          </button>
        </div>
      </div>
      <!-- Button để hiển thị form thêm địa điểm -->
<button @click="showCreateForm" class="w-[10%] px-2 py-2 bg-blue-600 text-white rounded-md hover:bg-gray-600 mt-4 ml-4">Thêm Địa Điểm</button>
<!-- Form thêm địa điểm -->
<div v-if="editFormVisible" class="p-4 bg-white rounded-lg shadow-md ml-2 mr-2 mt-2">
    <h2 class="text-lg font-semibold mb-4">Thêm Địa Điểm Mới</h2>
    <div class="mb-4">
        <label for="tenDiaDiem" class="block text-sm font-medium text-gray-700">Tên Địa Điểm</label>
        <input v-model="newDiaDiem.tenDiaDiem" type="text" id="tenDiaDiem" placeholder="Nhập tên địa điểm" class="border border-gray-300 rounded-md px-3 py-2 mt-1 block w-full">
    </div>
    <div class="mb-4">
        <label for="moTa" class="block text-sm font-medium text-gray-700">Mô Tả</label>
        <textarea v-model="newDiaDiem.moTa" id="moTa" placeholder="Nhập mô tả" rows="3" class="border border-gray-300 rounded-md px-3 py-2 mt-1 block w-full"></textarea>
    </div>
    <div class="mb-4">
        <label for="soSao" class="block text-sm font-medium text-gray-700">Số Sao</label>
        <input v-model="newDiaDiem.soSao" type="number" id="soSao" placeholder="Nhập số sao" class="border border-gray-300 rounded-md px-3 py-2 mt-1 block w-full">
    </div>
    <div class="mb-4">
        <label for="hinhAnh" class="block text-sm font-medium text-gray-700">Hình Ảnh</label>
        <input v-model="newDiaDiem.hinhAnh" type="text" id="hinhAnh" placeholder="URL hình ảnh" class="border border-gray-300 rounded-md px-3 py-2 mt-1 block w-full">
    </div>
    <div class="mb-4">
        <label for="tinhThanhID" class="block text-sm font-medium text-gray-700">Tỉnh/Thành phố</label>
        <select v-model="newDiaDiem.tinhThanhID" id="tinhThanhID" class="border border-gray-300 rounded-md px-3 py-2 mt-1 block w-full">
    <option disabled value="">Chọn Tỉnh/Thành phố</option>
    <option v-for="tinhThanh in danhSachTinhThanh" :key="tinhThanh._id" :value="tinhThanh._id">{{ tinhThanh.tenTinhThanh }}</option>
</select>

    </div>
    <div class="flex justify-end">
        <button @click="createNewDiaDiem" class="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 mr-2">Lưu</button>
        <button @click="cancelCreateForm" class="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600">Hủy</button>
    </div>
</div>


      <!-- List of DiaDiem -->
      <div class="p-4 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        <!-- Item template -->
        <div v-for="item in paginatedDiaDiems" :key="item._id" class="bg-white rounded-lg shadow-md overflow-hidden">
          <!-- Image -->
          <img :src="item.hinhAnh" alt="Hình ảnh" class="w-full h-64 object-cover">
          <!-- Information -->
          <div class="p-4">
            <div v-if="successMessage" class="fixed  right-0 bg-green-500 text-white px-4 py-2 rounded-tl-md">
        {{ successMessage }}
    </div>
            <div class="text-lg font-semibold mb-2">{{ item.tenDiaDiem }}</div>
            <div class="text-sm text-gray-500">Số sao: {{ item.soSao }}</div>
            <div class="text-sm text-gray-700 mt-2">{{ item.moTa }}</div>
            <div class="flex justify-between mt-4">
              <button @click="editDiaDiem(item)" class="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600">Chỉnh sửa</button>
              <button @click="deleteDiaDiem(item._id)" class="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600">Xoá</button>
            </div>
            <EditDiaDiemForm v-if="editFormVisible" :diaDiem="selectedDiaDiem" @save="saveEdit" @cancel="cancelEdit" />
          </div>
        </div>
      </div>
      <!-- Custom Pagination -->
      <div class="flex justify-center my-4">
        <button class="mx-2 px-4 py-2 rounded-md bg-blue-500 text-white" @click="prevPage" :disabled="currentPage === 1">Trước</button>
        <div>{{ currentPage }}</div>
        <button class="mx-2 px-4 py-2 rounded-md bg-blue-500 text-white" @click="nextPage" :disabled="currentPage === totalPages">Sau</button>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../services/api.service';
import EditDiaDiemForm from '../components/diadiem/EditDiaDiemForm.vue';
export default {
  data() {
    return {
      searchQuery: '',
      diaDiemList: [],
      currentPage: 1,
      pageSize: 8,
      editFormVisible: false,
      selectedDiaDiem:null,
      successMessage: '',
      newDiaDiem: {
        tenDiaDiem: '',
        moTa: '',
        soSao: 0,
        hinhAnh: '',
        tinhThanhID: ''
      }
    };
  },
  components: {
    EditDiaDiemForm,
  },
  methods: {
    async fetchTinhThanhList() {
    try {
        const response = await api.get('/api/tinhthanh/getAll');
        this.danhSachTinhThanh = response.data.data; // Gán danh sách tỉnh thành từ API vào biến danhSachTinhThanh
    } catch (error) {
        console.error('Lỗi khi lấy danh sách tỉnh thành:', error);
    }
},
    showCreateForm() {
        this.editFormVisible = true;
    },
    createNewDiaDiem() {
        // Gửi request POST để tạo mới địa điểm
        api.post('/api/diadiem/create', this.newDiaDiem)
            .then(() => {
                // Tạo mới thành công, đóng form và cập nhật lại danh sách địa điểm
                this.editFormVisible = false;
                this.fetchData();
                this.successMessage = 'Thêm địa điểm thành công!';

// Đặt thời gian hiển thị thông báo và sau đó ẩn đi
setTimeout(() => {
    this.successMessage = '';
}, 3000); 
                // Hiển thị thông báo hoặc thực hiện các hành động khác
            })
            .catch(error => {
                console.error('Lỗi khi tạo mới địa điểm:', error);
                // Xử lý lỗi và hiển thị thông báo
            });
    },
    cancelCreateForm() {
        // Hủy thao tác tạo mới, đóng form
        this.editFormVisible = false;
    },
    // Các phương thức khác của bạn
    async fetchData() {
      try {
        const response = await api.get('/api/diadiem/getAll');
        this.diaDiemList = response.data.data;
      } catch (error) {
        console.error('Lỗi khi lấy danh sách địa điểm:', error);
      }
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
    editDiaDiem(diaDiem) {
      this.selectedDiaDiem = diaDiem;
      this.editFormVisible = true;
    },
    deleteDiaDiem(itemId) {
      // Gửi request DELETE tới API để xoá địa điểm
      api.delete(`/api/diadiem/delete/${itemId}`)
        .then(() => {
          this.successMessage = 'Xoá thành công!';

// Đặt thời gian hiển thị thông báo và sau đó ẩn đi
setTimeout(() => {
    this.successMessage = '';
}, 3000); 
          // Xoá thành công, cập nhật lại danh sách địa điểm
          this.fetchData();
        })
        .catch(error => {
          console.error('Lỗi khi xoá địa điểm:', error);
        });
    },
    showCreateForm() {
        this.editFormVisible = true;
    },
    
    saveEdit(newData) {
      // Gửi request POST tới API để cập nhật thông tin của địa điểm
      api.put('/api/diadiem/update', newData)
        .then(() => {
          // Cập nhật thành công, đóng form và cập nhật lại danh sách địa điểm
          this.editFormVisible = false;
          this.fetchData();
          this.successMessage = 'Cập nhật thành công!';

// Đặt thời gian hiển thị thông báo và sau đó ẩn đi
setTimeout(() => {
    this.successMessage = '';
}, 3000); // 3 giây sau đó ẩn đi
        })
        .catch(error => {
          console.error('Lỗi khi cập nhật địa điểm:', error);
        });
    },
    cancelEdit() {
      // Hủy chỉnh sửa, đóng form
      this.editFormVisible = false;
    },
  },
  computed: {
    paginatedDiaDiems() {
      const startIndex = (this.currentPage - 1) * this.pageSize;
      const endIndex = startIndex + this.pageSize;
      return this.diaDiemList.slice(startIndex, endIndex);
    },
    totalPages() {
      return Math.ceil(this.diaDiemList.length / this.pageSize);
    },
  },
  mounted() {
    this.fetchData();
    this.fetchTinhThanhList();
  },
};
</script>

<style>
/* Add your custom styles here */
</style>
