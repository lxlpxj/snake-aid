<template>
  <div class="consult-workbench">
    <div class="wb-header">
      <h2>👩‍⚕️ 咨询工单工作台</h2>
      <el-tag type="info">仅医疗顾问可见</el-tag>
    </div>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card pending">
        <div class="num">{{ stats.pending }}</div>
        <div class="label">待处理</div>
      </div>
      <div class="stat-card progress">
        <div class="num">{{ stats.inProgress }}</div>
        <div class="label">处理中</div>
      </div>
      <div class="stat-card resolved">
        <div class="num">{{ stats.resolved }}</div>
        <div class="label">已解决</div>
      </div>
    </div>

    <div class="wb-body">
      <!-- 左侧工单列表 -->
      <div class="ticket-list">
        <div class="filter-bar">
          <el-select v-model="filterStatus" placeholder="全部状态" size="small" clearable>
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="IN_PROGRESS" />
            <el-option label="已解决" value="RESOLVED" />
          </el-select>
          <el-input v-model="keyword" placeholder="搜索标题/内容" size="small" clearable style="width: 200px;" />
        </div>

        <div v-for="t in filteredTickets" :key="t.id"
             :class="['ticket-item', { active: selectedId === t.id }, t.status.toLowerCase()]">
          <div class="ti-main" @click="selectTicket(t)">
            <div class="ti-header">
              <span class="ti-title">{{ t.title }}</span>
              <el-tag size="small" :type="statusTag(t.status)">{{ statusText(t.status) }}</el-tag>
            </div>
            <div class="ti-cat">
              <el-tag size="small" type="info">{{ t.category }}</el-tag>
              <span class="ti-time">{{ formatTime(t.createTime) }}</span>
            </div>
            <div class="ti-preview">{{ t.lastMessage }}</div>
          </div>
          <el-button link type="danger" size="small" @click.stop="delTicketFromList(t)">删除</el-button>
        </div>

        <el-empty v-if="!filteredTickets.length" description="暂无工单" />
      </div>

      <!-- 右侧对话 -->
      <div class="chat-panel" v-if="current">
        <div class="cp-header">
          <div>
            <h3>{{ current.title }}</h3>
            <div class="cp-meta">
              <el-tag size="small" type="info">{{ current.category }}</el-tag>
              <span>用户 #{{ current.userId }}</span>
              <span>创建于 {{ formatTime(current.createTime) }}</span>
            </div>
          </div>
          <div class="cp-actions">
            <el-button v-if="current.status !== 'IN_PROGRESS'" size="small" type="primary" @click="startHandle">开始处理</el-button>
            <el-button v-if="current.status !== 'RESOLVED'" size="small" type="success" @click="resolveTicket">标记已解决</el-button>
            <el-button size="small" type="danger" plain @click="delCurrentTicket">删除工单</el-button>
          </div>
        </div>

        <div class="chat-area" ref="chatArea">
          <div v-for="(m, idx) in messages" :key="m.id" :class="['chat-msg', m.senderType === 'ADMIN' ? 'admin' : 'user']">
            <div class="cm-avatar">{{ m.senderType === 'ADMIN' ? '👩‍⚕️' : '👤' }}</div>
            <div class="cm-bubble">
              <div class="cm-sender">{{ m.senderType === 'ADMIN' ? '医疗顾问' : '用户' }}</div>
              <div class="cm-content">{{ m.content }}</div>
              <div class="cm-actions">
                <span class="cm-time">{{ formatTime(m.createTime) }}</span>
                <el-button link type="danger" size="small" @click="delMsg(m, idx)">删除</el-button>
              </div>
            </div>
          </div>
        </div>

        <div class="reply-area">
          <el-input v-model="replyContent" type="textarea" :rows="3"
                    placeholder="输入回复内容..." @keyup.enter.ctrl="sendReply" />
          <div class="ra-actions">
            <el-button type="primary" :disabled="!replyContent.trim()" @click="sendReply">发送回复</el-button>
          </div>
        </div>
      </div>

      <el-empty v-else description="请从左侧选择一个工单" :image-size="120" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const API = '/admin/consult'
const tickets = ref([])
const current = ref(null)
const messages = ref([])
const selectedId = ref(null)
const filterStatus = ref('')
const keyword = ref('')
const replyContent = ref('')

const stats = computed(() => {
  const s = { pending: 0, inProgress: 0, resolved: 0 }
  tickets.value.forEach(t => {
    if (t.status === 'PENDING') s.pending++
    else if (t.status === 'IN_PROGRESS') s.inProgress++
    else if (t.status === 'RESOLVED') s.resolved++
  })
  return s
})

const filteredTickets = computed(() => {
  return tickets.value.filter(t => {
    if (filterStatus.value && t.status !== filterStatus.value) return false
    if (keyword.value) {
      const k = keyword.value.toLowerCase()
      return (t.title || '').toLowerCase().includes(k) || (t.lastMessage || '').toLowerCase().includes(k)
    }
    return true
  }).sort((a, b) => new Date(b.lastTime) - new Date(a.lastTime))
})

onMounted(loadList)

async function loadList() {
  try {
    const res = await request.get(API + '/tickets')
    // request 拦截器已拆到 res.data 层，这里 res 就是 { records, total, ... }
    tickets.value = res.records || []
  } catch (e) {
    console.error(e)
  }
}

async function selectTicket(t) {
  current.value = t
  selectedId.value = t.id
  try {
    const res = await request.get(API + '/ticket/' + t.id)
    // res 就是后端的 data 层
    messages.value = res.messages || []
    await nextTick()
    scrollBottom()
  } catch (e) {}
}

async function sendReply() {
  const text = replyContent.value.trim()
  if (!text) return
  try {
    const res = await request.post(API + '/ticket/' + current.value.id + '/reply', { content: text })
    // res 就是后端的 data（新消息对象）
    messages.value.push(res)
    replyContent.value = ''
    await nextTick()
    scrollBottom()
    ElMessage.success('回复成功')
    loadList()
  } catch (e) {
    ElMessage.error('回复失败')
  }
}

async function startHandle() {
  try {
    await request.put(API + '/ticket/' + current.value.id + '/status', null, { params: { status: 'IN_PROGRESS' } })
    current.value.status = 'IN_PROGRESS'
    ElMessage.success('已开始处理')
    loadList()
  } catch (e) {}
}

async function resolveTicket() {
  try {
    await ElMessageBox.confirm('标记此工单为已解决？', '确认')
    await request.put(API + '/ticket/' + current.value.id + '/status', null, { params: { status: 'RESOLVED' } })
    current.value.status = 'RESOLVED'
    ElMessage.success('已标记解决')
    loadList()
  } catch (e) {}
}

async function delMsg(m, idx) {
  try {
    await ElMessageBox.confirm('确定删除这条消息？', '确认')
    await request.delete(API + '/message/' + m.id)
    messages.value.splice(idx, 1)
    ElMessage.success('已删除')
  } catch (e) {}
}

async function delCurrentTicket() {
  try {
    await ElMessageBox.confirm('确定删除整个工单及所有消息？此操作不可恢复！', '警告', { type: 'warning' })
    await request.delete(API + '/ticket/' + current.value.id)
    current.value = null
    messages.value = []
    selectedId.value = null
    ElMessage.success('工单已删除')
    loadList()
  } catch (e) {}
}

async function delTicketFromList(t) {
  try {
    await ElMessageBox.confirm(`确定删除"${t.title}"？此操作不可恢复！`, '警告', { type: 'warning' })
    await request.delete(API + '/ticket/' + t.id)
    if (current.value?.id === t.id) {
      current.value = null
      messages.value = []
      selectedId.value = null
    }
    ElMessage.success('工单已删除')
    loadList()
  } catch (e) {}
}

function scrollBottom() {
  const area = document.querySelector('.chat-area')
  if (area) area.scrollTop = area.scrollHeight
}

function statusText(s) { return { PENDING: '待处理', IN_PROGRESS: '处理中', RESOLVED: '已解决' }[s] || s }
function statusTag(s) { return { PENDING: 'warning', IN_PROGRESS: 'primary', RESOLVED: 'success' }[s] || '' }
function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const pad = n => n < 10 ? '0' + n : n
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
</script>

<style scoped>
.consult-workbench { padding: 24px; height: 100%; display: flex; flex-direction: column; gap: 16px; }
.wb-header { display: flex; justify-content: space-between; align-items: center; }
.wb-header h2 { margin: 0; font-size: 22px; }

.stat-cards { display: flex; gap: 16px; }
.stat-card { flex: 1; padding: 20px; border-radius: 8px; color: #FFF; text-align: center; }
.stat-card.pending { background: linear-gradient(135deg, #FF9800, #F57C00); }
.stat-card.progress { background: linear-gradient(135deg, #2196F3, #1565C0); }
.stat-card.resolved { background: linear-gradient(135deg, #4CAF50, #2E7D32); }
.stat-card .num { font-size: 36px; font-weight: bold; }
.stat-card .label { font-size: 14px; opacity: 0.9; }

.wb-body { flex: 1; display: flex; gap: 16px; min-height: 0; }
.ticket-list { width: 340px; background: #FFF; border-radius: 8px; padding: 12px; display: flex; flex-direction: column; overflow: hidden; }
.filter-bar { display: flex; gap: 8px; margin-bottom: 12px; }
.ticket-item { padding: 12px; border-radius: 6px; cursor: pointer; border: 1px solid #EEE; margin-bottom: 8px; transition: all .2s; }
.ticket-item:hover { border-color: #1976D2; }
.ticket-item.active { border-color: #1976D2; background: #E3F2FD; }
.ticket-item .ti-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.ticket-item .ti-title { font-weight: bold; font-size: 14px; flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.ticket-item .ti-cat { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.ticket-item .ti-time { font-size: 12px; color: #999; }
.ticket-item .ti-preview { font-size: 13px; color: #666; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }

.chat-panel { flex: 1; background: #FFF; border-radius: 8px; display: flex; flex-direction: column; overflow: hidden; }
.cp-header { padding: 16px 20px; border-bottom: 1px solid #EEE; display: flex; justify-content: space-between; align-items: flex-start; }
.cp-header h3 { margin: 0 0 6px 0; }
.cp-meta { display: flex; gap: 12px; font-size: 12px; color: #888; }
.cp-actions { display: flex; gap: 8px; }

.chat-area { flex: 1; padding: 20px; overflow-y: auto; background: #FAFAFA; }
.chat-msg { display: flex; margin-bottom: 16px; }
.chat-msg.admin { flex-direction: row-reverse; }
.cm-avatar { width: 40px; height: 40px; border-radius: 50%; background: #FFF; border: 1px solid #EEE; display: flex; align-items: center; justify-content: center; font-size: 20px; flex-shrink: 0; }
.cm-bubble { max-width: 65%; padding: 10px 14px; border-radius: 10px; margin: 0 10px; }
.chat-msg.user .cm-bubble { background: #FFF; border: 1px solid #EEE; }
.chat-msg.admin .cm-bubble { background: #1976D2; color: #FFF; }
.cm-sender { font-size: 12px; opacity: 0.7; margin-bottom: 4px; }
.cm-content { font-size: 14px; line-height: 1.6; white-space: pre-wrap; }
.cm-time { font-size: 11px; opacity: 0.5; margin-top: 6px; }

.reply-area { padding: 16px 20px; border-top: 1px solid #EEE; }
.ra-actions { display: flex; justify-content: flex-end; margin-top: 8px; }
</style>
