import request from '@/utils/request'

/**
 * 分页查询物流订单列表
 */
export function getLogisticsOrderPage(params) {
  return request({
    url: '/v1/logistics/order',
    method: 'get',
    params
  })
}

/**
 * 创建物流订单
 */
export function createLogisticsOrder(data) {
  return request({
    url: '/v1/logistics/order',
    method: 'post',
    data
  })
}

/**
 * 更新物流订单
 */
export function updateLogisticsOrder(id, data) {
  return request({
    url: `/v1/logistics/order/${id}`,
    method: 'put',
    data
  })
}

/**
 * 更新物流状态
 */
export function updateLogisticsStatus(id, status) {
  return request({
    url: `/v1/logistics/order/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 查询物流订单详情
 */
export function getLogisticsOrderDetail(id) {
  return request({
    url: `/v1/logistics/order/${id}`,
    method: 'get'
  })
}
