import type { TransactionType } from './category'
export interface Transaction { id: string; bookId: string; categoryId: string; categoryName?: string; categoryIcon?: string; type: TransactionType; amount: number; transactionDate: string; note?: string; createdAt: string }
export interface TransactionRequest { categoryId: string | null; type: TransactionType; amount: number; transactionDate: string; note?: string }
export interface PageResult<T> { records: T[]; total: number; page: number; size: number }
