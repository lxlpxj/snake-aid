<template>
  <div class="dashboard">

    <!-- ============ 第一行：6 个核心统计卡片 ============ -->
    <el-row :gutter="16">
      <el-col :xs="12" :sm="6" v-for="c in mainCards" :key="c.key">
        <el-card class="stat-card" shadow="hover" :body-style="{ padding: '16px 20px' }">
          <div class="stat-icon" :style="{ background: c.bg }">{{ c.icon }}</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats[c.key] ?? 0 }}</div>
            <div class="stat-label">{{ c.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- ============ 第二行：今日访问 ============ -->
    <el-row :gutter="16" style="margin-top:16px;">
      <el-col :xs="12" :sm="6" v-for="c in todayCards" :key="c.key">
        <el-card class="stat-card small" shadow="hover" :body-style="{ padding: '12px 16px' }">
          <div class="stat-icon small" :style="{ background: c.bg }">{{ c.icon }}</div>
          <div class="stat-info">
            <div class="stat-value small">{{ stats[c.key] ?? 0 }}</div>
            <div class="stat-label">{{ c.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- ============ 第三行：省份热度 + 蛇种属性 ============ -->
    <el-row :gutter="16" style="margin-top:16px;">
      <!-- 省份热度 TOP10 -->
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">🗺️ 省份筛选热度 TOP10</span>
          </template>
          <el-table :data="provinceTop" size="small" stripe empty-text="暂无数据，请先在 App 端浏览各省蛇种">
            <el-table-column type="index" label="#" width="45" align="center">
              <template #default="scope">
                <el-tag v-if="scope.$index < 3" type="danger" size="small" effect="dark">{{ scope.$index + 1 }}</el-tag>
                <span v-else style="color:#999">{{ scope.$index + 1 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="province" label="省份" />
            <el-table-column label="筛选次数" width="120">
              <template #default="{ row }">
                <el-progress :percentage="getPercent(row.cnt)" :stroke-width="12" :show-text="false" />
              </template>
            </el-table-column>
            <el-table-column prop="cnt" label="次数" width="70" align="center">
              <template #default="{ row }"><strong>{{ row.cnt }}</strong></template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 蛇种属性分布 -->
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">🐍 蛇种属性分布</span>
          </template>
          <el-tabs v-model="snakeTab">
            <el-tab-pane label="有毒 / 无毒" name="toxic">
              <div v-if="snakeTraits.toxicity?.length" class="trait-list">
                <div v-for="r in snakeTraits.toxicity" :key="r.is_poisonous" class="trait-row">
                  <span class="trait-label">{{ r.is_poisonous == 1 ? '⚠️ 有毒蛇' : '🌱 无毒蛇' }}</span>
                  <el-progress :percentage="getPercent(r.cnt, 50)" :color="r.is_poisonous == 1 ? '#f56c6c' : '#67c23a'" />
                  <span class="trait-count">{{ r.cnt }} 种</span>
                </div>
              </div>
              <el-empty v-else description="暂无蛇种数据" />
            </el-tab-pane>
            <el-tab-pane label="毒型分布" name="venom">
              <div v-if="snakeTraits.venomType?.length" class="trait-list">
                <div v-for="r in snakeTraits.venomType" :key="r.venom_type" class="trait-row">
                  <span class="trait-label">{{ r.venom_type || '未知' }}</span>
                  <el-progress :percentage="getPercent(r.cnt, 20)" color="#e6a23c" />
                  <span class="trait-count">{{ r.cnt }} 种</span>
                </div>
              </div>
              <el-empty v-else description="暂无数据" />
            </el-tab-pane>
            <el-tab-pane label="审核状态" name="hazard">
              <div v-if="snakeTraits.hazard?.length" class="trait-list">
                <div v-for="r in snakeTraits.hazard" :key="r.hazard_level" class="trait-row">
                  <span class="trait-label">状态 {{ r.hazard_level }}</span>
                  <el-progress :percentage="getPercent(r.cnt, 20)" color="#f56c6c" />
                  <span class="trait-count">{{ r.cnt }} 种</span>
                </div>
              </div>
              <el-empty v-else description="暂无数据" />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <!-- ============ 第四行：考试统计 + 错题 TOP ============ -->
    <el-row :gutter="16" style="margin-top:16px;">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">📝 考试成绩总览</span>
          </template>
          <el-descriptions :column="2" border size="small" v-if="quizAggregate">
            <el-descriptions-item label="总考试次数">{{ quizAggregate.total ?? 0 }}</el-descriptions-item>
            <el-descriptions-item label="平均分数">
              <el-tag type="warning">{{ (quizAggregate.avg_score ?? 0).toFixed(1) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="最高分数">
              <el-tag type="success">{{ quizAggregate.max_score ?? 0 }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="最低分数">
              <el-tag type="danger">{{ quizAggregate.min_score ?? 0 }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>

          <div v-if="quizByCategory?.length" style="margin-top:16px;">
            <div style="font-weight:600;margin-bottom:8px;">按分类平均分</div>
            <div v-for="c in quizByCategory" :key="c.category" class="trait-row">
              <span class="trait-label">{{ c.category || '综合' }}</span>
              <el-progress :percentage="Math.min(100, c.avg_score || 0)" color="#409eff" />
              <span class="trait-count">{{ (c.avg_score ?? 0).toFixed(1) }} 分</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">❌ 高频错题 TOP5</span>
          </template>
          <el-table :data="quizTopWrong" size="small" stripe empty-text="暂无错题数据">
            <el-table-column type="index" label="#" width="45" align="center" />
            <el-table-column prop="question" label="题目" show-overflow-tooltip>
              <template #default="{ row }">
                {{ (row.question || '').slice(0, 30) }}{{ (row.question || '').length > 30 ? '...' : '' }}
              </template>
            </el-table-column>
            <el-table-column prop="wrong_count" label="答错次数" width="90" align="center">
              <template #default="{ row }">
                <el-tag type="danger" size="small">{{ row.wrong_count }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- ============ 第五行：用户积分排行榜 + 等级分布 ============ -->
    <el-row :gutter="16" style="margin-top:16px;">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">🏆 用户积分排行榜 TOP10</span>
          </template>
          <el-table :data="leaderboard" size="small" stripe empty-text="暂无数据">
            <el-table-column type="index" label="#" width="45" align="center">
              <template #default="scope">
                <el-tag v-if="scope.$index < 3" type="warning" size="small" effect="dark">{{ scope.$index + 1 }}</el-tag>
                <span v-else style="color:#999">{{ scope.$index + 1 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="nickname" label="用户" />
            <el-table-column label="等级" width="90" align="center">
              <template #default="{ row }"><el-tag size="small">Lv.{{ row.level || 1 }}</el-tag></template>
            </el-table-column>
            <el-table-column label="积分" width="90" align="center">
              <template #default="{ row }"><strong>{{ row.points || 0 }}</strong></template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">📊 用户等级分布</span>
          </template>
          <el-table :data="levelDist" size="small" stripe empty-text="暂无数据">
            <el-table-column prop="level" label="等级" width="70" align="center">
              <template #default="{ row }">Lv.{{ row.level }}</template>
            </el-table-column>
            <el-table-column prop="cnt" label="人数" width="80" align="center" />
            <el-table-column label="占比">
              <template #default="{ row }">
                <el-progress :percentage="levelPercent(row.cnt)" :stroke-width="14" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- ============ 第六行：近 14 天趋势 ============ -->
    <el-card shadow="hover" style="margin-top:16px;">
      <template #header>
        <span class="card-title">📈 近 14 天访问趋势</span>
      </template>
      <el-table :data="trendRows" size="small" stripe empty-text="暂无访问记录">
        <el-table-column prop="stat_date" label="日期" width="110" />
        <el-table-column v-for="e in eventTypes" :key="e" :label="eventLabel(e)" min-width="120">
          <template #default="{ row }">
            <span :style="{ color: row[e] ? '#333' : '#ccc', fontWeight: row[e] ? 600 : 400 }">
              {{ row[e] || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="当日合计" width="100">
          <template #default="{ row }">
            <el-tag type="info">{{ row._total || 0 }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

// ====== 数据 ======
const stats = ref({})
const provinceTop = ref([])
const snakeTraits = ref({ toxicity: [], venomType: [], hazard: [] })
const quizAggregate = ref(null)
const quizByCategory = ref([])
const quizTopWrong = ref([])
const trendRows = ref([])
const leaderboard = ref([])
const levelDist = ref([])
const snakeTab = ref('toxic')

// ====== 卡片配置 ======
const mainCards = [
  { key: 'snakeCount',    label: '🐍 蛇种总数',  icon: '🐍', bg: '#ff6b6b' },
  { key: 'hospitalCount', label: '🏥 合作医院',  icon: '🏥', bg: '#4ecdc4' },
  { key: 'articleCount',  label: '📚 科普文章',  icon: '📚', bg: '#45b7d1' },
  { key: 'userCount',     label: '👥 注册用户',  icon: '👥', bg: '#96ceb4' },
  { key: 'quizTotal',     label: '📝 考试总数',  icon: '📝', bg: '#a29bfe' },
  { key: 'questionTotal', label: '❓ 题库数量',  icon: '❓', bg: '#fd79a8' },
]

const todayCards = [
  { key: 'snakeFilterToday',  label: '今日蛇种筛选', icon: '🔍', bg: '#74b9ff' },
  { key: 'snakeViewToday',    label: '今日蛇种详情', icon: '🐍', bg: '#55efc4' },
  { key: 'hospitalViewToday', label: '今日医院查询', icon: '🏥', bg: '#ffeaa7' },
  { key: 'quizSubmitToday',   label: '今日考试提交', icon: '✅', bg: '#fab1a0' },
]

// ====== 事件类型 ======
const eventTypes = ['snake_filter', 'snake_view', 'hospital_view', 'quiz_submit']
function eventLabel(e) {
  return { snake_filter: '蛇种筛选', snake_view: '蛇种详情', hospital_view: '医院查询', quiz_submit: '考试提交' }[e] || e
}

// ====== 工具 ======
function getPercent(val, maxHint) {
  const base = maxHint || Math.max(...provinceTop.value.map(r => r.cnt || 1), 1)
  return Math.min(100, Math.round((val / base) * 100))
}
function levelPercent(cnt) {
  const total = levelDist.value.reduce((a, b) => a + (b.cnt || 0), 0) || 1
  return Math.round((cnt / total) * 100)
}

// ====== 加载数据 ======
async function load() {
  try {
    stats.value = await request.get('/admin/statistics/overview') || {}
  } catch (e) { console.error('[overview]', e?.message) }

  try {
    provinceTop.value = await request.get('/admin/statistics/province') || []
  } catch (e) { console.error('[province]', e?.message) }

  try {
    snakeTraits.value = await request.get('/admin/statistics/snake-traits') || { toxicity: [], venomType: [], hazard: [] }
  } catch (e) { console.error('[snake-traits]', e?.message) }

  try {
    const qs = await request.get('/admin/statistics/quiz-stats') || {}
    quizAggregate.value = qs.aggregate || null
    quizByCategory.value = qs.byCategory || []
    quizTopWrong.value = qs.topWrongQuestions || []
  } catch (e) { console.error('[quiz-stats]', e?.message) }

  try {
    leaderboard.value = await request.get('/admin/statistics/leaderboard?topN=10') || []
  } catch (e) { console.error('[leaderboard]', e?.message) }

  try {
    levelDist.value = await request.get('/admin/statistics/level-distribution') || []
  } catch (e) { console.error('[level-distribution]', e?.message) }

  try {
    const rows = await request.get('/admin/statistics/trend') || []
    const map = new Map()
    for (const r of rows) {
      const key = r.stat_date
      if (!map.has(key)) map.set(key, { stat_date: key, _total: 0 })
      map.get(key)[r.event_type] = r.cnt
      map.get(key)._total += r.cnt
    }
    trendRows.value = Array.from(map.values()).sort((a, b) => a.stat_date < b.stat_date ? 1 : -1)
  } catch (e) { console.error('[trend]', e?.message) }
}

onMounted(load)
</script>

<style scoped>
.dashboard { /* default padding from Layout */ }

.stat-card { display: flex; align-items: center; gap: 14px; border-radius: 10px; }
.stat-card.small { gap: 10px; }

.stat-icon {
  width: 54px; height: 54px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 26px; color: #fff; flex-shrink: 0;
}
.stat-icon.small { width: 42px; height: 42px; font-size: 20px; border-radius: 10px; }

.stat-info { flex: 1; min-width: 0; }
.stat-value { font-size: 26px; font-weight: 700; color: #1e293b; line-height: 1.2; }
.stat-value.small { font-size: 20px; }
.stat-label { font-size: 12px; color: #94a3b8; margin-top: 2px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.card-title { font-weight: 600; color: #1e293b; font-size: 15px; }

.trait-list { padding: 4px 0; }
.trait-row {
  display: flex; align-items: center; gap: 12px;
  margin-bottom: 10px;
}
.trait-label { width: 100px; font-size: 13px; color: #475569; flex-shrink: 0; }
.trait-count { width: 60px; text-align: right; font-size: 13px; font-weight: 600; color: #1e293b; }

:deep(.el-progress) { flex: 1; }
</style>
