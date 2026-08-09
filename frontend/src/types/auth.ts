export interface User { id: string; email: string; nickname?: string }
export interface AuthResponse { token: string; user: User }
export interface RegisterRequest { email: string; password: string; nickname?: string }
export interface LoginRequest { email: string; password: string }
