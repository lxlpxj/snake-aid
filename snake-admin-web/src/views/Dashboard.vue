<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background:#ff6b6b;">🐍</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.snakeCount }}</div>
            <div class="stat-label">蛇种类数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background:#4ecdc4;">📚</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.articleCount }}</div>
            <div class="stat-label">科普文章</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background:#45b7d1;">🏥</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.hospitalCount }}</div>
            <div class="stat-label">合作医院</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background:#96ceb4;">👥</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.userCount }}</div>
            <div class="stat-label">注册用户</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top:20px;">
      <el-col :span="12">
        <el-card>
          <template #header><strong>角色权限说明</strong></template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="系统管理员">账号权限 / 操作日志 / 全局数据看板</el-descriptions-item>
            <el-descriptions-item label="内容管理员">蛇种库 / 科普文章 / 医院血清 的增删改 + 提交审核</el-descriptions-item>
            <el-descriptions-item label="医疗顾问">审核急救指南和科普内容准确性 / 维护急救指南</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><strong>紧急联系</strong></template>
          <el-table :data="emergency" border size="small">
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="phone" label="电话" />
            <el-table-column prop="note" label="说明" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const stats = ref({ snakeCount: 0, articleCount: 0, hospitalCount: 0, userCount: 0 })

const emergency = [
  { name: '急救中心', phone: '120', note: '紧急医疗救助' },
  { name: '报警电话', phone: '110', note: '紧急求助' },
  { name: '森林消防', phone: '12119', note: '野外救援' }
]

onMounted(async () => {
  try {
    // 调后端统一统计接口
    const data = await request.get('/admin/statistics/overview')
    stats.value = {
      snakeCount: data.snakeCount || 0,
      hospitalCount: data.hospitalCount || 0,
      articleCount: data.articleCount || 0,
      userCount: data.userCount || 0
    }
  } catch (e) {
    console.error('[Dashboard] 加载统计失败:', e?.message)
  }
})
</script>

<style scoped>
.stat-card { display: flex; align-items: center; gap: 20px; }
.stat-icon {
  width: 60px; height: 60px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 28px; color: #fff;
}
.stat-value { font-size: 28px; font-weight: 700; color: #333; }
.stat-label { font-size: 13px; color: #999; }
</style>
