import request from '../utils/request'

// 用户登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 获取当前用户
export function getCurrentUser() {
  return request({
    url: '/auth/currentUser',
    method: 'get'
  })
}

// 用户登出
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

// 获取用户列表
export function getUserList(params) {
  return request({
    url: '/system/user',
    method: 'get',
    params
  })
}

// 获取用户详情
export function getUser(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'get'
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(id, data) {
  return request({
    url: `/system/user/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'delete'
  })
}

// 分配角色
export function assignRoles(id, data) {
  return request({
    url: `/system/user/${id}/roles`,
    method: 'post',
    data
  })
}
