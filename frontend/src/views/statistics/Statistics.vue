<template>
  <div class="page">
    <div class="toolbar"><h2 style="margin-right:auto">统计分析</h2><el-date-picker v-model="month" type="month" value-format="YYYY-MM" @change="load" /></div>
    <div class="stat-grid"><el-card><div>收入</div><h2 class="money-income">¥{{ summary.income }}</h2></el-card><el-card><div>支出</div><h2 class="money-expense">¥{{ summary.expense }}</h2></el-card><el-card><div>结余</div><h2>¥{{ summary.balance }}</h2></el-card></div>
    <el-row :gutter="16"><el-col :xs="24" :md="12"><el-card><template #header>支出分类占比</template><div ref="pieRef" class="chart"></div></el-card></el-col><el-col :xs="24" :md="12"><el-card><template #header>每日趋势</template><div ref="lineRef" class="chart"></div></el-card></el-col></el-row>
  </div>
</template>
<script setup lang="ts">
import { onMounted, onBeforeUnmount, reactive, ref, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import * as echarts from 'echarts'
import { statisticsApi } from '@/api/statisticsApi'
const route=useRoute(); const bookId=String(route.params.bookId); const month=ref(new Date().toISOString().slice(0,7)); const summary=reactive({income:0,expense:0,balance:0}); const pieRef=ref<HTMLDivElement>(); const lineRef=ref<HTMLDivElement>()
let pieChart: echarts.ECharts | null = null
let lineChart: echarts.ECharts | null = null
function range(){ const [y,m]=month.value.split('-').map(Number); const last=new Date(y,m,0).getDate(); return {startDate:`${month.value}-01`,endDate:`${month.value}-${String(last).padStart(2,'0')}`} }
async function load(){
  Object.assign(summary, await statisticsApi.monthly(bookId, month.value)); const r=range(); const cats=await statisticsApi.category(bookId,{...r,type:'EXPENSE'}); const trend=await statisticsApi.dailyTrend(bookId,r); await nextTick();
  if(!pieChart && pieRef.value) pieChart = echarts.init(pieRef.value)
  if(!lineChart && lineRef.value) lineChart = echarts.init(lineRef.value)
  pieChart?.setOption({tooltip:{},series:[{type:'pie',radius:'65%',data:cats.map((c:any)=>({name:c.CATEGORYNAME||c.categoryName,value:c.TOTAL||c.total}))}]})
  lineChart?.setOption({tooltip:{trigger:'axis'},xAxis:{type:'category',data:[...new Set(trend.map((t:any)=>t.TRANSACTIONDATE||t.transactionDate))]},yAxis:{type:'value'},series:['INCOME','EXPENSE'].map(type=>({name:type,type:'line',data:trend.filter((t:any)=>(t.TYPE||t.type)===type).map((t:any)=>t.TOTAL||t.total)}))})
}
function onResize(){ pieChart?.resize(); lineChart?.resize() }
onMounted(() => { load(); window.addEventListener('resize', onResize) })
onBeforeUnmount(() => { window.removeEventListener('resize', onResize); pieChart?.dispose(); lineChart?.dispose() })
</script>
