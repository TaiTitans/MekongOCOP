<template>
    <NavBarS title="Thống kê" />
  
    <div class="flex flex-col items-center w-full max-w-7xl mt-20 space-y-8 ml-6 mr-6 mb-10">
      <div class="w-full bg-white p-4 rounded-lg shadow-md">
        <h2 class="text-xl font-semibold mb-4">Doanh thu</h2>
        <canvas id="revenueChart"></canvas>
      </div>
      <div class="w-full bg-white p-4 rounded-lg shadow-md">
        <h2 class="text-xl font-semibold mb-4">Số lượng đơn hàng</h2>
        <canvas id="orderChart"></canvas>
      </div>
      <div class="w-full bg-white p-4 rounded-lg shadow-md">
        <h2 class="text-xl font-semibold mb-4">Số lượng sản phẩm</h2>
        <canvas id="productChart"></canvas>
      </div>
    </div>
  

  </template>
  
  
  <script>
  import NavBarS from '../../components/NavbarS.vue'
  import api from '../../services/api.service';
  import { Chart, registerables } from 'chart.js';
  
  Chart.register(...registerables);
  
  export default {
    name: 'ChartDashboard',
    components: {
      NavBarS,
    },
    data() {
      return {
        storeId: null,
        revenueData: null,
        orderData: null,
        productCount: null,
        notification: {
          message: '',
          type: ''
        }
      };
    },
    async mounted() {
      await this.fetchStoreId();
      await this.fetchData();
      this.renderCharts();
    },
    methods: {
      async fetchStoreId() {
        try {
          const response = await api.get(`/api/v1/seller/store`);
          this.storeId = response.data.data.store_id; // Adjust based on actual response structure
        } catch (error) {
          console.error('Error fetching store ID:', error);
          this.notification.message = 'Error fetching store ID!';
          this.notification.type = 'error';
        }
      },
      async fetchData() {
        if (!this.storeId) return;
  
        try {
          const revenueResponse = await api.get(`/api/v1/seller/store/total/${this.storeId}`);
          const orderResponse = await api.get(`/api/v1/seller/store/order/total/${this.storeId}`);
          const productResponse = await api.get(`/api/v1/seller/product/count/${this.storeId}`);
  
          this.revenueData = revenueResponse.data;
          this.orderData = orderResponse.data;
          this.productCount = productResponse.data;
        } catch (error) {
          console.error('Error fetching data:', error);
          this.notification.message = 'Error fetching data!';
          this.notification.type = 'error';
        }
      },
      renderCharts() {
        if (this.revenueData) {
          new Chart(document.getElementById('revenueChart'), {
            type: 'bar',
            data: {
              labels: ['Ngày', 'Tháng', 'Năm'],
              datasets: [{
                label: 'Doanh thu',
                data: [this.revenueData.dayRevenue, this.revenueData.monthRevenue, this.revenueData.yearRevenue],
                backgroundColor: ['rgba(75, 192, 192, 0.2)'],
                borderColor: ['rgba(75, 192, 192, 1)'],
                borderWidth: 1
              }]
            },
            options: {
              scales: {
                y: {
                  beginAtZero: true
                }
              }
            }
          });
        }
  
        if (this.orderData) {
          new Chart(document.getElementById('orderChart'), {
            type: 'line',
            data: {
              labels: ['Ngày', 'Tháng', 'Năm'],
              datasets: [{
                label: 'Đơn hàng',
                data: [this.orderData.dayOrderCount, this.orderData.monthOrderCount, this.orderData.yearOrderCount],
                backgroundColor: ['rgba(153, 102, 255, 0.2)'],
                borderColor: ['rgba(153, 102, 255, 1)'],
                borderWidth: 1
              }]
            },
            options: {
              scales: {
                y: {
                  beginAtZero: true
                }
              }
            }
          });
        }
  
        if (this.productCount !== null) {
          new Chart(document.getElementById('productChart'), {
            type: 'doughnut',
            data: {
              labels: ['Sản phẩm'],
              datasets: [{
                label: 'Sản phẩm',
                data: [this.productCount],
                backgroundColor: ['rgba(255, 159, 64, 0.2)'],
                borderColor: ['rgba(255, 159, 64, 1)'],
                borderWidth: 1
              }]
            },
            options: {
              responsive: true,
              plugins: {
                legend: {
                  position: 'top',
                },
                tooltip: {
                  callbacks: {
                    label: function (context) {
                      return `${context.label}: ${context.raw}`;
                    }
                  }
                }
              }
            }
          });
        }
      }
    }
  };
  </script>
  
  <style>
  /* Add any custom styles here */
  </style>