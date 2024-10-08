<template>
  <div class="flex max-h-full bg-gray-100 max-w-full">
    <!-- NavBarAdmin component -->
    <NavBarAdmin />

    <router-view></router-view>
  </div>
</template>

<script>
import NavBarAdmin from '../../components/navBarAdmin.vue';
import NavbarS from '../../components/NavbarS.vue';

export default {
  name: 'AdminDashboard',
  components: {
    NavBarAdmin,
  },
};
</script>

<style>
/* Add any required styles */
</style>
