import request from '../utils/request'

// 获取入库单列表
export function getInboundList(params) {
  return request({
    url: '/inbound',
    method: 'get',
    params
  })
}

// 获取入库单详情
export function getInbound(id) {
  return request({
    url: `/inbound/${id}`,
    method: 'get'
  })
}

// 创建入库单
export function createInbound(data) {
  return request({
    url: '/inbound',
    method: 'post',
    data
  })
}

// 确认入库
export function confirmInbound(id) {
  return request({
    url: `/inbound/${id}/confirm`,
    method: 'post'
  })
}

// 获取入库单明细
export function getInboundDetails(id) {
  return request({
    url: `/inbound/${id}/details`,
    method: 'get'
  })
}
