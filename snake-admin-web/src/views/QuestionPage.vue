<template>
  <el-card>
    <template #header>
      <div class="header-bar">
        <span>📚 题库管理</span>
        <div>
          <el-input v-model="keyword" placeholder="搜索题目" style="width:200px;margin-right:10px;" clearable @keyup.enter="load" />
          <el-select v-model="filterCategory" placeholder="类别" style="width:140px;margin-right:10px;" clearable @change="load">
            <el-option value="FIRST_AID" label="急救知识" />
            <el-option value="SNAKE" label="蛇种识别" />
            <el-option value="HOSPITAL" label="医院常识" />
            <el-option value="LEARN" label="测一测(四步学)" />
            <el-option value="PRACTICE" label="练一练(四步学)" />
          </el-select>
          <el-select v-model="filterType" placeholder="题型" style="width:120px;margin-right:10px;" clearable @change="load">
            <el-option :value="1" label="单选题" />
            <el-option :value="2" label="多选题" />
            <el-option :value="3" label="判断题" />
          </el-select>
          <el-select v-model="filterStatus" placeholder="状态" style="width:100px;margin-right:10px;" clearable @change="load">
            <el-option :value="1" label="启用" />
            <el-option :value="0" label="禁用" />
          </el-select>
          <el-button type="primary" :icon="Plus" @click="openForm()">新增题目</el-button>
        </div>
      </div>
    </template>

    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="类别" width="100">
        <template #default="{ row }">
          <el-tag :type="row.category === 'FIRST_AID' ? 'primary' : row.category === 'SNAKE' ? 'success' : 'warning'" size="small">
            {{ categoryMap[row.category] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="题型" width="80">
        <template #default="{ row }">
          <el-tag size="small">{{ typeMap[row.questionType] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="question" label="题目内容" show-overflow-tooltip min-width="280" />
      <el-table-column label="答案" width="100">
        <template #default="{ row }">
          <el-tag type="danger" size="small" effect="plain">{{ row.correctAnswer }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="score" label="分值" width="60" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openForm(row)">编辑</el-button>
          <el-button size="small" type="danger" link @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top:16px;display:flex;justify-content:flex-end;">
      <el-pagination
        v-model:current-page="page.current"
        v-model:page-size="page.size"
        :total="page.total"
        :page-sizes="[10,20,50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="load" @current-change="load" />
    </div>
  </el-card>

  <!-- 新增/编辑弹窗 -->
  <el-dialog v-model="dialogVisible" :title="form.id ? '编辑题目' : '新增题目'" width="640px">
    <el-form :model="form" label-width="80px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="类别">
            <el-select v-model="form.category" style="width:100%">
              <el-option value="FIRST_AID" label="急救知识" />
              <el-option value="SNAKE" label="蛇种识别" />
              <el-option value="HOSPITAL" label="医院常识" />
              <el-option value="LEARN" label="测一测(四步学)" />
              <el-option value="PRACTICE" label="练一练(四步学)" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="题型">
            <el-select v-model="form.questionType" style="width:100%" @change="onTypeChange">
              <el-option :value="1" label="单选题" />
              <el-option :value="2" label="多选题" />
              <el-option :value="3" label="判断题" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="分值">
        <el-input-number v-model="form.score" :min="1" :max="100" />
      </el-form-item>
      <el-form-item label="题目">
        <el-input v-model="form.question" type="textarea" :rows="2" placeholder="请输入题目内容" />
      </el-form-item>
      <template v-if="form.questionType === 3">
        <!-- 判断题 -->
        <el-form-item label="选项">
          <el-input v-model="form.optionA" placeholder="正确" style="width:200px;margin-right:10px;" />
          <el-input v-model="form.optionB" placeholder="错误" style="width:200px;" />
        </el-form-item>
      </template>
      <template v-else>
        <el-form-item label="选项A">
          <el-input v-model="form.optionA" />
        </el-form-item>
        <el-form-item label="选项B">
          <el-input v-model="form.optionB" />
        </el-form-item>
        <el-form-item v-if="form.questionType === 1 || form.questionType === 2" label="选项C">
          <el-input v-model="form.optionC" />
        </el-form-item>
        <el-form-item v-if="form.questionType === 1 || form.questionType === 2" label="选项D">
          <el-input v-model="form.optionD" />
        </el-form-item>
      </template>
      <el-form-item label="正确答案">
        <template v-if="form.questionType === 1 || form.questionType === 3">
          <el-radio-group v-model="form.correctAnswer">
            <el-radio value="A">A</el-radio>
            <el-radio value="B">B</el-radio>
            <el-radio v-if="form.questionType !== 3" value="C">C</el-radio>
            <el-radio v-if="form.questionType !== 3" value="D">D</el-radio>
          </el-radio-group>
        </template>
        <template v-else>
          <el-checkbox-group v-model="correctAnswers">
            <el-checkbox value="A">A</el-checkbox>
            <el-checkbox value="B">B</el-checkbox>
            <el-checkbox value="C">C</el-checkbox>
            <el-checkbox value="D">D</el-checkbox>
          </el-checkbox-group>
        </template>
      </el-form-item>
      <el-form-item label="解析">
        <el-input v-model="form.analysis" type="textarea" :rows="3" placeholder="答案解析（对用户可见）" />
      </el-form-item>
      <el-form-item label="状态">
        <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="save">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '../utils/request'

const categoryMap = { FIRST_AID: '急救知识', SNAKE: '蛇种识别', HOSPITAL: '医院常识', LEARN: '测一测(四步学)', PRACTICE: '练一练(四步学)' }
const typeMap = { 1: '单选', 2: '多选', 3: '判断' }

const keyword = ref('')
const filterCategory = ref('')
const filterType = ref(null)
const filterStatus = ref(null)
const list = ref([])
const page = reactive({ current: 1, size: 10, total: 0 })

const dialogVisible = ref(false)
const form = ref({})
const correctAnswers = ref([])

async function load() {
  const params = {
    current: page.current, size: page.size,
    category: filterCategory.value || undefined,
    questionType: filterType.value,
    status: filterStatus.value,
    keyword: keyword.value || undefined,
  }
  const data = await request.get('/admin/question/page', { params })
  list.value = data.records
  page.total = data.total
}

function openForm(row) {
  if (row) {
    form.value = { ...row }
    if (row.questionType === 2 && row.correctAnswer) {
      correctAnswers.value = row.correctAnswer.split('')
    } else {
      correctAnswers.value = []
    }
  } else {
    form.value = { category: 'FIRST_AID', questionType: 1, score: 10, status: 1, correctAnswer: 'A', optionA: '', optionB: '', optionC: '', optionD: '' }
    correctAnswers.value = []
  }
  dialogVisible.value = true
}

function onTypeChange() {
  if (form.value.questionType === 3) {
    form.value.optionA = form.value.optionA || '正确'
    form.value.optionB = form.value.optionB || '错误'
    form.value.optionC = ''
    form.value.optionD = ''
  }
  if (form.value.questionType === 2) {
    form.value.correctAnswer = correctAnswers.value.sort().join('')
  } else {
    correctAnswers.value = []
  }
}

async function save() {
  if (!form.value.question) return ElMessage.warning('请输入题目')
  if (!form.value.correctAnswer && correctAnswers.value.length === 0) return ElMessage.warning('请选择正确答案')
  if (form.value.questionType === 2) {
    form.value.correctAnswer = correctAnswers.value.sort().join('')
  }
  await request.post('/admin/question', form.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function toggleStatus(row) {
  await request.post(`/admin/question/${row.id}/toggle`)
  load()
}

async function remove(row) {
  await ElMessageBox.confirm('确定删除该题目？', '提示', { type: 'warning' })
  await request.delete(`/admin/question/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>

<style scoped>
.header-bar { display: flex; justify-content: space-between; align-items: center; font-weight: bold; font-size: 16px; }
</style>
