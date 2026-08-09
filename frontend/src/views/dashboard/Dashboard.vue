<template>
  <div class="page">
    <div class="stat-grid">
      <el-card><div>本月收入</div><h2 class="money-income">¥{{ summary.income || 0 }}</h2></el-card>
      <el-card><div>本月支出</div><h2 class="money-expense">¥{{ summary.expense || 0 }}</h2></el-card>
      <el-card><div>本月结余</div><h2>¥{{ summary.balance || 0 }}</h2></el-card>
    </div>
    <el-card><template #header>最近账目</template><div class="table-scroll"><el-table :data="transactions.records" v-loading="transactions.loading"><el-table-column prop="transactionDate" label="日期"/><el-table-column label="分类"><template #default="s">{{ s.row.categoryIcon }} {{ s.row.categoryName }}</template></el-table-column><el-table-column prop="note" label="备注"/><el-table-column label="金额"><template #default="s"><span :class="s.row.type==='INCOME'?'money-income':'money-expense'">{{ s.row.type==='INCOME'?'+':'-' }}¥{{ s.row.amount }}</span></template></el-table-column></el-table></div></el-card>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, watch } from 'vue'
import { useRoute } from 'vue-router'
import { statisticsApi } from '@/api/statisticsApi'
import { useTransactionStore } from '@/stores/transactionStore'
const route = useRoute(); const transactions = useTransactionStore(); const summary = reactive({ income:0, expense:0, balance:0 })
function month(){ return new Date().toISOString().slice(0,7) }
async function load(){ const bookId=String(route.params.bookId); Object.assign(summary, await statisticsApi.monthly(bookId, month())); await transactions.loadTransactions(bookId,{page:1,size:8}) }
watch(() => transactions.drawerVisible, (v, old) => { if (!v && old) load() })
onMounted(load)
</script>
