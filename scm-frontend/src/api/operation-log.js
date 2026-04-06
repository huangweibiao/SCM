import request from '@/utils/request'

/**
 * 分页查询操作日志列表
 */
export function getOperationLogPage(params) {
  return request({
    url: '/v1/operation-log',
    method: 'get',
    params
  })
}

/**
 * 查询操作日志详情
 */
export function getOperationLogDetail(id) {
  return request({
    url: `/v1/operation-log/${id}`,
    method: 'get'
  })
}

/**
 * 删除操作日志
 */
export function deleteOperationLog(id) {
  return request({
    url: `/v1/operation-log/${id}`,
    method: 'delete'
  })
}
