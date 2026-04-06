import request from '@/utils/request'

/**
 * 分页查询出库单列表
 */
export function getOutboundOrderPage(params) {
  return request({
    url: '/v1/outbound',
    method: 'get',
    params
  })
}

/**
 * 创建出库单
 */
export function createOutboundOrder(data) {
  return request({
    url: '/v1/outbound',
    method: 'post',
    data
  })
}

/**
 * 确认出库单
 */
export function confirmOutboundOrder(id) {
  return request({
    url: `/v1/outbound/${id}/confirm`,
    method: 'put'
  })
}

/**
 * 查询出库单详情
 */
export function getOutboundOrderDetail(id) {
  return request({
    url: `/v1/outbound/${id}`,
    method: 'get'
  })
}
