import request from './request'
import type { AuthResponse, LoginRequest, RegisterRequest } from '@/types/auth'
export const authApi = {
  register: (data: RegisterRequest) => request.post<any, AuthResponse>('/auth/register', data),
  login: (data: LoginRequest) => request.post<any, AuthResponse>('/auth/login', data)
}
