<template>
  <el-card>
    <template #header>
      <div class="header-bar">
        <span>蛇种库管理</span>
        <div>
          <el-input v-model="keyword" placeholder="搜索蛇名/学名" style="width:200px;margin-right:10px;" clearable @keyup.enter="load" />
          <el-select v-model="filterVenom" placeholder="是否有毒" style="width:120px;margin-right:10px;" clearable @change="load">
            <el-option :value="1" label="有毒" />
            <el-option :value="0" label="无毒" />
          </el-select>
          <el-button v-if="!isAdvisor" type="primary" :icon="Plus" @click="openForm()">新增蛇种</el-button>
          <el-tag v-if="isAdvisor" type="info" effect="plain">👁️ 只读查看（医疗顾问）</el-tag>
        </div>
      </div>
    </template>

    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="图片" width="90">
        <template #default="{ row }">
          <el-image
            v-if="row.imageUrl"
            :src="imgUrl(row.imageUrl)"
            :preview-src-list="[imgUrl(row.imageUrl)]"
            fit="cover"
            style="width:60px;height:60px;border-radius:4px;">
            <template #error><div class="img-fallback">无图</div></template>
          </el-image>
          <div v-else class="img-fallback">无图</div>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="中文名" width="120" />
      <el-table-column prop="scientificName" label="学名" width="170" show-overflow-tooltip />
      <el-table-column label="有毒" width="70">
        <template #default="{ row }">
          <el-tag :type="row.isVenomous === 1 ? 'danger' : 'success'" size="small">
            {{ row.isVenomous === 1 ? '有毒' : '无毒' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="venomType" label="毒液类型" width="90" />
      <el-table-column label="等级" width="70">
        <template #default="{ row }">
          <span v-if="row.hazardLevel">{{ '★'.repeat(row.hazardLevel) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="130">
        <template #default="{ row }">
          <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="toggleStatus(row)" />
          <el-tag v-if="row.auditStatus === 1" type="warning" size="small" style="margin-left:2px;">待审</el-tag>
          <el-tag v-else-if="row.auditStatus === 2" type="success" size="small" style="margin-left:2px;">已发</el-tag>
          <el-tag v-else-if="row.auditStatus === 3" type="info" size="small" style="margin-left:2px;">驳回</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" :width="isAdvisor ? 80 : 180" fixed="right">
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
      :total="total"
      :page-sizes="[10,20,50]"
      layout="total, sizes, prev, pager, next"
      @current-change="load" @size-change="load" />
  </el-card>

  <!-- 新增/编辑弹窗 -->
  <el-dialog v-model="dialogVisible" :title="form.id ? '编辑蛇种' : '新增蛇种'" width="720px">
    <el-form :model="form" label-width="110px">
      <el-divider content-position="left">基础信息</el-divider>
      <el-form-item label="中文名" required>
        <el-input v-model="form.name" placeholder="如：银环蛇" />
      </el-form-item>
      <el-form-item label="拉丁学名">
        <el-input v-model="form.scientificName" placeholder="如：Bungarus multicinctus" />
      </el-form-item>
      <el-form-item label="是否有毒" required>
        <el-switch v-model="form.isVenomous" :active-value="1" :inactive-value="0" />
        <span class="form-tip">关闭 = 无毒蛇（用于 FR12 无毒对比）</span>
      </el-form-item>
      <el-form-item label="毒液类型">
        <el-select v-model="form.venomType" placeholder="无毒请留空" style="width:100%;" :disabled="form.isVenomous !== 1" clearable>
          <el-option label="神经毒" value="神经毒" />
          <el-option label="血液毒" value="血液毒" />
          <el-option label="细胞毒" value="细胞毒" />
          <el-option label="混合毒" value="混合毒" />
        </el-select>
      </el-form-item>
      <el-form-item label="危险等级">
        <el-select v-model="form.hazardLevel" placeholder="1-4 级" style="width:100%;" clearable :disabled="form.isVenomous !== 1">
          <el-option label="1 轻微毒" :value="1" />
          <el-option label="2 中等毒" :value="2" />
          <el-option label="3 剧毒" :value="3" />
          <el-option label="4 极危" :value="4" />
        </el-select>
      </el-form-item>

      <el-divider content-position="left">外形特征（FR4 图库检索用）</el-divider>
      <el-form-item label="头型" required>
        <el-select v-model="form.headShape" placeholder="选择或输入" style="width:100%;" filterable allow-create clearable>
          <el-option label="三角形" value="三角形" />
          <el-option label="椭圆形" value="椭圆形" />
          <el-option label="圆形" value="圆形" />
          <el-option label="菱形" value="菱形" />
          <el-option label="钝圆" value="钝圆" />
          <el-option label="扁平" value="扁平" />
          <el-option label="椭圆形可膨扁" value="椭圆形可膨扁" />
        </el-select>
      </el-form-item>
      <el-form-item label="花纹">
        <el-select v-model="form.pattern" placeholder="选择或输入" style="width:100%;" filterable allow-create clearable>
          <el-option label="无" value="无" />
          <el-option label="横纹" value="横纹" />
          <el-option label="纵纹" value="纵纹" />
          <el-option label="斑点" value="斑点" />
          <el-option label="斑块" value="斑块" />
          <el-option label="条纹" value="条纹" />
          <el-option label="环纹" value="环纹" />
          <el-option label="方形斑纹" value="方形斑纹" />
          <el-option label="眼镜状斑纹" value="眼镜状斑纹" />
          <el-option label="黑纵纹" value="黑纵纹" />
        </el-select>
      </el-form-item>
      <el-form-item label="颜色">
        <el-input v-model="form.color" placeholder="如：翠绿色 / 黑色+白色环纹 / 棕褐色" />
      </el-form-item>
      <el-form-item label="尾部">
        <el-input v-model="form.tailShape" placeholder="如：尖尾 / 短粗 / 渐细 / 红色尾尖" />
      </el-form-item>

      <el-divider content-position="left">详细描述</el-divider>
      <el-form-item label="外观描述">
        <el-input v-model="form.appearanceDesc" type="textarea" :rows="3" placeholder="大段文字描述蛇的整体外观..." />
      </el-form-item>
      <el-form-item label="分布地区">
        <el-input v-model="form.distribution" placeholder="如：长江以南各省 / 华南 / 云南" />
      </el-form-item>
      <el-form-item label="被咬症状" v-if="form.isVenomous === 1">
        <el-input v-model="form.symptoms" type="textarea" :rows="2" placeholder="中毒后的特异性症状..." />
      </el-form-item>
      <el-form-item label="处理要点" v-if="form.isVenomous === 1">
        <el-input v-model="form.firstAidSpecifics" type="textarea" :rows="2" placeholder="针对该蛇种的特异性处理..." />
      </el-form-item>

      <el-divider content-position="left">其他</el-divider>
      <el-form-item label="封面图">
        <el-upload
          class="snake-uploader"
          action="/api/admin/upload/image"
          :show-file-list="false"
          :before-upload="beforeUpload"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError">
          <img v-if="form.imageUrl" :src="imgUrl(form.imageUrl)" class="snake-preview" />
          <el-icon v-else class="snake-uploader-icon"><Plus /></el-icon>
        </el-upload>
        <div v-if="form.imageUrl" class="img-url-tip" @click="form.imageUrl = ''">点击移除</div>
      </el-form-item>
      <el-form-item label="上下架">
        <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        <span class="form-tip">下架后 APP 端不可见</span>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="save">保存（还需提交审核发布）</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import request from '../utils/request'

const user = computed(() => { try { return JSON.parse(localStorage.getItem('snake_admin_user') || '{}') } catch { return {} } })
const isAdvisor = computed(() => user.value.role === 'MEDICAL_ADVISOR')
import { snakePage, addSnake, updateSnake, deleteSnake, submitSnakeAudit } from '../api/snake'

// 拼接图片完整 URL（后端 context-path=/api，Vite proxy /api→后端8080/api）
function imgUrl(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return '/api' + url  // /uploads/xxx.jpg → /api/uploads/xxx.jpg
}

const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const filterVenom = ref(null)
const dialogVisible = ref(false)

const form = reactive({
  id: null, name: '', scientificName: '',
  isVenomous: 0, venomType: '', hazardLevel: null,
  headShape: '', pattern: '', color: '', tailShape: '',
  appearanceDesc: '', distribution: '', symptoms: '', firstAidSpecifics: '',
  imageUrl: '', status: 1, auditStatus: 0
})

async function load() {
  try {
    const data = await snakePage({
      pageNum: pageNum.value, pageSize: pageSize.value,
      keyword: keyword.value,
      isVenomous: filterVenom.value
    })
    list.value = data.records || []
    total.value = data.total || 0
  } catch {}
}

function openForm(row) {
  Object.assign(form, {
    id: null, name: '', scientificName: '',
    isVenomous: 0, venomType: '', hazardLevel: null,
    headShape: '', pattern: '', color: '', tailShape: '',
    appearanceDesc: '', distribution: '', symptoms: '', firstAidSpecifics: '',
    imageUrl: '', status: 1, auditStatus: 0
  })
  if (row) Object.assign(form, row)
  dialogVisible.value = true
}

async function save() {
  try {
    if (form.id) {
      await updateSnake(form); ElMessage.success('更新成功')
    } else {
      await addSnake(form); ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    load()
  } catch {}
}

async function toggleStatus(row) {
  try {
    await updateSnake({ id: row.id, status: row.status })
    ElMessage.success(row.status === 1 ? '已上架' : '已下架')
  } catch {
    row.status = row.status === 1 ? 0 : 1
  }
}

async function submitAudit(row) {
  try {
    await submitSnakeAudit(row.id)
    ElMessage.success('已提交审核')
    load()
  } catch {}
}

async function remove(row) {
  try {
    const isPublished = row.auditStatus === 2
    const action = isPublished ? '下线' : '删除'
    await ElMessageBox.confirm(`确定${action}蛇种"${row.name}"吗？${isPublished ? '（已发布内容将转为"已下线"状态，历史可追溯）' : ''}`, '提示', { type: 'warning' })
    await deleteSnake(row.id)
    ElMessage.success(isPublished ? '已下线' : '删除成功')
    load()
  } catch {}
}

function beforeUpload(file) {
  const ok = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(file.type)
  if (!ok) { ElMessage.error('仅支持图片格式'); return false }
  if (file.size > 5 * 1024 * 1024) { ElMessage.error('图片不能超过 5MB'); return false }
  return true
}
function handleUploadSuccess(res) {
  if (res && res.code === 200 && res.data && res.data.url) {
    form.imageUrl = res.data.url  // 后端返回 /uploads/xxx.jpg
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(res?.message || res?.msg || '上传失败')
  }
}
function handleUploadError() {
  ElMessage.error('上传失败，请确认后端已启动')
}

onMounted(load)
</script>

<style scoped>
.header-bar { display: flex; justify-content: space-between; align-items: center; }
.snake-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9; border-radius: 6px; cursor: pointer;
  position: relative; overflow: hidden; width: 120px; height: 120px;
  display: flex; align-items: center; justify-content: center;
}
.snake-uploader :deep(.el-upload:hover) { border-color: var(--el-color-primary); }
.snake-uploader-icon { font-size: 28px; color: #8c939d; }
.snake-preview { width: 120px; height: 120px; object-fit: cover; display: block; }
.img-fallback { width:60px;height:60px;display:flex;align-items:center;justify-content:center;background:#f5f7fa;color:#c0c4cc;font-size:12px;border-radius:4px; }
.img-url-tip { font-size:12px;color:#e6a23c;cursor:pointer;margin-top:4px; }
.img-url-tip:hover { text-decoration: underline; }
.form-tip { margin-left: 8px; font-size: 12px; color: #909399; }
</style>
