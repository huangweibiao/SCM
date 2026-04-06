import request from '@/utils/request'

/**
 * 分页查询入库单列表
 */
export function getInboundOrderPage(params) {
  return request({
    url: '/v1/inbound',
    method: 'get',
    params
  })
}

/**
 * 创建入库单
 */
export function createInboundOrder(data) {
  return request({
    url: '/v1/inbound',
    method: 'post',
    data
  })
}

/**
 * 确认入库单
 */
export function confirmInboundOrder(id) {
  return request({
    url: `/v1/inbound/${id}/confirm`,
    method: 'put'
  })
}

/**
 * 采购收货
 */
export function purchaseReceipt(poId, data) {
  return request({
    url: '/v1/inbound/receipt',
    method: 'post',
    params: { poId },
    data
  })
}

/**
 * 查询入库单详情
 */
export function getInboundOrderDetail(id) {
  return request({
    url: `/v1/inbound/${id}`,
    method: 'get'
  })
}
