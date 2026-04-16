import request from '../utils/request'

// 获取角色列表
export function getRoleList(params) {
  return request({
    url: '/system/role',
    method: 'get',
    params
  })
}

// 获取角色详情
export function getRole(id) {
  return request({
    url: `/system/role/${id}`,
    method: 'get'
  })
}

// 创建角色
export function createRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(id, data) {
  return request({
    url: `/system/role/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/system/role/${id}`,
    method: 'delete'
  })
}

// 获取角色权限
export function getRolePermissions(id) {
  return request({
    url: `/system/role/${id}/permissions`,
    method: 'get'
  })
}

// 分配权限
export function assignPermissions(id, data) {
  return request({
    url: `/system/role/${id}/permissions`,
    method: 'post',
    data
  })
}
