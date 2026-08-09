import request from './request'
import type { PageResult, Transaction, TransactionRequest } from '@/types/transaction'
export const transactionApi = {
  page: (bookId: string, params: Record<string, any>) => request.get<any, PageResult<Transaction>>(`/books/${bookId}/transactions`, { params }),
  create: (bookId: string, data: TransactionRequest) => request.post<any, Transaction>(`/books/${bookId}/transactions`, data),
  update: (bookId: string, transactionId: string, data: TransactionRequest) => request.put<any, Transaction>(`/books/${bookId}/transactions/${transactionId}`, data),
  remove: (bookId: string, transactionId: string) => request.delete(`/books/${bookId}/transactions/${transactionId}`)
}
