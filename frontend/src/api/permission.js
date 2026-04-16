import request from '../utils/request'

// 获取权限列表
export function getPermissionList(params) {
  return request({
    url: '/system/permission',
    method: 'get',
    params
  })
}

// 获取权限树
export function getPermissionTree() {
  return request({
    url: '/system/permission/tree',
    method: 'get'
  })
}

// 获取权限详情
export function getPermission(id) {
  return request({
    url: `/system/permission/${id}`,
    method: 'get'
  })
}

// 创建权限
export function createPermission(data) {
  return request({
    url: '/system/permission',
    method: 'post',
    data
  })
}

// 更新权限
export function updatePermission(id, data) {
  return request({
    url: `/system/permission/${id}`,
    method: 'put',
    data
  })
}

// 删除权限
export function deletePermission(id) {
  return request({
    url: `/system/permission/${id}`,
    method: 'delete'
  })
}
