export type TransactionType = 'INCOME' | 'EXPENSE'
export interface Category { id: string; bookId: string; name: string; type: TransactionType; icon?: string; color?: string; sortOrder: number }
export interface CategoryRequest { name: string; type: TransactionType; icon?: string; color?: string; sortOrder?: number }
