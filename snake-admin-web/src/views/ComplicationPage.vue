<template>
  <el-card>
    <template #header>
      <div class="header-bar">
        <span>🆘 并发症处理流程管理</span>
        <div>
          <el-input v-model="keyword" placeholder="搜索标题/触发条件" style="width:220px;margin-right:10px;" clearable @keyup.enter="load" />
          <el-select v-model="filterCat" placeholder="分类" style="width:130px;margin-right:10px;" clearable @change="load">
            <el-option value="hemotoxic" label="血循毒" />
            <el-option value="neurotoxic" label="神经毒" />
            <el-option value="cytotoxic" label="细胞毒" />
            <el-option value="general" label="综合" />
          </el-select>
          <el-button type="primary" :icon="Plus" @click="openForm()">新增流程</el-button>
        </div>
      </div>
    </template>

    <el-alert v-if="isContentAdmin" type="info" :closable="false" show-icon style="margin-bottom:16px;">
      CONTENT_ADMIN 可直接录入并发症流程，保存后即发布在 APP 前端显示。
    </el-alert>
    <el-alert v-if="isMedicalAdvisor" type="success" :closable="false" show-icon style="margin-bottom:16px;">
      MEDICAL_ADVISOR 可直接录入或编辑，也可点击"审核发布"按钮将草稿正式发布。
    </el-alert>

    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="流程标题" min-width="220" show-overflow-tooltip />
      <el-table-column label="分类" width="100">
        <template #default="{ row }">
          <el-tag size="small" :type="catTag(row.category)">{{ catLabel(row.category) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag size="small" :type="row.auditStatus === 2 ? 'success' : 'warning'">
            {{ row.auditStatus === 2 ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openForm(row)">编辑</el-button>
          <el-button v-if="isMedicalAdvisor && row.auditStatus !== 2" size="small" type="success" @click="audit(row)">审核发布</el-button>
          <el-button size="small" type="danger" link @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top:16px;display:flex;justify-content:flex-end;">
      <el-pagination v-model:current-page="page.current" v-model:page-size="page.size"
        :page-sizes="[10,20,50]" :total="page.total" layout="total, sizes, prev, pager, next" @size-change="load" @current-change="load" />
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑流程' : '新增流程'" width="760px" top="3vh">
      <el-form :model="form" label-width="120px" label-position="right">
        <el-form-item label="流程标题" required><el-input v-model="form.title" placeholder="如：DIC 处理流程" /></el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="form.category" style="width:100%">
            <el-option value="hemotoxic" label="血循毒" />
            <el-option value="neurotoxic" label="神经毒" />
            <el-option value="cytotoxic" label="细胞毒" />
            <el-option value="general" label="综合" />
          </el-select>
        </el-form-item>
        <el-form-item label="触发条件"><el-input v-model="form.triggers" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="临床表现"><el-input v-model="form.symptoms" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="诊断要点"><el-input v-model="form.diagnosis" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="处理步骤（Markdown/换行）"><el-input v-model="form.treatmentSteps" type="textarea" :rows="5" /></el-form-item>
        <el-form-item label="支持治疗"><el-input v-model="form.supportiveCare" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="会诊建议"><el-input v-model="form.consultation" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="参考依据"><el-input v-model="form.reference" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const API = '/api/admin/complication'
const token = () => localStorage.getItem('snake_admin_token') || ''
const role = computed(() => {
  try { return (JSON.parse(localStorage.getItem('snake_admin_user') || '{}').role_type || '').toString().toUpperCase() } catch { return '' }
})
const isContentAdmin = computed(() => role.value === 'CONTENT_ADMIN')
const isMedicalAdvisor = computed(() => role.value === 'MEDICAL_ADVISOR')

const list = ref([])
const keyword = ref('')
const filterCat = ref('')
const dialogVisible = ref(false)
const form = reactive({ id: null, title: '', category: '', triggers: '', symptoms: '', diagnosis: '', treatmentSteps: '', supportiveCare: '', consultation: '', reference: '' })
const page = reactive({ current: 1, size: 10, total: 0 })

const catLabel = c => ({ hemotoxic: '血循毒', neurotoxic: '神经毒', cytotoxic: '细胞毒', general: '综合' }[c] || c || '—')
const catTag = c => ({ hemotoxic: 'danger', neurotoxic: 'info', cytotoxic: 'warning', general: 'success' }[c] || '')

async function load() {
  const qs = new URLSearchParams({ pageNum: page.current, pageSize: page.size })
  if (keyword.value) qs.set('keyword', keyword.value)
  if (filterCat.value) qs.set('category', filterCat.value)
  const res = await fetch(API + '/page?' + qs, { headers: { Authorization: 'Bearer ' + token() } })
  const j = await res.json()
  if (j.code === 200) { list.value = j.data.records || []; page.total = j.data.total || 0 }
}
function openForm(row = null) {
  Object.assign(form, { id: null, title: '', category: '', triggers: '', symptoms: '', diagnosis: '', treatmentSteps: '', supportiveCare: '', consultation: '', reference: '' })
  if (row) Object.assign(form, row)
  dialogVisible.value = true
}
async function save() {
  if (!form.title) { ElMessage.warning('请填写标题'); return }
  const res = await fetch(API + '/save', { method: 'POST', headers: { 'Content-Type': 'application/json', Authorization: 'Bearer ' + token() }, body: JSON.stringify(form) })
  const j = await res.json()
  if (j.code === 200) { ElMessage.success('保存成功'); dialogVisible.value = false; load() }
  else ElMessage.error(j.msg || '保存失败')
}
async function audit(row) {
  await ElMessageBox.confirm(`确认发布《${row.title}》？发布后 APP 端可见。`, '审核发布', { type: 'success' })
  const res = await fetch(API + '/' + row.id + '/audit', { method: 'POST', headers: { Authorization: 'Bearer ' + token() } })
  const j = await res.json()
  if (j.code === 200) { ElMessage.success('已发布'); load() }
  else ElMessage.error(j.msg || '发布失败')
}
async function remove(row) {
  await ElMessageBox.confirm(`确定删除《${row.title}》？`, '提示', { type: 'warning' })
  const res = await fetch(API + '/' + row.id, { method: 'DELETE', headers: { Authorization: 'Bearer ' + token() } })
  const j = await res.json()
  if (j.code === 200) { ElMessage.success('已删除'); load() }
  else ElMessage.error(j.msg || '删除失败')
}
onMounted(load)
</script>
<style scoped>.header-bar { display:flex; justify-content:space-between; align-items:center; font-size:18px; font-weight:600; }</style>
