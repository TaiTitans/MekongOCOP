<template>
    <div v-if="diaDiem" class="absolute top-0 left-0 w-full h-full bg-gray-900 bg-opacity-50 flex justify-center items-center z-50">
      <div class="bg-white p-10 rounded-lg">
        <h2 class="text-xl font-semibold mb-4">Chỉnh sửa địa điểm</h2>
        <form @submit.prevent="saveEdit">
            <div>
            <label for="_id" class="mr-4 font-bold">ID Địa điểm:</label>
            <input type="text" id="tenDiaDiem" v-model="editedDiaDiem._id" class="border border-gray-200 focus:border-black rounded-md">
          </div>
          <div>
            <label for="tenDiaDiem" class="mr-4 font-bold">Tên Địa Điểm:</label>
            <input type="text" id="tenDiaDiem" v-model="editedDiaDiem.tenDiaDiem" class="border border-gray-200 focus:border-black rounded-md">
          </div>
          <div>
            <label for="moTa" class="mr-4 font-bold">Mô Tả:</label>
            <textarea id="moTa" v-model="editedDiaDiem.moTa" class="border border-gray-200 focus:border-black rounded-md"></textarea>
          </div>
          <div>
            <label for="soSao" class="mr-4 font-bold">Số Sao:</label>
            <input type="number" id="soSao" v-model="editedDiaDiem.soSao" class="border border-gray-200 focus:border-black rounded-md">
          </div>
          <div>
            <label for="hinhAnh" class="mr-4 font-bold">Hình Ảnh:</label>
            <input type="text" id="hinhAnh" v-model="editedDiaDiem.hinhAnh" class="border border-gray-200 focus:border-black rounded-md">
          </div>
          <div>
            <label for="tinhThanhID" class="mr-4 font-bold">Mã Tỉnh:</label>
            <input type="text" id="tinhThanhID" v-model="editedDiaDiem.tinhThanhID" class="border border-gray-200 focus:border-black rounded-md">
          </div>
  
          <div class="flex justify-between mt-4">
            <button type="submit" class="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600">Lưu</button>
            <button type="button" @click="cancel" class="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400">Hủy</button>
          </div>
        </form>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    props: ['diaDiem'],
    data() {
      return {
        editedDiaDiem: { ...this.diaDiem },
      };
    },
    methods: {
      saveEdit() {
        this.$emit('save', this.editedDiaDiem);
      },
      cancel() {
        this.$emit('cancel');
      },
    },
  };
  </script>
  