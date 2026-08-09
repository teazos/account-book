import { defineStore } from 'pinia'
import { bookApi } from '@/api/bookApi'
import type { Book, BookRequest } from '@/types/book'

export const useBookStore = defineStore('book', {
  state: () => ({ books: [] as Book[], currentBook: null as Book | null }),
  getters: { currentBookId: state => state.currentBook?.id },
  actions: {
    async loadBooks() { this.books = await bookApi.list(); if (!this.currentBook && this.books.length) this.currentBook = this.books.find(b => b.isDefault) || this.books[0] },
    async setCurrentBook(bookId: string) { this.currentBook = await bookApi.get(bookId) },
    async createBook(data: BookRequest) { const book = await bookApi.create(data); await this.loadBooks(); this.currentBook = book; return book }
  }
})
