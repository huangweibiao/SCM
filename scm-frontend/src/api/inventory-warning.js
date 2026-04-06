import request from '@/utils/request'

/**
 * 手动触发库存预警检查
 */
export function checkInventoryWarning() {
  return request({
    url: '/v1/inventory-warning/check',
    method: 'post'
  })
}

/**
 * 获取低库存预警列表
 */
export function getLowStockWarnings() {
  return request({
    url: '/v1/inventory-warning/low',
    method: 'get'
  })
}

/**
 * 获取高库存预警列表
 */
export function getHighStockWarnings() {
  return request({
    url: '/v1/inventory-warning/high',
    method: 'get'
  })
}
