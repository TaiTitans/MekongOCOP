<template>
    <div class="flex flex-col min-h-screen mx-auto w-fit">
      <!-- Phần tiêu đề -->
      <div class="bg-cyan-500 text-white p-3 rounded-sm shadow-md text-center font-semibold text-[20px] mt-4">
        THỐNG KÊ
      </div>
      
      <!-- Nội dung chính -->
      <div class="flex-grow p-6 bg-gray-100">
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          <div class="bg-white rounded-lg shadow-md p-4">
            <h3 class="text-xl font-semibold mb-4 text-center">Tổng số người dùng</h3>
            <canvas id="userChart"></canvas>
          </div>
          <div class="bg-white rounded-lg shadow-md p-4">
            <h3 class="text-xl font-semibold mb-4 text-center">Tổng số sản phẩm</h3>
            <canvas id="productChart"></canvas>
          </div>
          <div class="bg-white rounded-lg shadow-md p-4">
            <h3 class="text-xl font-semibold mb-4 text-center">Doanh số đơn hàng</h3>
            <canvas id="orderChart"></canvas>
          </div>
        </div>
  
        <!-- Doanh thu -->
        <div class="bg-white rounded-lg shadow-md p-4 mt-6">
          <h3 class="text-xl font-semibold mb-4 text-center">Doanh thu</h3>
          <canvas id="revenueChart"></canvas>
        </div>
      </div>
    </div>
  </template>
  
  
  
  <script>
  import {
    Chart,
    BarController,
    BarElement,
    DoughnutController,
    LineController,
    ArcElement,
    PointElement,
    LineElement,
    CategoryScale,
    LinearScale,
    Title,
    Tooltip,
    Legend,
  } from "chart.js";
  
  import api from "@/services/api.service";
  
  export default {
    name: "DashboardChart",
    data() {
      return {
        userData: null,
        productData: null,
        orderData: null,
        revenueData: null,
      };
    },
    async mounted() {
      await this.fetchData();
  
      // Register all necessary components
      Chart.register(
        BarController,
        BarElement,
        DoughnutController,
        LineController,
        ArcElement,   // Required for doughnut charts
        PointElement, // Required for line charts
        LineElement,  // Required for line charts
        CategoryScale,
        LinearScale,
        Title,
        Tooltip,
        Legend
      );
  
      // Create user chart (Bar chart)
      new Chart(document.getElementById("userChart"), {
        type: "bar",
        data: {
          labels: ["Tổng", "Khách hàng", "Người bán"],
          datasets: [
            {
              label: "Người dùng",
              data: [
                this.userData.totalUsers,
                this.userData.usersWithRoleBuyer,
                this.userData.usersWithRoleSeller,
              ],
              backgroundColor: ["#42A5F5", "#66BB6A", "#FFA726"],
            },
          ],
        },
      });
  
      // Create product chart (Doughnut chart)
      new Chart(document.getElementById("productChart"), {
        type: "doughnut",
        data: {
          labels: ["Số lượng sản phẩm"],
          datasets: [
            {
              label: "Sản phẩm",
              data: [this.productData.totalProduct],
              backgroundColor: ["#FF6384"],
            },
          ],
        },
      });
  
      // Create order chart (Doughnut chart)
      new Chart(document.getElementById("orderChart"), {
        type: "doughnut",
        data: {
          labels: ["Số lượng đơn hàng"],
          datasets: [
            {
              label: "Đơn hàng",
              data: [this.orderData.totalOrders],
              backgroundColor: ["#36A2EB"],
            },
          ],
        },
      });
  
      // Create revenue chart (Line chart)
      new Chart(document.getElementById("revenueChart"), {
        type: "line",
        data: {
          labels: ["Ngày", "Tháng", "Năm"],
          datasets: [
            {
              label: "Doanh thu",
              data: [
                this.revenueData.totalToday,
                this.revenueData.totalMonth,
                this.revenueData.totalYear,
              ],
              backgroundColor: "rgba(75, 192, 192, 0.2)",
              borderColor: "rgba(75, 192, 192, 1)",
              borderWidth: 2,
              fill: true,
            },
          ],
        },
      });
    },
    methods: {
      async fetchData() {
        // Fetch user data
        const userResponse = await api.get("/api/v1/admin/user/count");
        this.userData = userResponse.data;
  
        // Fetch product data
        const productResponse = await api.get("/api/v1/admin/product/count");
        this.productData = productResponse.data;
  
        // Fetch order data
        const orderResponse = await api.get("/api/v1/admin/order/count");
        this.orderData = orderResponse.data;
  
        // Fetch revenue data
        const revenueResponse = await api.get("/api/v1/admin/order/total");
        this.revenueData = revenueResponse.data;
      },
    },
  };
  </script>
  
  
  <style>
  .chart-container {
    margin: 20px;
    max-width: 600px;
  }
  </style>
  