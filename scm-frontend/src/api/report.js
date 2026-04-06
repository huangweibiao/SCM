import request from '@/utils/request'

/**
 * 采购订单量统计
 */
export function getPurchaseOrderCountReport(params) {
  return request({
    url: '/v1/report/purchase/order-count',
    method: 'get',
    params
  })
}

/**
 * 采购金额统计
 */
export function getPurchaseAmountReport(params) {
  return request({
    url: '/v1/report/purchase/amount',
    method: 'get',
    params
  })
}

/**
 * 采购完成率统计
 */
export function getPurchaseCompletionReport(params) {
  return request({
    url: '/v1/report/purchase/completion',
    method: 'get',
    params
  })
}

/**
 * 销售订单量统计
 */
export function getSalesOrderCountReport(params) {
  return request({
    url: '/v1/report/sales/order-count',
    method: 'get',
    params
  })
}

/**
 * 销售金额统计
 */
export function getSalesAmountReport(params) {
  return request({
    url: '/v1/report/sales/amount',
    method: 'get',
    params
  })
}

/**
 * 销售发货率统计
 */
export function getSalesShipmentReport(params) {
  return request({
    url: '/v1/report/sales/shipment',
    method: 'get',
    params
  })
}

/**
 * 库存数量统计
 */
export function getInventoryCountReport(params) {
  return request({
    url: '/v1/report/inventory/count',
    method: 'get',
    params
  })
}

/**
 * 库存预警统计
 */
export function getInventoryWarnReport() {
  return request({
    url: '/v1/report/inventory/warn',
    method: 'get'
  })
}
