import request from '@/utils/request'

/**
 * 分页查询生产工单列表
 */
export function getProductionOrderPage(params) {
  return request({
    url: '/v1/production/order',
    method: 'get',
    params
  })
}

/**
 * 创建生产工单
 */
export function createProductionOrder(data) {
  return request({
    url: '/v1/production/order',
    method: 'post',
    data
  })
}

/**
 * 更新生产工单
 */
export function updateProductionOrder(id, data) {
  return request({
    url: `/v1/production/order/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除生产工单
 */
export function deleteProductionOrder(id) {
  return request({
    url: `/v1/production/order/${id}`,
    method: 'delete'
  })
}

/**
 * 开始生产
 */
export function startProduction(id) {
  return request({
    url: `/v1/production/order/${id}/start`,
    method: 'put'
  })
}

/**
 * 完工确认
 */
export function completeProduction(id, finishedQty, warehouseId) {
  return request({
    url: `/v1/production/order/${id}/complete`,
    method: 'put',
    params: { finishedQty, warehouseId }
  })
}

/**
 * 查询生产工单详情
 */
export function getProductionOrderDetail(id) {
  return request({
    url: `/v1/production/order/${id}`,
    method: 'get'
  })
}
