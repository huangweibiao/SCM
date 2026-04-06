import request from '@/utils/request'

/**
 * 分页查询仓库列表
 */
export function getWarehousePage(params) {
  return request({
    url: '/v1/basic/warehouse',
    method: 'get',
    params
  })
}

/**
 * 创建仓库
 */
export function createWarehouse(data) {
  return request({
    url: '/v1/basic/warehouse',
    method: 'post',
    data
  })
}

/**
 * 更新仓库
 */
export function updateWarehouse(id, data) {
  return request({
    url: `/v1/basic/warehouse/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除仓库
 */
export function deleteWarehouse(id) {
  return request({
    url: `/v1/basic/warehouse/${id}`,
    method: 'delete'
  })
}
