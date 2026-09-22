<template>
  <el-card>
    <template #header>
      <div class="header-bar">
        <span>后台用户管理</span>
        <div>
          <el-select v-model="filterRole" placeholder="按角色筛选" style="width:160px;margin-right:10px;" clearable @change="load">
            <el-option label="系统管理员" value="SYS_ADMIN" />
            <el-option label="内容管理员" value="CONTENT_ADMIN" />
            <el-option label="医疗顾问" value="MEDICAL_ADVISOR" />
          </el-select>
          <el-button type="primary" :icon="Plus" @click="openForm()">新增账号</el-button>
        </div>
      </div>
    </template>

    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="登录账号" width="130" />
      <el-table-column prop="realName" label="真实姓名" width="130" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column label="角色" width="140">
        <template #default="{ row }">
          <el-tag :type="roleTagType(row.roleType)" size="small">{{ roleLabel(row.roleType) }}</el-tag>
          <div class="role-code">{{ row.roleType }}</div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="toggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openForm(row)">编辑</el-button>
          <el-button size="small" type="warning" @click="resetPwd(row)">重置密码</el-button>
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
  <el-dialog v-model="dialogVisible" :title="form.id ? '编辑账号' : '新增账号'" width="500px">
    <el-form :model="form" label-width="100px">
      <el-form-item label="登录账号" required>
        <el-input v-model="form.username" :disabled="!!form.id" placeholder="如 contentadmin" />
      </el-form-item>
      <el-form-item label="密码" v-if="!form.id">
        <el-input v-model="form.password" type="password" show-password placeholder="默认 123456" />
      </el-form-item>
      <el-form-item label="真实姓名">
        <el-input v-model="form.realName" placeholder="如 张医生" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="form.phone" />
      </el-form-item>
      <el-form-item label="角色" required>
        <el-select v-model="form.roleType" style="width:100%;">
          <el-option label="系统管理员 (SYS_ADMIN)" value="SYS_ADMIN" />
          <el-option label="内容管理员 (CONTENT_ADMIN)" value="CONTENT_ADMIN" />
          <el-option label="医疗顾问 (MEDICAL_ADVISOR)" value="MEDICAL_ADVISOR" />
        </el-select>
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
import { getUserList, addUser, updateUser, deleteUser, resetUserPwd } from '../api/user'

const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const filterRole = ref('')
const dialogVisible = ref(false)

// ⚠️ 对齐后端 SysUser entity：roleType（不是 role）
const form = reactive({
  id: null, username: '', password: '', realName: '',
  phone: '', roleType: 'CONTENT_ADMIN', status: 1
})

const ROLE_MAP = {
  SYS_ADMIN: '系统管理员',
  CONTENT_ADMIN: '内容管理员',
  MEDICAL_ADVISOR: '医疗顾问'
}
function roleLabel(r) { return ROLE_MAP[r] || r }
function roleTagType(r) {
  const map = { SYS_ADMIN: 'danger', CONTENT_ADMIN: '', MEDICAL_ADVISOR: 'warning' }
  return map[r] || ''
}

async function load() {
  try {
    const data = await getUserList({
      pageNum: pageNum.value, pageSize: pageSize.value,
      roleType: filterRole.value || undefined
    })
    list.value = data.records || []
    total.value = data.total || 0
  } catch {}
}

function openForm(row) {
  Object.assign(form, {
    id: null, username: '', password: '', realName: '',
    phone: '', roleType: 'CONTENT_ADMIN', status: 1
  })
  if (row) {
    // 把后端返回的 row（含 roleType）拷贝过来
    Object.assign(form, {
      ...row,
      password: ''  // 密码不回显
    })
  }
  dialogVisible.value = true
}

async function save() {
  try {
    if (form.id) {
      await updateUser({
        id: form.id, username: form.username, realName: form.realName,
        phone: form.phone, roleType: form.roleType, status: form.status
      })
      ElMessage.success('更新成功')
    } else {
      if (!form.password) form.password = '123456'
      await addUser({
        username: form.username, password: form.password, realName: form.realName,
        phone: form.phone, roleType: form.roleType, status: form.status
      })
      ElMessage.success('新增成功，默认密码 123456')
    }
    dialogVisible.value = false
    load()
  } catch {}
}

async function toggleStatus(row) {
  try {
    await updateUser({ id: row.id, status: row.status })
    ElMessage.success(row.status === 1 ? '已启用' : '已禁用')
  } catch {
    row.status = row.status === 1 ? 0 : 1  // 回滚
  }
}

async function resetPwd(row) {
  try {
    await ElMessageBox.confirm(`确定重置"${row.username}"的密码为 123456 吗？`, '提示', { type: 'warning' })
    await resetUserPwd(row.id)
    ElMessage.success('密码已重置为 123456')
  } catch {}
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`确定删除账号"${row.username}"吗？`, '提示', { type: 'warning' })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    load()
  } catch {}
}

onMounted(load)
</script>

<style scoped>
.header-bar { display: flex; justify-content: space-between; align-items: center; }
.role-code { font-size: 11px; color: #909399; margin-top: 2px; }
</style>
