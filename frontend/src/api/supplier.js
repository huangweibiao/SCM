import request from '../utils/request'

// 获取供应商列表
export function getSupplierList(params) {
  return request({
    url: '/supplier',
    method: 'get',
    params
  })
}

// 获取所有供应商
export function getAllSuppliers() {
  return request({
    url: '/supplier/all',
    method: 'get'
  })
}

// 获取供应商详情
export function getSupplier(id) {
  return request({
    url: `/supplier/${id}`,
    method: 'get'
  })
}

// 创建供应商
export function createSupplier(data) {
  return request({
    url: '/supplier',
    method: 'post',
    data
  })
}

// 更新供应商
export function updateSupplier(id, data) {
  return request({
    url: `/supplier/${id}`,
    method: 'put',
    data
  })
}

// 删除供应商
export function deleteSupplier(id) {
  return request({
    url: `/supplier/${id}`,
    method: 'delete'
  })
}
