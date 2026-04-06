import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
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
        meta: { title: '首页', icon: 'HomeFilled', requiresAuth: true }
      },
      {
        path: 'basic',
        name: 'Basic',
        meta: { title: '基础数据', icon: 'Setting', requiresAuth: true },
        children: [
          {
            path: 'item',
            name: 'Item',
            component: () => import('@/views/basic/item.vue'),
            meta: { title: '物料管理', requiresAuth: true }
          },
          {
            path: 'warehouse',
            name: 'Warehouse',
            component: () => import('@/views/basic/warehouse.vue'),
            meta: { title: '仓库管理', requiresAuth: true }
          }
        ]
      },
      {
        path: 'supplier',
        name: 'Supplier',
        component: () => import('@/views/supplier/index.vue'),
        meta: { title: '供应商管理', icon: 'OfficeBuilding', requiresAuth: true }
      },
      {
        path: 'inventory',
        name: 'Inventory',
        component: () => import('@/views/inventory/index.vue'),
        meta: { title: '库存管理', icon: 'Box', requiresAuth: true }
      },
      {
        path: 'purchase',
        name: 'Purchase',
        component: () => import('@/views/purchase/index.vue'),
        meta: { title: '采购管理', icon: 'ShoppingCart', requiresAuth: true }
      },
      {
        path: 'sales',
        name: 'Sales',
        component: () => import('@/views/sales/index.vue'),
        meta: { title: '销售管理', icon: 'Sell', requiresAuth: true }
      },
      {
        path: 'production',
        name: 'Production',
        component: () => import('@/views/production/index.vue'),
        meta: { title: '生产管理', icon: 'Operation', requiresAuth: true }
      },
      {
        path: 'logistics',
        name: 'Logistics',
        component: () => import('@/views/logistics/index.vue'),
        meta: { title: '物流管理', icon: 'Van', requiresAuth: true }
      },
      {
        path: 'report',
        name: 'Report',
        component: () => import('@/views/report/index.vue'),
        meta: { title: '报表分析', icon: 'DataAnalysis', requiresAuth: true }
      },
      {
        path: 'system',
        name: 'System',
        meta: { title: '系统管理', icon: 'Setting', requiresAuth: true },
        children: [
          {
            path: 'user',
            name: 'User',
            component: () => import('@/views/system/user.vue'),
            meta: { title: '用户管理', requiresAuth: true }
          },
          {
            path: 'operation-log',
            name: 'OperationLog',
            component: () => import('@/views/system/operation-log.vue'),
            meta: { title: '操作日志', requiresAuth: true }
          },
          {
            path: 'inventory-warning',
            name: 'InventoryWarning',
            component: () => import('@/views/system/inventory-warning.vue'),
            meta: { title: '库存预警', requiresAuth: true }
          }
        ]
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth !== false) {
    if (!userStore.isLoggedIn) {
      next('/login')
    } else {
      next()
    }
  } else {
    if (userStore.isLoggedIn && to.path === '/login') {
      next('/dashboard')
    } else {
      next()
    }
  }
})

export default router
