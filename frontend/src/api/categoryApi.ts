import request from './request'
import type { Category, CategoryRequest, TransactionType } from '@/types/category'
export const categoryApi = {
  list: (bookId: string, type?: TransactionType) => request.get<any, Category[]>(`/books/${bookId}/categories`, { params: { type } }),
  create: (bookId: string, data: CategoryRequest) => request.post<any, Category>(`/books/${bookId}/categories`, data),
  update: (bookId: string, categoryId: string, data: CategoryRequest) => request.put<any, Category>(`/books/${bookId}/categories/${categoryId}`, data),
  remove: (bookId: string, categoryId: string) => request.delete(`/books/${bookId}/categories/${categoryId}`)
}
