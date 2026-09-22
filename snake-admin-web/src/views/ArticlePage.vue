<template>
  <el-card>
    <template #header>
      <div class="header-bar">
        <span>科普文章管理</span>
        <div>
          <el-select v-model="filterCategory" placeholder="分类" style="width:120px;margin-right:10px;" clearable @change="load">
            <el-option label="预防" value="预防" />
            <el-option label="急救" value="急救" />
            <el-option label="科普" value="科普" />
          </el-select>
          <el-select v-model="filterAudit" placeholder="审核状态" style="width:120px;margin-right:10px;" clearable @change="load">
            <el-option :value="0" label="草稿" />
            <el-option :value="1" label="待审核" />
            <el-option :value="2" label="已发布" />
            <el-option :value="3" label="已驳回" />
          </el-select>
          <el-button v-if="!isAdvisor" type="primary" :icon="Plus" @click="openForm()">新增文章</el-button>
          <el-tag v-if="isAdvisor" type="info" effect="plain">👁️ 只读查看（医疗顾问）</el-tag>
        </div>
      </div>
    </template>

    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="封面" width="70">
        <template #default="{ row }">
          <el-image
            v-if="row.coverImage"
            :src="imgUrl(row.coverImage)"
            :preview-src-list="[imgUrl(row.coverImage)]"
            fit="cover"
            style="width:50px;height:50px;border-radius:4px;">
            <template #error><div class="img-fallback">无</div></template>
          </el-image>
          <div v-else class="img-fallback">无</div>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
      <el-table-column prop="category" label="分类" width="80" />
      <el-table-column label="审核" width="90">
        <template #default="{ row }">
          <el-tag v-if="row.auditStatus === 0" size="small">草稿</el-tag>
          <el-tag v-else-if="row.auditStatus === 1" type="warning" size="small">待审</el-tag>
          <el-tag v-else-if="row.auditStatus === 2" type="success" size="small">已发布</el-tag>
          <el-tag v-else type="danger" size="small">驳回</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="上下架" width="80">
        <template #default="{ row }">
          <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="165" />
      <el-table-column label="操作" :width="isAdvisor ? 80 : 280" fixed="right">
        <template #default="{ row }">
          <el-button v-if="isAdvisor" size="small" link type="info">只读</el-button>
          <template v-else>
            <el-button size="small" @click="openForm(row)">编辑</el-button>
            <el-button v-if="row.auditStatus !== 2" size="small" type="primary" link @click="submitAudit(row)">提交审核</el-button>
            <el-button size="small" type="danger" link @click="remove(row)">删除</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      style="margin-top:16px;justify-content:flex-end;display:flex;"
      v-model:current-page="pageNum" v-model:page-size="pageSize"
      :total="total" :page-sizes="[10,20,50]"
      layout="total, sizes, prev, pager, next"
      @current-change="load" @size-change="load" />
  </el-card>

  <!-- 新增/编辑弹窗 -->
  <el-dialog v-model="dialogVisible" :title="form.id ? '编辑文章' : '新增文章'" width="720px">
    <el-form :model="form" label-width="100px">
      <el-form-item label="标题" required>
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="form.category" style="width:100%;" placeholder="请选择">
          <el-option label="预防" value="预防" />
          <el-option label="急救" value="急救" />
          <el-option label="科普" value="科普" />
        </el-select>
      </el-form-item>
      <el-form-item label="摘要">
        <el-input v-model="form.summary" />
      </el-form-item>
      <el-form-item label="封面图">
        <el-upload
          class="article-uploader"
          action="/api/admin/upload/image"
          :show-file-list="false"
          :before-upload="beforeUpload"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError">
          <img v-if="form.coverImage" :src="imgUrl(form.coverImage)" class="article-preview" />
          <el-icon v-else class="article-uploader-icon"><Plus /></el-icon>
        </el-upload>
        <div v-if="form.coverImage" class="img-url-tip" @click="form.coverImage = ''">点击移除</div>
      </el-form-item>
      <el-form-item label="内容" required>
        <el-input v-model="form.content" type="textarea" :rows="14" placeholder="支持 Markdown 语法" />
      </el-form-item>
      <el-form-item label="上下架">
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { articlePage, addArticle, updateArticle, deleteArticle, auditArticle, submitArticleAudit } from '../api/article'

const user = computed(() => { try { return JSON.parse(localStorage.getItem('snake_admin_user') || '{}') } catch { return {} } })
const isAdvisor = computed(() => user.value.role === 'MEDICAL_ADVISOR')

function imgUrl(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return '/api' + url  // /uploads/xxx.jpg → /api/uploads/xxx.jpg
}

const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const filterCategory = ref('')
const filterAudit = ref(null)
const dialogVisible = ref(false)

const form = reactive({
  id: null, title: '', category: '', summary: '',
  coverImage: '', content: '', auditStatus: 0, status: 1
})

async function load() {
  try {
    const data = await articlePage({
      pageNum: pageNum.value, pageSize: pageSize.value,
      auditStatus: filterAudit.value ?? undefined,
      category: filterCategory.value || undefined
    })
    list.value = data.records || []
    total.value = data.total || 0
  } catch {}
}

function openForm(row) {
  Object.assign(form, {
    id: null, title: '', category: '', summary: '',
    coverImage: '', content: '', auditStatus: 0, status: 1
  })
  if (row) Object.assign(form, row)
  dialogVisible.value = true
}

async function save() {
  try {
    if (form.id) {
      await updateArticle({
        id: form.id, title: form.title, category: form.category,
        summary: form.summary, content: form.content,
        coverImage: form.coverImage, status: form.status
      })
      ElMessage.success('更新成功')
    } else {
      await addArticle({
        title: form.title, category: form.category, summary: form.summary,
        content: form.content, coverImage: form.coverImage, status: form.status
      })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    load()
  } catch {}
}

async function toggleStatus(row) {
  try {
    await updateArticle({ id: row.id, status: row.status })
    ElMessage.success(row.status === 1 ? '已上架' : '已下架')
  } catch { row.status = row.status === 1 ? 0 : 1 }
}

async function submitAudit(row) {
  try { await ElMessageBox.confirm(`确定将"${row.title}"提交审核？`, '提示', { type: 'warning' }); await submitArticleAudit(row.id); ElMessage.success('已提交审核'); load() } catch {}
}

async function audit(row, pass) {
  try {
    await ElMessageBox.confirm(`确定${pass ? '通过' : '驳回'}"${row.title}"？`, '提示', { type: 'warning' })
    await auditArticle(row.id, { pass })
    ElMessage.success(`已${pass ? '通过' : '驳回'}`); load()
  } catch {}
}

async function remove(row) {
  const isPublished = row.auditStatus === 2
  const action = isPublished ? '下线' : '删除'
  try { await ElMessageBox.confirm(`确定${action}文章"${row.title}"吗？${isPublished ? '（已发布内容将转为"已下线"状态，历史可追溯）' : ''}`, '提示', { type: 'warning' }); await deleteArticle(row.id); ElMessage.success(isPublished ? '已下线' : '删除成功'); load() } catch {}
}

function beforeUpload(file) {
  const ok = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(file.type)
  if (!ok) { ElMessage.error('仅支持图片格式'); return false }
  if (file.size > 5 * 1024 * 1024) { ElMessage.error('图片不能超过 5MB'); return false }
  return true
}
function handleUploadSuccess(res) {
  if (res && res.code === 200 && res.data && res.data.url) {
    form.coverImage = res.data.url; ElMessage.success('上传成功')
  } else {
    ElMessage.error(res?.message || res?.msg || '上传失败')
  }
}
function handleUploadError() { ElMessage.error('上传失败，请确认后端已启动') }

onMounted(load)
</script>

<style scoped>
.header-bar { display: flex; justify-content: space-between; align-items: center; }
.img-fallback { width:50px;height:50px;display:flex;align-items:center;justify-content:center;background:#f5f7fa;color:#c0c4cc;font-size:12px;border-radius:4px; }
.article-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9; border-radius: 6px; cursor: pointer;
  position: relative; overflow: hidden; width: 140px; height: 140px;
  display: flex; align-items: center; justify-content: center;
}
.article-uploader :deep(.el-upload:hover) { border-color: var(--el-color-primary); }
.article-uploader-icon { font-size: 28px; color: #8c939d; }
.article-preview { width: 140px; height: 140px; object-fit: cover; display: block; }
.img-url-tip { font-size:12px;color:#e6a23c;cursor:pointer;margin-top:4px; }
.img-url-tip:hover { text-decoration: underline; }
</style>
