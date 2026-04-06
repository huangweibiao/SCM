import request from '@/utils/request'

/**
 * 分页查询物料列表
 */
export function getItemPage(params) {
  return request({
    url: '/v1/basic/item',
    method: 'get',
    params
  })
}

/**
 * 创建物料
 */
export function createItem(data) {
  return request({
    url: '/v1/basic/item',
    method: 'post',
    data
  })
}

/**
 * 更新物料
 */
export function updateItem(id, data) {
  return request({
    url: `/v1/basic/item/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除物料
 */
export function deleteItem(id) {
  return request({
    url: `/v1/basic/item/${id}`,
    method: 'delete'
  })
}

/**
 * 查询物料详情
 */
export function getItemDetail(id) {
  return request({
    url: `/v1/basic/item/${id}`,
    method: 'get'
  })
}
