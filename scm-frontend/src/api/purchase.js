import request from '@/utils/request'

/**
 * 分页查询采购订单列表
 */
export function getPurchaseOrderPage(params) {
  return request({
    url: '/v1/purchase/order',
    method: 'get',
    params
  })
}

/**
 * 创建采购订单
 */
export function createPurchaseOrder(data) {
  return request({
    url: '/v1/purchase/order',
    method: 'post',
    data
  })
}

/**
 * 更新采购订单
 */
export function updatePurchaseOrder(id, data) {
  return request({
    url: `/v1/purchase/order/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除采购订单
 */
export function deletePurchaseOrder(id) {
  return request({
    url: `/v1/purchase/order/${id}`,
    method: 'delete'
  })
}

/**
 * 审核采购订单
 */
export function auditPurchaseOrder(id, auditBy) {
  return request({
    url: `/v1/purchase/order/${id}/audit`,
    method: 'put',
    params: { auditBy }
  })
}

/**
 * 关闭采购订单
 */
export function closePurchaseOrder(id) {
  return request({
    url: `/v1/purchase/order/${id}/close`,
    method: 'put'
  })
}

/**
 * 查询采购订单详情
 */
export function getPurchaseOrderDetail(id) {
  return request({
    url: `/v1/purchase/order/${id}`,
    method: 'get'
  })
}

/**
 * 查询采购订单明细列表
 */
export function getPurchaseOrderDetails(id) {
  return request({
    url: `/v1/purchase/order/${id}/details`,
    method: 'get'
  })
}
