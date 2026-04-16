import request from '../utils/request'

// 获取采购订单列表
export function getPurchaseOrderList(params) {
  return request({
    url: '/purchase',
    method: 'get',
    params
  })
}

// 获取采购订单详情
export function getPurchaseOrder(id) {
  return request({
    url: `/purchase/${id}`,
    method: 'get'
  })
}

// 创建采购订单
export function createPurchaseOrder(data) {
  return request({
    url: '/purchase',
    method: 'post',
    data
  })
}

// 更新采购订单
export function updatePurchaseOrder(id, data) {
  return request({
    url: `/purchase/${id}`,
    method: 'put',
    data
  })
}

// 删除采购订单
export function deletePurchaseOrder(id) {
  return request({
    url: `/purchase/${id}`,
    method: 'delete'
  })
}

// 审核采购订单
export function auditPurchaseOrder(id, data) {
  return request({
    url: `/purchase/${id}/audit`,
    method: 'post',
    data
  })
}

// 获取采购订单明细
export function getPurchaseOrderDetails(id) {
  return request({
    url: `/purchase/${id}/details`,
    method: 'get'
  })
}
