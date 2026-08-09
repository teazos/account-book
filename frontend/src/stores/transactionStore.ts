import { defineStore } from 'pinia'
import { transactionApi } from '@/api/transactionApi'
import type { Transaction, TransactionRequest } from '@/types/transaction'

export const useTransactionStore = defineStore('transaction', {
  state: () => ({ records: [] as Transaction[], total: 0, loading: false, drawerVisible: false }),
  actions: {
    openDrawer() { this.drawerVisible = true },
    closeDrawer() { this.drawerVisible = false },
    async loadTransactions(bookId: string, params: Record<string, any> = {}) {
      this.loading = true
      try { const page = await transactionApi.page(bookId, params); this.records = page.records; this.total = page.total }
      finally { this.loading = false }
    },
    async createTransaction(bookId: string, data: TransactionRequest) { await transactionApi.create(bookId, data) }
  }
})
