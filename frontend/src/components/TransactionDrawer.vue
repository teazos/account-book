<template>
  <el-drawer v-model="visible" title="记一笔" direction="rtl" size="min(420px, 92vw)">
    <el-form :model="form" label-width="80px">
      <el-form-item label="类型"><el-radio-group v-model="form.type" @change="form.categoryId=null"><el-radio-button label="EXPENSE">支出</el-radio-button><el-radio-button label="INCOME">收入</el-radio-button></el-radio-group></el-form-item>
      <el-form-item label="金额"><el-input-number v-model="form.amount" :min="0.01" :precision="2" style="width:100%" /></el-form-item>
      <el-form-item label="分类"><el-select v-model="form.categoryId" placeholder="选择分类" style="width:100%"><el-option v-for="c in availableCategories" :key="c.id" :label="`${c.icon || ''} ${c.name}`" :value="c.id"/></el-select></el-form-item>
      <el-form-item label="日期"><el-date-picker v-model="form.transactionDate" value-format="YYYY-MM-DD" type="date" style="width:100%"/></el-form-item>
      <el-form-item label="备注"><el-input v-model="form.note" type="textarea" :rows="3" /></el-form-item>
      <el-form-item><el-button type="primary" @click="submit">保存</el-button><el-button @click="close">取消</el-button></el-form-item>
    </el-form>
  </el-drawer>
</template>
<script setup lang="ts">
import { computed, reactive, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useCategoryStore } from '@/stores/categoryStore'
import { useTransactionStore } from '@/stores/transactionStore'
const route = useRoute(); const categories = useCategoryStore(); const transactions = useTransactionStore()
const bookId = computed(() => String(route.params.bookId))
const visible = computed({
  get: () => transactions.drawerVisible,
  set: (v: boolean) => { transactions.drawerVisible = v }
})
const form = reactive({ type: 'EXPENSE' as 'EXPENSE'|'INCOME', amount: 0, categoryId: null as string|null, transactionDate: new Date().toISOString().slice(0,10), note: '' })
const availableCategories = computed(() => categories.categories.filter(c => c.type === form.type))
watch(visible, v => { if (v) { categories.loadCategories(bookId.value); reset() } })
function reset(){ Object.assign(form,{ type:'EXPENSE', amount:0, categoryId:null, transactionDate:new Date().toISOString().slice(0,10), note:'' }) }
async function submit(){
  if(!form.categoryId){ ElMessage.warning('请选择分类'); return }
  if(!form.amount || form.amount <= 0){ ElMessage.warning('请输入金额'); return }
  await transactions.createTransaction(bookId.value, form)
  ElMessage.success('保存成功')
  transactions.closeDrawer()
  reset()
}
function close(){ transactions.closeDrawer(); reset() }
</script>
