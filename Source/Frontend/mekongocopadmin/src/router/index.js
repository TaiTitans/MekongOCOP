import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import TinhThanh from '../views/TinhThanh.vue'
import DiaDiem from '../views/DiaDiem.vue'
import AmThuc from '../views/AmThuc.vue'
import Feedback from '../views/Feedback.vue'
import Login from '../views/Login.vue'
import SellerDashboard from '../views/Seller/SellerDashboard.vue'
import authService from '../services/auth.service'
import AdminDashboard from '../views/Admin/AdminDashboard.vue'
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/seller',
    name: 'SellerDashboard',
    component: SellerDashboard,
    meta: { requiresSeller: true }
  },
  {
    path: '/admin',
    component: AdminDashboard, 
    meta: { requiresAdmin: true },
    children: [
      {
        path: 'tinhthanh',
        name: 'TinhThanh',
        component: TinhThanh,
      },
      {
        path: 'diadiem',
        name: 'DiaDiem',
        component: DiaDiem,
      },
      {
        path: 'amthuc',
        name: 'AmThuc',
        component: AmThuc,
      },
      {
        path: 'feedback',
        name: 'Feedback',
        component: Feedback,
      },
    ],
  }
  
  
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0 };
    }
  },
})
router.beforeEach((to, from, next) => {
  const isLoginPage = to.path === "/login";
  const accessToken = authService.getAccessToken();
  const isTokenValid = authService.isTokenValid(accessToken);

  if (!accessToken || !isTokenValid) {
    if (!isLoginPage) {
      localStorage.setItem('redirectPath', to.path);
      next('/login');
    } else {
      next();
    }
  } else {
    const userRoles = authService.getUserRoles();
    console.log("Current user roles:", userRoles);
    
    if (to.meta.requiresSeller && !userRoles.includes('ROLE_SELLER')) {
      console.log("User doesn't have ROLE_SELLER, redirecting to Home");
      next({ name: 'AdminDashboard' });
    } else if (to.meta.requiresAdmin && !userRoles.includes('ROLE_ADMIN')) {
      console.log("User doesn't have ROLE_ADMIN, redirecting to Home");
      next({ name: 'SellerDashboard' });
    } else if (isLoginPage) {
      console.log("User is already logged in, redirecting to Home");
      next({ name: 'Home' });
    } else {
      next();
    }
  }
});
export default router
