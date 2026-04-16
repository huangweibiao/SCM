import request from '../utils/request'

// 获取出库单列表
export function getOutboundList(params) {
  return request({
    url: '/outbound',
    method: 'get',
    params
  })
}

// 获取出库单详情
export function getOutbound(id) {
  return request({
    url: `/outbound/${id}`,
    method: 'get'
  })
}

// 创建出库单
export function createOutbound(data) {
  return request({
    url: '/outbound',
    method: 'post',
    data
  })
}

// 确认出库
export function confirmOutbound(id) {
  return request({
    url: `/outbound/${id}/confirm`,
    method: 'post'
  })
}

// 获取出库单明细
export function getOutboundDetails(id) {
  return request({
    url: `/outbound/${id}/details`,
    method: 'get'
  })
}
