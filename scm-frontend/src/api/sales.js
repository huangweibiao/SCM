import request from '@/utils/request'

/**
 * 分页查询销售订单列表
 */
export function getSalesOrderPage(params) {
  return request({
    url: '/v1/sales/order',
    method: 'get',
    params
  })
}

/**
 * 创建销售订单
 */
export function createSalesOrder(data) {
  return request({
    url: '/v1/sales/order',
    method: 'post',
    data
  })
}

/**
 * 更新销售订单
 */
export function updateSalesOrder(id, data) {
  return request({
    url: `/v1/sales/order/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除销售订单
 */
export function deleteSalesOrder(id) {
  return request({
    url: `/v1/sales/order/${id}`,
    method: 'delete'
  })
}

/**
 * 审核销售订单
 */
export function auditSalesOrder(id, auditBy) {
  return request({
    url: `/v1/sales/order/${id}/audit`,
    method: 'put',
    params: { auditBy }
  })
}

/**
 * 关闭销售订单
 */
export function closeSalesOrder(id) {
  return request({
    url: `/v1/sales/order/${id}/close`,
    method: 'put'
  })
}

/**
 * 查询销售订单详情
 */
export function getSalesOrderDetail(id) {
  return request({
    url: `/v1/sales/order/${id}`,
    method: 'get'
  })
}

/**
 * 查询销售订单明细列表
 */
export function getSalesOrderDetails(id) {
  return request({
    url: `/v1/sales/order/${id}/details`,
    method: 'get'
  })
}

/**
 * 销售发货
 */
export function salesShipment(soId, data) {
  return request({
    url: '/v1/sales/shipment',
    method: 'post',
    params: { soId },
    data
  })
}
