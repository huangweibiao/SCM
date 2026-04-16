import request from '../utils/request'

// 获取销售订单列表
export function getSalesOrderList(params) {
  return request({
    url: '/sales',
    method: 'get',
    params
  })
}

// 获取销售订单详情
export function getSalesOrder(id) {
  return request({
    url: `/sales/${id}`,
    method: 'get'
  })
}

// 创建销售订单
export function createSalesOrder(data) {
  return request({
    url: '/sales',
    method: 'post',
    data
  })
}

// 更新销售订单
export function updateSalesOrder(id, data) {
  return request({
    url: `/sales/${id}`,
    method: 'put',
    data
  })
}

// 删除销售订单
export function deleteSalesOrder(id) {
  return request({
    url: `/sales/${id}`,
    method: 'delete'
  })
}

// 审核销售订单
export function auditSalesOrder(id, data) {
  return request({
    url: `/sales/${id}/audit`,
    method: 'post',
    data
  })
}

// 获取销售订单明细
export function getSalesOrderDetails(id) {
  return request({
    url: `/sales/${id}/details`,
    method: 'get'
  })
}

// 销售发货
export function deliverSalesOrder(id, data) {
  return request({
    url: `/sales/${id}/deliver`,
    method: 'post',
    data
  })
}
