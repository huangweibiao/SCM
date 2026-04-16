import request from '../utils/request'

// 获取仓库列表
export function getWarehouseList(params) {
  return request({
    url: '/basic/warehouse',
    method: 'get',
    params
  })
}

// 获取所有仓库
export function getAllWarehouses() {
  return request({
    url: '/basic/warehouse/all',
    method: 'get'
  })
}

// 获取仓库详情
export function getWarehouse(id) {
  return request({
    url: `/basic/warehouse/${id}`,
    method: 'get'
  })
}

// 创建仓库
export function createWarehouse(data) {
  return request({
    url: '/basic/warehouse',
    method: 'post',
    data
  })
}

// 更新仓库
export function updateWarehouse(id, data) {
  return request({
    url: `/basic/warehouse/${id}`,
    method: 'put',
    data
  })
}

// 删除仓库
export function deleteWarehouse(id) {
  return request({
    url: `/basic/warehouse/${id}`,
    method: 'delete'
  })
}
