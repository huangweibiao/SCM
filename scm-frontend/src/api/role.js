import request from '@/utils/request'

/**
 * 分页查询角色列表
 */
export function getRolePage(params) {
  return request({
    url: '/v1/permission/role',
    method: 'get',
    params
  })
}

/**
 * 创建角色
 */
export function createRole(data) {
  return request({
    url: '/v1/permission/role',
    method: 'post',
    data
  })
}

/**
 * 更新角色
 */
export function updateRole(id, data) {
  return request({
    url: `/v1/permission/role/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除角色
 */
export function deleteRole(id) {
  return request({
    url: `/v1/permission/role/${id}`,
    method: 'delete'
  })
}

/**
 * 查询角色的权限ID列表
 */
export function getRolePermissionIds(id) {
  return request({
    url: `/v1/permission/role/${id}/permissions`,
    method: 'get'
  })
}
