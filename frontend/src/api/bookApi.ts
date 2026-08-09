import request from './request'
import type { Book, BookRequest } from '@/types/book'
export const bookApi = {
  list: () => request.get<any, Book[]>('/books'),
  get: (bookId: string) => request.get<any, Book>(`/books/${bookId}`),
  create: (data: BookRequest) => request.post<any, Book>('/books', data),
  update: (bookId: string, data: BookRequest) => request.put<any, Book>(`/books/${bookId}`, data),
  remove: (bookId: string) => request.delete(`/books/${bookId}`),
  setDefault: (bookId: string) => request.put(`/books/${bookId}/default`),
  uploadCover: (file: File) => {
    const fd = new FormData()
    fd.append('file', file)
    return request.post<any, string>('/books/cover', fd)
  }
}
