import { defineStore } from 'pinia'
import { authApi } from '@/api/authApi'
import type { AuthResponse, LoginRequest, RegisterRequest, User } from '@/types/auth'

const TOKEN_KEY = 'account_book_token'
const USER_KEY = 'account_book_user'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    user: JSON.parse(localStorage.getItem(USER_KEY) || 'null') as User | null
  }),
  getters: { isLoggedIn: state => !!state.token },
  actions: {
    setSession(res: AuthResponse) {
      this.token = res.token
      this.user = res.user
      localStorage.setItem(TOKEN_KEY, res.token)
      localStorage.setItem(USER_KEY, JSON.stringify(res.user))
    },
    async login(data: LoginRequest) { this.setSession(await authApi.login(data)) },
    async register(data: RegisterRequest) { this.setSession(await authApi.register(data)) },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    }
  }
})
