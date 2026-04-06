import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi, getUserInfo } from '@/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username || ''
  },

  actions: {
    // 登录
    async login(loginForm) {
      const res = await loginApi(loginForm)
      this.token = res.data.token || res.data
      localStorage.setItem('token', this.token)
      return res
    },

    // 获取用户信息
    async getUserInfo() {
      const res = await getUserInfo()
      this.userInfo = res.data
      return res
    },

    // 登出
    async logout() {
      try {
        await logoutApi()
      } catch (error) {
        console.error('登出失败:', error)
      } finally {
        this.token = ''
        this.userInfo = null
        localStorage.removeItem('token')
      }
    }
  }
})
