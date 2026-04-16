import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '../utils/request'

interface UserInfo {
  id: number
  username: string
  realName: string
  phone: string
  email: string
}

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  const login = async (username: string, password: string) => {
    const res = await request.post('/auth/login', { username, password })
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    userInfo.value = {
      id: res.data.userId,
      username: res.data.username,
      realName: res.data.realName,
      phone: '',
      email: ''
    }
    return res.data
  }

  const getUserInfo = async () => {
    const res = await request.get('/auth/currentUser')
    userInfo.value = res.data
    return res.data
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return {
    token,
    userInfo,
    login,
    getUserInfo,
    logout
  }
})
