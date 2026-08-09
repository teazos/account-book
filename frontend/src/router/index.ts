import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/books' },
    { path: '/login', component: () => import('@/views/auth/Login.vue') },
    { path: '/register', component: () => import('@/views/auth/Register.vue') },
    { path: '/books', component: () => import('@/views/books/BookList.vue') },
    {
      path: '/books/:bookId',
      component: () => import('@/layouts/MainLayout.vue'),
      children: [
        { path: '', redirect: to => `/books/${to.params.bookId}/dashboard` },
        { path: 'dashboard', component: () => import('@/views/dashboard/Dashboard.vue') },
        { path: 'categories', component: () => import('@/views/categories/CategoryManage.vue') },
        { path: 'transactions', component: () => import('@/views/transactions/TransactionList.vue') },
        { path: 'statistics', component: () => import('@/views/statistics/Statistics.vue') }
      ]
    }
  ]
})

router.beforeEach(to => {
  const token = localStorage.getItem('account_book_token')
  if (to.path.startsWith('/books') && !token) return { path: '/login', query: { redirect: to.fullPath } }
  if ((to.path === '/login' || to.path === '/register') && token) return '/books'
  return true
})

export default router
