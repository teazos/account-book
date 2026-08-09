import request from './request'
import type { MonthlySummary } from '@/types/statistics'
export const statisticsApi = {
  monthly: (bookId: string, month: string) => request.get<any, MonthlySummary>(`/books/${bookId}/statistics/monthly`, { params: { month } }),
  category: (bookId: string, params: Record<string, any>) => request.get<any, any[]>(`/books/${bookId}/statistics/category`, { params }),
  dailyTrend: (bookId: string, params: Record<string, any>) => request.get<any, any[]>(`/books/${bookId}/statistics/daily-trend`, { params }),
  yearly: (bookId: string, year: number) => request.get<any, any[]>(`/books/${bookId}/statistics/yearly`, { params: { year } })
}
