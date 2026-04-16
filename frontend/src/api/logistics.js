import request from '../utils/request'

// 获取物流订单列表
export function getLogisticsOrderList(params) {
  return request({
    url: '/logistics',
    method: 'get',
    params
  })
}

// 获取物流订单详情
export function getLogisticsOrder(id) {
  return request({
    url: `/logistics/${id}`,
    method: 'get'
  })
}

// 创建物流订单
export function createLogisticsOrder(data) {
  return request({
    url: '/logistics',
    method: 'post',
    data
  })
}

// 更新物流订单
export function updateLogisticsOrder(id, data) {
  return request({
    url: `/logistics/${id}`,
    method: 'put',
    data
  })
}

// 删除物流订单
export function deleteLogisticsOrder(id) {
  return request({
    url: `/logistics/${id}`,
    method: 'delete'
  })
}

// 更新物流状态
export function updateLogisticsStatus(id, data) {
  return request({
    url: `/logistics/${id}/status`,
    method: 'post',
    data
  })
}
