import request from '@/utils/request'

/**
 * 用户登录
 */
export function login(data) {
  return request({
    url: '/v1/auth/login',
    method: 'post',
    data
  })
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return request({
    url: '/v1/auth/userinfo',
    method: 'get'
  })
}

/**
 * 用户登出
 */
export function logout() {
  return request({
    url: '/v1/auth/logout',
    method: 'post'
  })
}

/**
 * 健康检查
 */
export function health() {
  return request({
    url: '/health',
    method: 'get'
  })
}
