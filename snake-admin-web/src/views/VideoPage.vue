<template>
  <el-card>
    <template #header>
      <div class="header-bar">
        <span>🎬 科普视频管理</span>
        <div>
          <el-input v-model="keyword" placeholder="搜索标题/简介" style="width:200px;margin-right:10px;" clearable @keyup.enter="load" />
          <el-select v-model="filterCategory" placeholder="分类" style="width:130px;margin-right:10px;" clearable @change="load">
            <el-option value="急救操作" label="急救操作" />
            <el-option value="蛇种识别" label="蛇种识别" />
            <el-option value="案例讲解" label="案例讲解" />
            <el-option value="其他" label="其他" />
          </el-select>
          <el-select v-model="filterStatus" placeholder="状态" style="width:100px;margin-right:10px;" clearable @change="load">
            <el-option :value="1" label="上架" />
            <el-option :value="0" label="下架" />
          </el-select>
          <el-button type="primary" :icon="Plus" @click="openForm()">新增视频</el-button>
        </div>
      </div>
    </template>

    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" show-overflow-tooltip min-width="200" />
      <el-table-column label="分类" width="100">
        <template #default="{ row }">
          <el-tag size="small">{{ row.category }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="视频" width="280">
        <template #default="{ row }">
          <video v-if="row.videoUrl" :src="fullUrl(row.videoUrl)" controls class="video-preview" />
          <span v-else style="color:#999">无视频</span>
        </template>
      </el-table-column>
      <el-table-column label="时长/大小" width="130">
        <template #default="{ row }">
          <div>{{ fmtDuration(row.duration) }}</div>
          <div style="font-size:12px;color:#888">{{ fmtSize(row.fileSize) }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="playCount" label="播放" width="70" />
      <el-table-column prop="sort" label="排序" width="60" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
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
  <el-dialog v-model="dialogVisible" :title="form.id ? '编辑视频' : '新增视频'" width="720px">
    <el-form :model="form" label-width="90px">
      <el-form-item label="标题" required>
        <el-input v-model="form.title" placeholder="视频标题" />
      </el-form-item>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="分类">
            <el-select v-model="form.category" style="width:100%">
              <el-option value="急救操作" label="急救操作" />
              <el-option value="蛇种识别" label="蛇种识别" />
              <el-option value="案例讲解" label="案例讲解" />
              <el-option value="其他" label="其他" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="作者">
            <el-input v-model="form.author" placeholder="主讲人（可选）" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="简介">
        <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="视频简介" />
      </el-form-item>

      <!-- 视频上传 -->
      <el-form-item label="视频文件" required>
        <div style="display:flex;gap:12px;align-items:center;">
          <el-upload
            action="/api/admin/upload/video"
            :show-file-list="false"
            :before-upload="beforeVideoUpload"
            :on-success="handleVideoSuccess"
            :on-error="handleUploadError">
            <el-button type="primary" :icon="Upload">选择视频</el-button>
          </el-upload>
          <el-input v-model="form.videoUrl" placeholder="/uploads/xxx.mp4" style="flex:1" />
        </div>
        <div v-if="form.videoUrl" style="margin-top:8px;">
          <video :src="fullUrl(form.videoUrl)" controls class="video-preview-lg" />
        </div>
        <div style="font-size:12px;color:#888;margin-top:4px;">支持 mp4/webm/mov/avi/mkv/m4v，最大 200MB</div>
      </el-form-item>

      <!-- 封面上传 -->
      <el-form-item label="封面图">
        <el-upload
          action="/api/admin/upload/image"
          :show-file-list="false"
          :before-upload="(f) => beforeImgUpload(f, 2 * 1024 * 1024)"
          :on-success="handleCoverSuccess"
          :on-error="handleUploadError">
          <el-button :icon="Upload">上传封面</el-button>
        </el-upload>
        <span style="margin-left:10px;">
          <el-input v-model="form.coverUrl" placeholder="/uploads/xxx.jpg" style="width:280px;" />
        </span>
        <img v-if="form.coverUrl" :src="fullUrl(form.coverUrl)" class="cover-preview" />
      </el-form-item>

      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="排序">
            <el-input-number v-model="form.sort" :min="0" :max="9999" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="状态">
            <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="上架" inactive-text="下架" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="审核">
            <el-switch v-model="form.auditStatus" :active-value="2" :inactive-value="0" active-text="已发布" inactive-text="草稿" />
          </el-form-item>
        </el-col>
      </el-row>
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
import { Plus, Upload } from '@element-plus/icons-vue'
import request from '../utils/request'

function fullUrl(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return '/api' + url
}
function fmtDuration(sec) {
  if (!sec) return '-'
  const m = Math.floor(sec / 60), s = sec % 60
  return m + ':' + String(s).padStart(2, '0')
}
function fmtSize(b) {
  if (!b) return '-'
  if (b > 1024 * 1024 * 1024) return (b / 1024 / 1024 / 1024).toFixed(1) + 'GB'
  if (b > 1024 * 1024) return (b / 1024 / 1024).toFixed(1) + 'MB'
  if (b > 1024) return (b / 1024).toFixed(1) + 'KB'
  return b + 'B'
}

const keyword = ref('')
const filterCategory = ref('')
const filterStatus = ref(null)
const list = ref([])
const page = reactive({ current: 1, size: 10, total: 0 })

const dialogVisible = ref(false)
const form = ref({})

async function load() {
  const params = {
    current: page.current, size: page.size,
    category: filterCategory.value || undefined,
    status: filterStatus.value,
    keyword: keyword.value || undefined,
  }
  const data = await request.get('/admin/video/page', { params })
  list.value = data.records
  page.total = data.total
}

function openForm(row) {
  if (row) {
    form.value = { ...row }
  } else {
    form.value = { title: '', category: '急救操作', summary: '', videoUrl: '', coverUrl: '', sort: 0, status: 1, auditStatus: 2, duration: 0, fileSize: 0, author: '' }
  }
  dialogVisible.value = true
}

async function save() {
  if (!form.value.title) return ElMessage.warning('请输入标题')
  if (!form.value.videoUrl) return ElMessage.warning('请先上传视频文件')
  await request.post('/admin/video', form.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function toggleStatus(row) {
  await request.post(`/admin/video/${row.id}/toggle`)
  load()
}

async function remove(row) {
  await ElMessageBox.confirm('确定删除该视频？', '提示', { type: 'warning' })
  await request.delete(`/admin/video/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

// ===== 上传 =====
function beforeVideoUpload(file) {
  const ok = ['video/mp4', 'video/webm', 'video/quicktime', 'video/x-msvideo', 'video/x-matroska'].includes(file.type)
  const extOk = /\.(mp4|webm|mov|avi|mkv|m4v)$/i.test(file.name)
  if (!ok && !extOk) { ElMessage.error('仅支持 mp4/webm/mov/avi/mkv/m4v'); return false }
  if (file.size > 200 * 1024 * 1024) { ElMessage.error('视频不能超过 200MB'); return false }
  return true
}
function beforeImgUpload(file, maxSize) {
  const ok = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(file.type)
  if (!ok) { ElMessage.error('仅支持图片格式'); return false }
  if (file.size > maxSize) { ElMessage.error('图片不能超过 ' + (maxSize / 1024 / 1024) + 'MB'); return false }
  return true
}
function handleVideoSuccess(res, file) {
  if (res && res.code === 200 && res.data) {
    form.value.videoUrl = res.data.url
    form.value.fileSize = parseInt(res.data.size || '0')
    // 从文件名推断时长太复杂，这里只存 size
    ElMessage.success('视频上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}
function handleCoverSuccess(res) {
  if (res && res.code === 200 && res.data && res.data.url) {
    form.value.coverUrl = res.data.url; ElMessage.success('封面上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}
function handleUploadError() { ElMessage.error('上传失败，请确认后端已启动') }

onMounted(load)
</script>

<style scoped>
.header-bar { display: flex; justify-content: space-between; align-items: center; font-weight: bold; font-size: 16px; }
.video-preview { width: 160px; height: 90px; object-fit: cover; border-radius: 4px; background:#000; }
.video-preview-lg { width: 320px; height: 180px; object-fit: contain; border-radius: 4px; background:#000; }
.cover-preview { max-width: 120px; max-height: 80px; margin-top: 6px; border-radius: 4px; }
</style>
