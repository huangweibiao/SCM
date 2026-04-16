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

// 库存调拨
export function transferInventory(data) {
  return request({
    url: '/inventory/transfer',
    method: 'post',
    data
  })
}

// 创建盘点单
export function createInventoryCheck(data) {
  return request({
    url: '/inventory/check',
    method: 'post',
    data
  })
}

// 提交盘点结果
export function submitCheckResult(id, data) {
  return request({
    url: `/inventory/check/${id}/result`,
    method: 'post',
    data
  })
}

// 查询盘点单列表
export function getCheckList(params) {
  return request({
    url: '/inventory/check/list',
    method: 'get',
    params
  })
}
