import request from '../utils/request'

// 获取生产工单列表
export function getProductionOrderList(params) {
  return request({
    url: '/production',
    method: 'get',
    params
  })
}

// 获取生产工单详情
export function getProductionOrder(id) {
  return request({
    url: `/production/${id}`,
    method: 'get'
  })
}

// 创建生产工单
export function createProductionOrder(data) {
  return request({
    url: '/production',
    method: 'post',
    data
  })
}

// 更新生产工单
export function updateProductionOrder(id, data) {
  return request({
    url: `/production/${id}`,
    method: 'put',
    data
  })
}

// 开始生产
export function startProduction(id) {
  return request({
    url: `/production/${id}/start`,
    method: 'post'
  })
}

// 完工入库
export function finishProduction(id, data) {
  return request({
    url: `/production/${id}/finish`,
    method: 'post',
    data
  })
}

// 生产领料
export function pickMaterials(id, data) {
  return request({
    url: `/production/${id}/pick`,
    method: 'post',
    data
  })
}
