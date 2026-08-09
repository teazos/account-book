import { defineStore } from 'pinia'
import { categoryApi } from '@/api/categoryApi'
import type { Category, CategoryRequest } from '@/types/category'

export const useCategoryStore = defineStore('category', {
  state: () => ({ categories: [] as Category[] }),
  getters: {
    incomeCategories: state => state.categories.filter(c => c.type === 'INCOME'),
    expenseCategories: state => state.categories.filter(c => c.type === 'EXPENSE')
  },
  actions: {
    async loadCategories(bookId: string) { this.categories = await categoryApi.list(bookId) },
    async createCategory(bookId: string, data: CategoryRequest) { await categoryApi.create(bookId, data); await this.loadCategories(bookId) },
    async updateCategory(bookId: string, categoryId: string, data: CategoryRequest) { await categoryApi.update(bookId, categoryId, data); await this.loadCategories(bookId) },
    async deleteCategory(bookId: string, categoryId: string) { await categoryApi.remove(bookId, categoryId); await this.loadCategories(bookId) }
  }
})
