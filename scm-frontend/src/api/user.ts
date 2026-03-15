import { get, put, type ApiResponse } from './request'

export interface UserInfo {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  avatar: string
  status: number
}

export interface UserApi {
  getCurrentUser: () => Promise<ApiResponse<UserInfo>>
  updateProfile: (data: Partial<UserInfo>) => Promise<ApiResponse<UserInfo>>
  checkAuthStatus: () => Promise<ApiResponse<boolean>>
}

export const userApi: UserApi = {
  getCurrentUser() {
    return get<UserInfo>('/auth/me')
  },

  updateProfile(data) {
    return put<UserInfo>('/auth/profile', data)
  },

  checkAuthStatus() {
    return get<boolean>('/auth/status')
  },
}
