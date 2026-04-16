import request from '../utils/request'

// 获取库存列表
export function getInventoryList(params) {
  return request({
    url: '/inventory',
    method: 'get',
    params
  })
}

// 获取库存详情
export function getInventory(id) {
  return request({
    url: `/inventory/${id}`,
    method: 'get'
  })
}

// 获取库存预警
export function getInventoryWarnings() {
  return request({
    url: '/inventory/warnings',
    method: 'get'
  })
}
