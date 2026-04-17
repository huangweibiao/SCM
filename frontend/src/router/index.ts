import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue')
  },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/index.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'basic/item',
        name: 'Item',
        component: () => import('../views/basic/item.vue'),
        meta: { title: '物料管理' }
      },
      {
        path: 'basic/warehouse',
        name: 'Warehouse',
        component: () => import('../views/basic/warehouse.vue'),
        meta: { title: '仓库管理' }
      },
      {
        path: 'supplier',
        name: 'Supplier',
        component: () => import('../views/supplier/index.vue'),
        meta: { title: '供应商管理' }
      },
      {
        path: 'purchase',
        name: 'Purchase',
        component: () => import('../views/purchase/index.vue'),
        meta: { title: '采购管理' }
      },
      {
        path: 'inbound',
        name: 'Inbound',
        component: () => import('../views/inbound/index.vue'),
        meta: { title: '入库管理' }
      },
      {
        path: 'inventory',
        name: 'Inventory',
        component: () => import('../views/inventory/index.vue'),
        meta: { title: '库存管理' }
      },
      {
        path: 'inventory/transfer',
        name: 'InventoryTransfer',
        component: () => import('../views/inventory/transfer/index.vue'),
        meta: { title: '库存调拨' }
      },
      {
        path: 'inventory/check',
        name: 'InventoryCheck',
        component: () => import('../views/inventory/check/index.vue'),
        meta: { title: '库存盘点' }
      },
      {
        path: 'sales',
        name: 'Sales',
        component: () => import('../views/sales/index.vue'),
        meta: { title: '销售管理' }
      },
      {
        path: 'customer',
        name: 'Customer',
        component: () => import('../views/customer/index.vue'),
        meta: { title: '客户管理' }
      },
      {
        path: 'outbound',
        name: 'Outbound',
        component: () => import('../views/outbound/index.vue'),
        meta: { title: '出库管理' }
      },
      {
        path: 'production',
        name: 'Production',
        component: () => import('../views/production/index.vue'),
        meta: { title: '生产管理' }
      },
      {
        path: 'logistics',
        name: 'Logistics',
        component: () => import('../views/logistics/index.vue'),
        meta: { title: '物流管理' }
      },
      {
        path: 'report',
        name: 'Report',
        component: () => import('../views/report/index.vue'),
        meta: { title: '报表分析' }
      },
      {
        path: 'system/user',
        name: 'User',
        component: () => import('../views/system/user.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'system/role',
        name: 'Role',
        component: () => import('../views/system/role.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'system/permission',
        name: 'Permission',
        component: () => import('../views/system/permission.vue'),
        meta: { title: '权限管理' }
      },
      {
        path: 'system/dict',
        name: 'Dict',
        component: () => import('../views/system/dict.vue'),
        meta: { title: '数据字典' }
      },
      {
        path: 'system/inventory-warning',
        name: 'InventoryWarning',
        component: () => import('../views/system/inventory-warning.vue'),
        meta: { title: '库存预警' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/error/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
