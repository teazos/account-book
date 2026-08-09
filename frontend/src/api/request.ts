import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({ baseURL: import.meta.env.VITE_API_BASE_URL || '/api', timeout: 10000 })

request.interceptors.request.use(config => {
  const token = localStorage.getItem('account_book_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 0) throw new Error(res.message || '请求失败')
    return res.data
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('account_book_token')
      localStorage.removeItem('account_book_user')
      if (!location.pathname.startsWith('/login')) location.href = '/login'
      return Promise.reject(error)
    }
    const msg = error.response?.data?.message || error.message || '网络异常'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default request
