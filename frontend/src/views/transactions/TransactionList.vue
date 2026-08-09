<template>
  <div class="page">
    <el-card>
      <template #header><div class="toolbar"><b>账目列表</b><el-button type="primary" style="margin-left:auto" @click="store.openDrawer()" >记一笔</el-button></div></template>
      <div class="toolbar"><el-date-picker class="filter-date" v-model="dates" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始日期" end-placeholder="结束日期"/><el-select v-model="filter.type" clearable placeholder="类型" style="width:120px"><el-option label="支出" value="EXPENSE"/><el-option label="收入" value="INCOME"/></el-select><el-button @click="load">查询</el-button></div>
      <div class="table-scroll"><el-table :data="store.records" v-loading="store.loading"><el-table-column prop="transactionDate" label="日期"/><el-table-column label="分类"><template #default="s">{{ s.row.categoryIcon }} {{ s.row.categoryName }}</template></el-table-column><el-table-column prop="note" label="备注"/><el-table-column label="类型"><template #default="s"><el-tag :type="s.row.type==='INCOME'?'success':'danger'">{{ s.row.type==='INCOME'?'收入':'支出' }}</el-tag></template></el-table-column><el-table-column label="金额"><template #default="s">¥{{ s.row.amount }}</template></el-table-column><el-table-column label="操作"><template #default="s"><el-popconfirm title="确认删除？" @confirm="remove(s.row.id)"><template #reference><el-button type="danger" link>删除</el-button></template></el-popconfirm></template></el-table-column></el-table></div>
      <el-pagination style="margin-top:16px" layout="prev, pager, next, total" :total="store.total" v-model:current-page="filter.page" :page-size="filter.size" @current-change="load" />
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { transactionApi } from '@/api/transactionApi'
import { useTransactionStore } from '@/stores/transactionStore'
const route=useRoute(); const bookId=String(route.params.bookId); const store=useTransactionStore(); const dates=ref<[string,string] | ''>(''); const filter=reactive({page:1,size:10,type:''})
async function load(){ await store.loadTransactions(bookId,{...filter,startDate:dates.value?.[0],endDate:dates.value?.[1]}) }
async function remove(id:string){ await transactionApi.remove(bookId,id); await load() }
watch(() => store.drawerVisible, (v, old) => { if (!v && old) load() })
onMounted(load)
</script>
<style scoped>
.filter-date { width: 360px; }
@media (max-width:768px) { .filter-date { width: 100%; } }
</style>
