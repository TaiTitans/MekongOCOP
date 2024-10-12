<template>
    <div class="flex max-h-full bg-gray-100 max-w-full">
         <NavBar/>
 <router-view/>
     </div>
 </template>
 
 <script>
 import NavBar from '../../components/navBar.vue';
 export default{
     components:{
         NavBar,
     }
 
 }
 </script>
 
 <style>
 
 </style>