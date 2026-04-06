import request from '@/utils/request'

/**
 * 分页查询库存列表
 */
export function getInventoryPage(params) {
  return request({
    url: '/v1/inventory',
    method: 'get',
    params
  })
}

/**
 * 查询库存详情
 */
export function getInventoryDetail(id) {
  return request({
    url: `/v1/inventory/${id}`,
    method: 'get'
  })
}
