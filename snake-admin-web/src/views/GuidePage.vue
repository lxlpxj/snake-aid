<template>
  <el-card>
    <template #header>
      <div class="header-bar">
        <span>急救指南管理</span>
        <el-button type="primary" :icon="Plus" @click="openForm()">新增指南</el-button>
      </div>
    </template>

    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" width="200" />
      <el-table-column label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isGeneric === 1 ? 'warning' : ''" size="small">
            {{ row.isGeneric === 1 ? '通用指南' : '蛇种专属' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="视频" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.videoUrl" type="success" size="small">有视频</el-tag>
          <span v-else style="color:#c0c4cc;font-size:12px;">无</span>
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="70" />
      <el-table-column label="内容预览" show-overflow-tooltip>
        <template #default="{ row }">
          {{ (row.content || '').substring(0, 80) }}...
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
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
  <el-dialog v-model="dialogVisible" :title="form.id ? '编辑指南' : '新增指南'" width="750px">
    <el-form :model="form" label-width="100px">
      <el-form-item label="标题" required>
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="通用指南">
        <el-switch v-model="form.isGeneric" :active-value="1" :inactive-value="0" />
        <span style="margin-left:10px;color:#999;font-size:12px;">开启后适用于所有咬伤</span>
      </el-form-item>
      <el-form-item label="关联蛇种">
        <el-input v-if="form.isGeneric === 0" v-model.number="form.snakeId" placeholder="蛇种ID" />
        <span v-else style="color:#999;font-size:12px;">通用指南无需关联蛇种</span>
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="form.sortOrder" :min="0" />
      </el-form-item>
      <el-form-item label="内容(Markdown)" required>
        <el-input v-model="form.content" type="textarea" :rows="12" placeholder="支持 Markdown 语法" />
      </el-form-item>
      <el-form-item label="教学视频">
        <div class="video-upload-wrap">
          <!-- 已有视频：播放器 + 移除按钮 -->
          <div v-if="form.videoUrl" class="video-preview">
            <video :src="videoUrl(form.videoUrl)" controls preload="metadata" style="max-width:100%;max-height:240px;border-radius:6px;" />
            <div class="video-actions">
              <el-button size="small" type="danger" link @click="form.videoUrl = ''">移除视频</el-button>
            </div>
          </div>
          <!-- 上传按钮 -->
          <el-upload
            v-else
            action="/api/admin/upload/video"
            :show-file-list="false"
            :before-upload="beforeUploadVideo"
            :on-success="handleVideoSuccess"
            :on-error="handleVideoError"
            :on-progress="handleVideoProgress"
            accept="video/mp4,video/webm,video/quicktime,video/x-msvideo">
            <el-button :loading="uploadingVideo" type="primary" plain>
              <el-icon style="margin-right:4px;"><UploadFilled /></el-icon>
              {{ uploadingVideo ? `上传中 ${uploadProgress}%` : '选择视频上传' }}
            </el-button>
            <div class="upload-tip">支持 mp4 / webm / mov / avi，最大 200MB</div>
          </el-upload>
        </div>
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
import { Plus, UploadFilled } from '@element-plus/icons-vue'
import { guideList, addGuide, updateGuide, deleteGuide } from '../api/guide'

const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const uploadingVideo = ref(false)
const uploadProgress = ref(0)

const form = reactive({
  id: null, title: '', snakeId: null, content: '',
  videoUrl: '', sortOrder: 0, isGeneric: 1
})

function videoUrl(u) {
  if (!u) return ''
  if (u.startsWith('http')) return u
  return '/api' + u  // /uploads/xxx.mp4 → /api/uploads/xxx.mp4
}

async function load() {
  try {
    const data = await guideList({ pageNum: pageNum.value, pageSize: pageSize.value })
    list.value = data.records || []
    total.value = data.total || 0
  } catch {}
}

function openForm(row) {
  Object.assign(form, {
    id: null, title: '', snakeId: null, content: '',
    videoUrl: '', sortOrder: 0, isGeneric: 1
  })
  if (row) Object.assign(form, row)
  dialogVisible.value = true
}

async function save() {
  try {
    if (form.id) { await updateGuide(form); ElMessage.success('更新成功') }
    else { await addGuide(form); ElMessage.success('新增成功') }
    dialogVisible.value = false
    load()
  } catch {}
}

async function remove(row) {
  const isPublished = row.auditStatus === 2
  const action = isPublished ? '下线' : '删除'
  try {
    await ElMessageBox.confirm(`确定${action}指南"${row.title}"吗？${isPublished ? '（已发布内容将转为"已下线"状态，历史可追溯）' : ''}`, '提示', { type: 'warning' })
    await deleteGuide(row.id)
    ElMessage.success(isPublished ? '已下线' : '删除成功')
    load()
  } catch {}
}

// ============ 视频上传 ============
function beforeUploadVideo(file) {
  const okTypes = ['video/mp4', 'video/webm', 'video/quicktime', 'video/x-msvideo', 'video/x-matroska']
  const okExt = /\.(mp4|webm|mov|avi|mkv|m4v)$/i.test(file.name)
  if (!okTypes.includes(file.type) && !okExt) {
    ElMessage.error('仅支持 mp4/webm/mov/avi/mkv/m4v 格式'); return false
  }
  if (file.size > 200 * 1024 * 1024) {
    ElMessage.error('视频不能超过 200MB'); return false
  }
  uploadingVideo.value = true
  uploadProgress.value = 0
  return true
}
function handleVideoProgress(e) {
  if (e.percent != null) uploadProgress.value = Math.round(e.percent)
}
function handleVideoSuccess(res) {
  uploadingVideo.value = false
  if (res && res.code === 200 && res.data && res.data.url) {
    form.videoUrl = res.data.url
    ElMessage.success('视频上传成功')
  } else {
    ElMessage.error(res?.message || res?.msg || '上传失败')
  }
}
function handleVideoError() {
  uploadingVideo.value = false
  uploadProgress.value = 0
  ElMessage.error('视频上传失败，请确认后端已启动且文件小于 200MB')
}

onMounted(load)
</script>

<style scoped>
.header-bar { display: flex; justify-content: space-between; }
.video-upload-wrap { width: 100%; }
.upload-tip { font-size: 12px; color: #909399; margin-top: 6px; }
.video-preview { border: 1px solid #ebeef5; border-radius: 6px; padding: 10px; background: #fafafa; }
.video-actions { margin-top: 6px; }
</style>
