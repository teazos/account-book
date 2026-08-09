<template>
  <div class="page">
    <div class="toolbar">
      <h2 style="margin-right:auto">我的账本</h2>
      <el-button type="primary" @click="openCreate">新建账本</el-button>
      <div class="user-info">
        <span class="nickname">{{ auth.user?.nickname || auth.user?.email }}</span>
        <el-tag size="small" type="info">共 {{ bookStore.books.length }} 个账本</el-tag>
        <el-button link type="danger" @click="logout">退出</el-button>
      </div>
    </div>
    <el-row :gutter="16">
      <el-col v-for="book in bookStore.books" :key="book.id" :xs="24" :sm="12" :md="8">
        <div class="book-card">
          <div class="book-banner" :style="bannerStyle(book)">
            <div class="book-banner-inner">
              <b class="book-name">{{ book.name }}</b>
              <el-tag v-if="book.isDefault" size="small" type="warning">默认</el-tag>
            </div>
          </div>
          <div class="book-body">
            <p class="book-desc">{{ book.description || '暂无描述' }}</p>
            <p class="book-currency">币种：{{ book.currency }}</p>
            <div class="book-actions">
              <el-button type="primary" size="small" @click="$router.push(`/books/${book.id}/dashboard`)">进入账本</el-button>
              <el-button size="small" @click="edit(book)">编辑</el-button>
              <el-button size="small" @click="setDefault(book.id)" :disabled="book.isDefault">设为默认</el-button>
              <el-popconfirm title="确认删除该账本？" @confirm="remove(book.id)"><template #reference><el-button size="small" type="danger" plain>删除</el-button></template></el-popconfirm>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-empty v-if="!bookStore.books.length" description="请先创建一个账本" />
    <el-drawer v-model="dialog" :title="editingId ? '编辑账本' : '新建账本'" direction="rtl" size="min(420px, 92vw)">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" /></el-form-item>
        <el-form-item label="币种"><el-input v-model="form.currency" /></el-form-item>
        <el-form-item label="默认"><el-switch v-model="form.isDefault" /></el-form-item>
        <el-form-item label="封面">
          <el-upload :show-file-list="false" :http-request="upload" :before-upload="beforeUpload" accept="image/*">
            <el-button>上传封面</el-button>
          </el-upload>
          <div v-if="form.cover" style="margin-top:8px;display:flex;align-items:center;gap:8px">
            <img :src="form.cover" style="width:120px;height:60px;object-fit:cover;border-radius:4px" />
            <el-button size="small" type="danger" link @click="form.cover=''">移除</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="dialog=false">取消</el-button><el-button type="primary" @click="submit">保存</el-button></template>
    </el-drawer>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { bookApi } from '@/api/bookApi'
import { useBookStore } from '@/stores/bookStore'
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'
import type { Book } from '@/types/book'
const bookStore = useBookStore(); const auth = useAuthStore(); const router = useRouter(); const dialog = ref(false); const editingId = ref<string | null>(null)
const form = reactive({ name: '', description: '', currency: 'CNY', isDefault: false, cover: '' })
onMounted(bookStore.loadBooks)
function openCreate(){ editingId.value=null; Object.assign(form,{name:'',description:'',currency:'CNY',isDefault:false,cover:''}); dialog.value=true }
function edit(book:Book){ editingId.value=book.id; Object.assign(form,{name:book.name,description:book.description||'',currency:book.currency,isDefault:book.isDefault,cover:book.cover||''}); dialog.value=true }
function beforeUpload(file:File){ if(file.size > 5*1024*1024){ ElMessage.warning('图片大小不能超过5MB'); return false } return true }
async function upload(options:any){ form.cover = await bookApi.uploadCover(options.file); ElMessage.success('封面上传成功') }
async function submit(){
  if(editingId.value){ await bookApi.update(editingId.value, form); ElMessage.success('修改成功') }
  else { await bookStore.createBook(form); ElMessage.success('创建成功') }
  dialog.value=false; await bookStore.loadBooks()
}
async function setDefault(id:string){ await bookApi.setDefault(id); await bookStore.loadBooks() }
async function remove(id:string){ await bookApi.remove(id); await bookStore.loadBooks() }
function logout(){ auth.logout(); router.push('/login') }
function bannerStyle(book:Book): Record<string, string> {
  if (book.cover) return { backgroundImage: `url(${book.cover})` }
  return { backgroundImage: 'linear-gradient(135deg, #409eff, #67c23a)' }
}
</script>
<style scoped>
.book-card { border:1px solid #e4e7ed; border-radius:8px; overflow:hidden; background:#fff; margin-bottom:16px; box-shadow:0 2px 8px rgba(0,0,0,0.05); aspect-ratio:1/1; display:flex; flex-direction:column; }
.book-banner { flex:1; background-size:cover; background-position:center; position:relative; }
.book-banner-inner { position:absolute; left:0; right:0; bottom:0; padding:10px 14px; display:flex; align-items:center; gap:8px; background:linear-gradient(to top, rgba(0,0,0,0.55), transparent); }
.book-name { color:#fff; font-size:16px; text-shadow:0 1px 3px rgba(0,0,0,0.3); }
.book-body { padding:12px; }
.book-desc { margin:0 0 6px; color:#606266; min-height:20px; }
.book-currency { margin:0 0 10px; color:#909399; font-size:13px; }
.book-actions { display:flex; gap:8px; flex-wrap:wrap; }
.user-info { display:flex; align-items:center; gap:8px; }
.nickname { color:#303133; font-weight:500; }
</style>
