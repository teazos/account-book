<template>
  <el-container style="min-height: 100vh">
    <el-aside class="desktop-aside" width="220px" style="background:#fff;border-right:1px solid #eee">
      <div style="padding:18px;font-weight:700">{{ bookStore.currentBook?.name || '账本' }}</div>
      <el-menu :default-active="$route.path" router @select="onMenuSelect">
        <el-menu-item v-for="m in menus" :key="m.index" :index="m.index">{{ m.label }}</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-button class="menu-btn" circle icon="Menu" @click="menuOpen=true" />
          <span class="header-title">{{ bookStore.currentBook?.name || '多账本记账应用' }}</span>
        </div>
        <div class="header-right">
          <el-select v-model="selected" class="book-select" @change="switchBook">
            <el-option v-for="b in bookStore.books" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
          <span class="user-name">{{ auth.user?.nickname || auth.user?.email }}</span>
          <el-button link type="danger" @click="logout">退出</el-button>
        </div>
      </el-header>
      <el-main><router-view /></el-main>
    </el-container>
    <el-drawer v-model="menuOpen" direction="ltr" size="min(220px, 72vw)" :with-header="false">
      <div style="padding:18px;font-weight:700">{{ bookStore.currentBook?.name || '账本' }}</div>
      <el-menu :default-active="$route.path" router @select="onMenuSelect">
        <el-menu-item v-for="m in menus" :key="m.index" :index="m.index">{{ m.label }}</el-menu-item>
      </el-menu>
    </el-drawer>
    <TransactionDrawer />
  </el-container>
</template>
<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import TransactionDrawer from '@/components/TransactionDrawer.vue'
import { useBookStore } from '@/stores/bookStore'
import { useAuthStore } from '@/stores/authStore'
import { useTransactionStore } from '@/stores/transactionStore'
const route = useRoute(); const router = useRouter(); const bookStore = useBookStore(); const auth = useAuthStore(); const transactions = useTransactionStore(); const selected = ref<string>(); const menuOpen = ref(false)
const base = computed(() => `/books/${route.params.bookId}`)
const menus = computed(() => [
  { index: `${base.value}/dashboard`, label: '看板' },
  { index: route.fullPath, label: '记一笔', action: () => transactions.openDrawer() },
  { index: `${base.value}/transactions`, label: '账目列表' },
  { index: `${base.value}/categories`, label: '分类管理' },
  { index: `${base.value}/statistics`, label: '统计分析' },
  { index: '/books', label: '返回账本' }
])
onMounted(async () => { await bookStore.loadBooks(); await bookStore.setCurrentBook(String(route.params.bookId)); selected.value = String(route.params.bookId) })
watch(() => route.params.bookId, async id => { if (id) { await bookStore.setCurrentBook(String(id)); selected.value = String(id) } })
function onMenuSelect(index: string) {
  menuOpen.value = false
  const m = menus.value.find(x => x.index === index)
  if (m?.action) m.action()
}
function switchBook(id: string) { router.push(`/books/${id}/dashboard`) }
function logout() { auth.logout(); router.push('/login') }
</script>
<style scoped>
.layout-header { background:#fff; display:flex; align-items:center; justify-content:space-between; gap:12px; }
.header-left, .header-right { display:flex; align-items:center; gap:12px; }
.header-title { white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
.book-select { width:220px; }
.menu-btn { display:none; }
@media (max-width:768px) {
  .desktop-aside { display:none; }
  .menu-btn { display:inline-flex; }
  .user-name { display:none; }
  .book-select { width:130px; }
  .header-title { max-width:40vw; }
}
</style>
