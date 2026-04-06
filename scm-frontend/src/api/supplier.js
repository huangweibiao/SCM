import request from '@/utils/request'

/**
 * 分页查询供应商列表
 */
export function getSupplierPage(params) {
  return request({
    url: '/v1/supplier',
    method: 'get',
    params
  })
}

/**
 * 创建供应商
 */
export function createSupplier(data) {
  return request({
    url: '/v1/supplier',
    method: 'post',
    data
  })
}

/**
 * 更新供应商
 */
export function updateSupplier(id, data) {
  return request({
    url: `/v1/supplier/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除供应商
 */
export function deleteSupplier(id) {
  return request({
    url: `/v1/supplier/${id}`,
    method: 'delete'
  })
}

/**
 * 查询供应商详情
 */
export function getSupplierDetail(id) {
  return request({
    url: `/v1/supplier/${id}`,
    method: 'get'
  })
}
