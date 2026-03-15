import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { requiresAuth: false, title: '登录' },
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '工作台', icon: 'Odometer' },
      },
      {
        path: 'supplier',
        name: 'Supplier',
        redirect: '/supplier/list',
        meta: { title: '供应商管理', icon: 'OfficeBuilding' },
        children: [
          {
            path: 'list',
            name: 'SupplierList',
            component: () => import('@/views/supplier/list.vue'),
            meta: { title: '供应商列表' },
          },
          {
            path: 'create',
            name: 'SupplierCreate',
            component: () => import('@/views/supplier/edit.vue'),
            meta: { title: '新增供应商' },
          },
          {
            path: 'edit/:id',
            name: 'SupplierEdit',
            component: () => import('@/views/supplier/edit.vue'),
            meta: { title: '编辑供应商' },
          },
        ],
      },
      {
        path: 'purchase',
        name: 'Purchase',
        redirect: '/purchase/order',
        meta: { title: '采购管理', icon: 'ShoppingCart' },
        children: [
          {
            path: 'order',
            name: 'PurchaseOrder',
            component: () => import('@/views/purchase/order.vue'),
            meta: { title: '采购订单' },
          },
        ],
      },
      {
        path: 'inventory',
        name: 'Inventory',
        redirect: '/inventory/stock',
        meta: { title: '库存管理', icon: 'Box' },
        children: [
          {
            path: 'stock',
            name: 'Stock',
            component: () => import('@/views/inventory/stock.vue'),
            meta: { title: '库存查询' },
          },
        ],
      },
      {
        path: 'report',
        name: 'Report',
        redirect: '/report/overview',
        meta: { title: '报表中心', icon: 'DataAnalysis' },
        children: [
          {
            path: 'overview',
            name: 'ReportOverview',
            component: () => import('@/views/report/overview.vue'),
            meta: { title: '报表概览' },
          },
        ],
      },
      {
        path: 'settings',
        name: 'Settings',
        redirect: '/settings/profile',
        meta: { title: '系统设置', icon: 'Setting' },
        children: [
          {
            path: 'profile',
            name: 'Profile',
            component: () => import('@/views/settings/profile.vue'),
            meta: { title: '个人设置' },
          },
        ],
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面未找到' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Navigation Guard
router.beforeEach(async (to, _from, next) => {
  // Set page title
  document.title = to.meta.title ? `${to.meta.title} - SCM` : 'SCM - 供应链管理系统'

  const userStore = useUserStore()
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)

  if (requiresAuth) {
    // Check if user is authenticated
    if (!userStore.isLoggedIn) {
      try {
        // Try to fetch user info
        await userStore.fetchCurrentUser()
        next()
      } catch {
        // Redirect to login
        next({
          path: '/login',
          query: { redirect: to.fullPath },
        })
      }
    } else {
      next()
    }
  } else {
    // If logged in and trying to access login page, redirect to dashboard
    if (to.path === '/login' && userStore.isLoggedIn) {
      next('/dashboard')
    } else {
      next()
    }
  }
})

export default router
