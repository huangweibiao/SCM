import request from '../utils/request'

// 获取物料列表
export function getItemList(params) {
  return request({
    url: '/basic/item',
    method: 'get',
    params
  })
}

// 获取所有物料
export function getAllItems() {
  return request({
    url: '/basic/item/all',
    method: 'get'
  })
}

// 获取物料详情
export function getItem(id) {
  return request({
    url: `/basic/item/${id}`,
    method: 'get'
  })
}

// 创建物料
export function createItem(data) {
  return request({
    url: '/basic/item',
    method: 'post',
    data
  })
}

// 更新物料
export function updateItem(id, data) {
  return request({
    url: `/basic/item/${id}`,
    method: 'put',
    data
  })
}

// 删除物料
export function deleteItem(id) {
  return request({
    url: `/basic/item/${id}`,
    method: 'delete'
  })
}

// 获取物料分类列表
export function getItemCategoryList(params) {
  return request({
    url: '/basic/itemCategory',
    method: 'get',
    params
  })
}

// 获取物料分类树
export function getItemCategoryTree() {
  return request({
    url: '/basic/itemCategory/tree',
    method: 'get'
  })
}
