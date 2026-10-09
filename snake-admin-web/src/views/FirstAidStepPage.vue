<template>
  <el-card>
    <template #header>
      <div class="header-bar">
        <span>🆘 急救闯关关卡管理</span>
        <div class="header-actions">
          <el-input v-model="keyword" placeholder="搜索标题/提示" clearable style="width:220px;margin-right:12px;" @clear="load" @keyup.enter="load" />
          <el-button type="primary" :icon="Plus" @click="openForm()">新增关卡</el-button>
        </div>
      </div>
    </template>

    <el-alert type="info" :closable="false" show-icon style="margin-bottom:16px;">
      共 9 步急救流程：脱离险境 → 辨认蛇种 → 保持镇定 → 紧急呼救 → 伤口解压 → 伤肢制动 → 加压包扎 → 止痛处理 → 禁忌清单
    </el-alert>

    <el-table :data="list" border stripe>
      <el-table-column prop="stepOrder" label="排序" width="70" />
      <el-table-column prop="emoji" label="图标" width="70" align="center">
        <template #default="{ row }">
          <span style="font-size:22px;">{{ row.emoji || '❓' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="关卡标题" width="160" />
      <el-table-column label="交互类型" width="110">
        <template #default="{ row }">
          <el-tag :type="interactiveTagType(row.interactionType)" size="small">
            {{ interactiveLabel(row.interactionType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="score" label="分值" width="70" />
      <el-table-column prop="hint" label="提示语" show-overflow-tooltip />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-switch :model-value="row.enabled === 1" @change="toggle(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openForm(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      style="margin-top:16px;justify-content:flex-end;display:flex;"
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10,20,50]"
      layout="total, sizes, prev, pager, next"
      @current-change="load" @size-change="load" />
  </el-card>

  <!-- 新增/编辑弹窗 -->
  <el-dialog v-model="dialogVisible" :title="form.id ? '编辑关卡' : '新增关卡'" width="700px">
    <el-form :model="form" label-width="110px">
      <el-form-item label="排序序号" required>
        <el-input-number v-model="form.stepOrder" :min="1" :max="99" />
        <span style="margin-left:10px;color:#999;font-size:12px;">决定闯关顺序（1~9）</span>
      </el-form-item>
      <el-form-item label="关卡标题" required>
        <el-input v-model="form.title" placeholder="如：脱离险境" />
      </el-form-item>
      <el-form-item label="图标 Emoji">
        <el-input v-model="form.emoji" placeholder="如：🏃" style="width:120px;" maxlength="4" />
      </el-form-item>
      <el-form-item label="交互类型" required>
        <el-select v-model="form.interactionType" placeholder="选择交互类型" style="width:200px;">
          <el-option label="选择题（choice）" value="choice" />
          <el-option label="模拟拨120（phone）" value="phone" />
          <el-option label="伤肢摆放（limb）" value="limb" />
          <el-option label="拖拽包扎（drag）" value="drag" />
        </el-select>
      </el-form-item>
      <el-form-item label="提示语" required>
        <el-input v-model="form.hint" placeholder="如：刚被蛇咬了，首先应该做什么？" />
      </el-form-item>
      <el-form-item label="正确答案" v-if="form.interactionType === 'choice'" required>
        <el-input v-model="form.correctAnswer" placeholder="正确的选项文本" />
      </el-form-item>
      <el-form-item label="错误答案" v-if="form.interactionType === 'choice'">
        <el-input v-model="form.wrongAnswer" type="textarea" :rows="2" placeholder="多个错误选项用 / 分隔，如：站着不动/打蛇报仇/拍照发圈" />
      </el-form-item>
      <el-form-item label="正确反馈" required>
        <el-input v-model="form.feedbackRight" type="textarea" :rows="2" placeholder="答对后的鼓励语" />
      </el-form-item>
      <el-form-item label="错误反馈">
        <el-input v-model="form.feedbackWrong" type="textarea" :rows="2" placeholder="答错后的解释（交互关卡可留空）" />
      </el-form-item>
      <el-form-item label="分值">
        <el-input-number v-model="form.score" :min="1" :max="100" />
      </el-form-item>
      <el-form-item label="启用状态">
        <el-switch v-model="form.enabled" :active-value="1" :inactive-value="0" />
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
import { stepPage, stepCreate, stepUpdate, stepDelete, stepToggle } from '../api/firstAidStep'

const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const dialogVisible = ref(false)

const form = reactive({
  id: null, stepOrder: 1, title: '', emoji: '', hint: '',
  interactionType: 'choice', correctAnswer: '', wrongAnswer: '',
  feedbackRight: '', feedbackWrong: '', score: 10, enabled: 1
})

function interactiveLabel(type) {
  const map = { choice: '选择题', phone: '拨120', limb: '伤肢摆放', drag: '拖拽包扎' }
  return map[type] || type
}
function interactiveTagType(type) {
  const map = { choice: '', phone: 'warning', limb: 'success', drag: 'danger' }
  return map[type] || ''
}

async function load() {
  try {
    const data = await stepPage({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value })
    list.value = data.records || []
    total.value = data.total || 0
  } catch {}
}

function openForm(row) {
  Object.assign(form, {
    id: null, stepOrder: 1, title: '', emoji: '', hint: '',
    interactionType: 'choice', correctAnswer: '', wrongAnswer: '',
    feedbackRight: '', feedbackWrong: '', score: 10, enabled: 1
  })
  if (row) Object.assign(form, row)
  dialogVisible.value = true
}

async function save() {
  try {
    if (form.interactionType === 'choice' && !form.correctAnswer) {
      ElMessage.warning('选择题类型必须填写正确答案')
      return
    }
    if (form.id) { await stepUpdate(form); ElMessage.success('更新成功') }
    else { await stepCreate(form); ElMessage.success('新增成功') }
    dialogVisible.value = false
    load()
  } catch {}
}

async function toggle(row) {
  try {
    await stepToggle(row.id)
    ElMessage.success(row.enabled === 1 ? '已禁用' : '已启用')
    load()
  } catch {}
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`确定删除关卡 "${row.title}" 吗？`, '提示', { type: 'warning' })
    await stepDelete(row.id)
    ElMessage.success('删除成功')
    load()
  } catch {}
}

onMounted(load)
</script>

<style scoped>
.header-bar { display: flex; justify-content: space-between; align-items: center; }
.header-actions { display: flex; align-items: center; }
</style>
