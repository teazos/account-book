export interface Book { id: string; userId: string; name: string; description?: string; currency: string; cover?: string; isDefault: boolean; createdAt: string; updatedAt: string }
export interface BookRequest { name: string; description?: string; currency?: string; cover?: string; isDefault?: boolean }
