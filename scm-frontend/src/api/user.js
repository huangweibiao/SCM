import request from '@/utils/request'

/**
 * 分页查询用户列表
 */
export function getUserPage(params) {
  return request({
    url: '/v1/permission/user',
    method: 'get',
    params
  })
}

/**
 * 创建用户
 */
export function createUser(data) {
  return request({
    url: '/v1/permission/user',
    method: 'post',
    data
  })
}

/**
 * 更新用户
 */
export function updateUser(id, data) {
  return request({
    url: `/v1/permission/user/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除用户
 */
export function deleteUser(id) {
  return request({
    url: `/v1/permission/user/${id}`,
    method: 'delete'
  })
}

/**
 * 重置密码
 */
export function resetPassword(id, newPassword) {
  return request({
    url: `/v1/permission/user/${id}/password`,
    method: 'put',
    params: { newPassword }
  })
}
