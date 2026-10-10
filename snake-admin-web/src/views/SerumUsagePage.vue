<template>
  <el-card>
    <template #header>
      <div class="header-bar">
        <span>💉 血清使用规范管理</span>
        <div>
          <el-input v-model="keyword" placeholder="搜索血清名称/剂量" style="width:200px;margin-right:10px;" clearable @keyup.enter="load" />
          <el-select v-model="filterVenom" placeholder="蛇毒类型" style="width:130px;margin-right:10px;" clearable @change="load">
            <el-option value="neurotoxic" label="神经毒" />
            <el-option value="hemotoxic" label="血循毒" />
            <el-option value="cytotoxic" label="细胞毒" />
            <el-option value="mixed" label="混合毒" />
          </el-select>
          <el-button type="primary" :icon="Plus" @click="openForm()">新增血清规范</el-button>
        </div>
      </div>
    </template>

    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="serumName" label="血清名称" min-width="180" show-overflow-tooltip />
      <el-table-column label="毒型" width="120">
        <template #default="{ row }">
          <el-tag size="small" :type="venomTag(row.venomType)">{{ venomLabel(row.venomType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="dosage" label="剂量" width="120" show-overflow-tooltip />
      <el-table-column prop="dilution" label="稀释" width="160" show-overflow-tooltip />
      <el-table-column prop="route" label="途径" width="110" />
      <el-table-column prop="infusionRate" label="滴速" width="160" show-overflow-tooltip />
      <el-table-column label="皮试" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.skinTest" size="small" type="warning">必读</el-tag>
          <span v-else style="color:#999">—</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openForm(row)">编辑</el-button>
          <el-button size="small" type="danger" link @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top:16px;display:flex;justify-content:flex-end;">
      <el-pagination v-model:current-page="page.current" v-model:page-size="page.size"
        :page-sizes="[10,20,50]" :total="page.total" layout="total, sizes, prev, pager, next" @size-change="load" @current-change="load" />
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑血清规范' : '新增血清规范'" width="720px" top="5vh">
      <el-form :model="form" label-width="120px" label-position="right">
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="血清名称" required><el-input v-model="form.serumName" placeholder="如：抗眼镜蛇毒血清" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="蛇毒类型">
            <el-select v-model="form.venomType" style="width:100%">
              <el-option value="neurotoxic" label="神经毒" />
              <el-option value="hemotoxic" label="血循毒" />
              <el-option value="cytotoxic" label="细胞毒" />
              <el-option value="mixed" label="混合毒" />
            </el-select>
          </el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="常规剂量"><el-input v-model="form.dosage" placeholder="如：2-4 支" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="给药途径"><el-input v-model="form.route" placeholder="如：静脉滴注" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="稀释方式"><el-input v-model="form.dilution" placeholder="如：20-50ml 生理盐水稀释" /></el-form-item>
        <el-form-item label="滴速建议"><el-input v-model="form.infusionRate" placeholder="如：前30分钟 10-20滴/分" /></el-form-item>
        <el-form-item label="皮试要求"><el-input v-model="form.skinTest" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="过敏处理"><el-input v-model="form.allergyHandling" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="注意事项"><el-input v-model="form.precautions" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="参考依据"><el-input v-model="form.reference" placeholder="如：中国蛇伤急救指南 2023" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const API = '/api/admin/serum-usage'
const token = () => localStorage.getItem('snake_admin_token') || ''

const list = ref([])
const keyword = ref('')
const filterVenom = ref('')
const dialogVisible = ref(false)
const form = reactive({ id: null, serumName: '', venomType: '', dosage: '', dilution: '', route: '', infusionRate: '', skinTest: '', allergyHandling: '', precautions: '', reference: '' })
const page = reactive({ current: 1, size: 10, total: 0 })

const venomLabel = t => ({ neurotoxic: '神经毒', hemotoxic: '血循毒', cytotoxic: '细胞毒', mixed: '混合毒' }[t] || t || '未知')
const venomTag = t => ({ neurotoxic: 'info', hemotoxic: 'danger', cytotoxic: 'warning', mixed: 'success' }[t] || '')

async function load() {
  const qs = new URLSearchParams({ pageNum: page.current, pageSize: page.size })
  if (keyword.value) qs.set('keyword', keyword.value)
  if (filterVenom.value) qs.set('venomType', filterVenom.value)
  const res = await fetch(API + '/page?' + qs, { headers: { Authorization: 'Bearer ' + token() } })
  const j = await res.json()
  if (j.code === 200) {
    list.value = j.data.records || []
    page.total = j.data.total || 0
  }
}

function openForm(row = null) {
  Object.assign(form, { id: null, serumName: '', venomType: '', dosage: '', dilution: '', route: '', infusionRate: '', skinTest: '', allergyHandling: '', precautions: '', reference: '' })
  if (row) Object.assign(form, row)
  dialogVisible.value = true
}

async function save() {
  if (!form.serumName) { ElMessage.warning('请填写血清名称'); return }
  const res = await fetch(API + '/save', {
    method: 'POST', headers: { 'Content-Type': 'application/json', Authorization: 'Bearer ' + token() },
    body: JSON.stringify(form)
  })
  const j = await res.json()
  if (j.code === 200) { ElMessage.success('保存成功'); dialogVisible.value = false; load() }
  else ElMessage.error(j.msg || '保存失败')
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除《${row.serumName}》？`, '提示', { type: 'warning' })
  const res = await fetch(API + '/' + row.id, { method: 'DELETE', headers: { Authorization: 'Bearer ' + token() } })
  const j = await res.json()
  if (j.code === 200) { ElMessage.success('已删除'); load() }
  else ElMessage.error(j.msg || '删除失败')
}

onMounted(load)
</script>
<style scoped>.header-bar { display:flex; justify-content:space-between; align-items:center; font-size:18px; font-weight:600; }</style>
