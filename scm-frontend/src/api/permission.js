import request from '@/utils/request'

/**
 * 查询权限树
 */
export function getPermissionTree() {
  return request({
    url: '/v1/permission/tree',
    method: 'get'
  })
}

/**
 * 创建权限
 */
export function createPermission(data) {
  return request({
    url: '/v1/permission',
    method: 'post',
    data
  })
}

/**
 * 更新权限
 */
export function updatePermission(id, data) {
  return request({
    url: `/v1/permission/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除权限
 */
export function deletePermission(id) {
  return request({
    url: `/v1/permission/${id}`,
    method: 'delete'
  })
}
