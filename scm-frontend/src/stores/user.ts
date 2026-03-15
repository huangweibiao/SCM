import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi, type UserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  // State
  const user = ref<UserInfo | null>(null)
  const loading = ref(false)

  // Getters
  const isLoggedIn = computed(() => !!user.value)
  const username = computed(() => user.value?.username || '')
  const nickname = computed(() => user.value?.nickname || user.value?.username || '')
  const avatar = computed(() => user.value?.avatar || '')

  // Actions
  async function fetchCurrentUser() {
    loading.value = true
    try {
      const response = await userApi.getCurrentUser()
      if (response.success && response.data) {
        user.value = response.data
      } else {
        user.value = null
      }
    } catch (error) {
      user.value = null
      throw error
    } finally {
      loading.value = false
    }
  }

  async function updateProfile(data: Partial<UserInfo>) {
    loading.value = true
    try {
      const response = await userApi.updateProfile(data)
      if (response.success && response.data) {
        user.value = response.data
        return true
      }
      return false
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  function logout() {
    user.value = null
    // Redirect to OAuth2 logout or just redirect to login
    window.location.href = '/api/auth/logout'
  }

  function $reset() {
    user.value = null
    loading.value = false
  }

  return {
    // State
    user,
    loading,
    // Getters
    isLoggedIn,
    username,
    nickname,
    avatar,
    // Actions
    fetchCurrentUser,
    updateProfile,
    logout,
    $reset,
  }
})
