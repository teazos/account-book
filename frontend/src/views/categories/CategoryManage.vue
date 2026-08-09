<template>
  <div class="page">
    <div class="toolbar"><h2 style="margin-right:auto">分类管理</h2><el-button type="primary" @click="open('EXPENSE')">新增支出分类</el-button><el-button type="success" @click="open('INCOME')">新增收入分类</el-button></div>
    <el-card style="margin-bottom:16px"><template #header>支出分类</template><el-table :data="store.expenseCategories"><el-table-column label="图标" prop="icon" width="80"/><el-table-column prop="name" label="名称"/><el-table-column prop="sortOrder" label="排序"/><el-table-column label="操作" width="150"><template #default="s"><el-button size="small" @click="edit(s.row)">编辑</el-button><el-popconfirm title="确认删除该分类？" @confirm="remove(s.row)"><template #reference><el-button size="small" type="danger">删除</el-button></template></el-popconfirm></template></el-table-column></el-table></el-card>
    <el-card><template #header>收入分类</template><el-table :data="store.incomeCategories"><el-table-column label="图标" prop="icon" width="80"/><el-table-column prop="name" label="名称"/><el-table-column prop="sortOrder" label="排序"/><el-table-column label="操作" width="150"><template #default="s"><el-button size="small" @click="edit(s.row)">编辑</el-button><el-popconfirm title="确认删除该分类？" @confirm="remove(s.row)"><template #reference><el-button size="small" type="danger">删除</el-button></template></el-popconfirm></template></el-table-column></el-table></el-card>
    <el-dialog v-model="dialog" :title="editingId ? '编辑分类' : '新增分类'" width="min(420px, 92vw)"><el-form :model="form" label-width="80px"><el-form-item label="类型"><el-select v-model="form.type"><el-option label="支出" value="EXPENSE"/><el-option label="收入" value="INCOME"/></el-select></el-form-item><el-form-item label="名称"><el-input v-model="form.name"/></el-form-item><el-form-item label="图标"><el-input v-model="form.icon"/></el-form-item><el-form-item label="颜色"><el-color-picker v-model="form.color"/></el-form-item><el-form-item label="排序"><el-input-number v-model="form.sortOrder"/></el-form-item></el-form><template #footer><el-button @click="dialog=false">取消</el-button><el-button type="primary" @click="submit">保存</el-button></template></el-dialog>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import { useCategoryStore } from '@/stores/categoryStore'
import type { Category, TransactionType } from '@/types/category'
const route=useRoute(); const store=useCategoryStore(); const bookId=String(route.params.bookId); const dialog=ref(false); const editingId=ref<string|null>(null); const form=reactive({name:'',type:'EXPENSE' as TransactionType,icon:'',color:'#409eff',sortOrder:0})
onMounted(()=>store.loadCategories(bookId))
function open(type:TransactionType){ editingId.value=null; Object.assign(form,{name:'',type,icon:'',color:'#409eff',sortOrder:0}); dialog.value=true }
function edit(c:Category){ editingId.value=c.id; Object.assign(form,{name:c.name,type:c.type,icon:c.icon||'',color:c.color||'#409eff',sortOrder:c.sortOrder||0}); dialog.value=true }
async function submit(){
  if(editingId.value){ await store.updateCategory(bookId, editingId.value, form); ElMessage.success('修改成功') }
  else { await store.createCategory(bookId, form); ElMessage.success('创建成功') }
  dialog.value=false
}
async function remove(c:Category){ await store.deleteCategory(bookId, c.id); ElMessage.success('删除成功') }
</script>
