<template>
  <el-card>
    <template #header>
      <div class="header-bar">
        <span>医院管理</span>
        <el-button type="primary" :icon="Plus" @click="openHospitalForm()">新增医院</el-button>
      </div>
    </template>

    <el-table :data="hospitalRows" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="医院名称" width="180" show-overflow-tooltip />
      <el-table-column prop="level" label="等级" width="80" />
      <el-table-column label="地区" width="180">
        <template #default="{ row }">
          {{ row.province || '' }}{{ row.city ? ' · ' + row.city : '' }}
        </template>
      </el-table-column>
      <el-table-column label="有血清" width="90">
        <template #default="{ row }">
          <el-tag :type="row.hasSerum === 1 ? 'success' : 'info'" size="small">
            {{ row.hasSerum === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="电话" width="140" />
      <el-table-column prop="address" label="详细地址" show-overflow-tooltip />
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openHospitalForm(row)">编辑</el-button>
          <el-button size="small" type="primary" link @click="showSerum(row)">血清库存</el-button>
          <el-button size="small" type="danger" link @click="removeHospital(row)">删除</el-button>
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

  <!-- ============ 新增/编辑医院 ============ -->
  <el-dialog v-model="hospitalDialog" :title="hospital.id ? '编辑医院' : '新增医院'" width="860px" top="5vh">
    <el-form :model="hospital" label-width="100px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="医院名称" required><el-input v-model="hospital.name" /></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="等级"><el-input v-model="hospital.level" placeholder="如:三甲/三乙" /></el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="8"><el-form-item label="省份"><el-input v-model="hospital.province" placeholder="如:广东省" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="城市"><el-input v-model="hospital.city" placeholder="如:广州市" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="电话"><el-input v-model="hospital.phone" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="详细地址"><el-input v-model="hospital.address" placeholder="街道门牌号" /></el-form-item>

      <!-- ====== 地图选点 ====== -->
      <el-form-item label="地图选点">
        <div class="map-wrap">
          <div class="map-controls">
            <el-input
              v-model="mapKeyword"
              placeholder="搜索地址: 如 广州市第一人民医院"
              style="width:400px;"
              @keyup.enter="searchAddress">
              <template #append>
                <el-button :icon="Search" @click="searchAddress">搜索</el-button>
              </template>
            </el-input>
            <el-button :icon="Location" type="success" plain @click="focusMap" :disabled="!mapInstance">居中到已有坐标</el-button>
            <span style="margin-left:8px;font-size:12px;color:#909399;">💡 点击地图也可直接选点，自动填省市区</span>
          </div>

          <div ref="mapContainerRef" class="leaflet-map"></div>

          <div class="coord-row">
            <div class="coord-item">
              <span>经度:</span>
              <el-input-number v-model="hospital.longitude" :precision="6" :step="0.001" :controls="false" style="width:180px;" placeholder="点击地图自动填充" />
            </div>
            <div class="coord-item">
              <span>纬度:</span>
              <el-input-number v-model="hospital.latitude" :precision="6" :step="0.001" :controls="false" style="width:180px;" placeholder="点击地图自动填充" />
            </div>
            <span v-if="hospital.longitude && hospital.latitude" style="font-size:12px;color:#67c23a;">✓ 已选坐标</span>
          </div>
        </div>
      </el-form-item>

      <el-form-item label="有血清">
        <el-switch v-model="hospital.hasSerum" :active-value="1" :inactive-value="0" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="hospitalDialog = false">取消</el-button>
      <el-button type="primary" @click="saveHospital">保存</el-button>
    </template>
  </el-dialog>

  <!-- ============ 血清库存（外层弹窗） ============ -->
  <el-dialog v-model="serumDialog" :title="'血清库存 - ' + (currentHospital?.name || '')" width="780px">
    <div style="margin-bottom:12px;display:flex;justify-content:space-between;align-items:center;">
      <span style="color:#909399;font-size:13px;">共 {{ serumRows.length }} 条血清记录</span>
      <el-button type="primary" :icon="Plus" @click="openSerumForm()">新增血清</el-button>
    </div>
    <el-table :data="serumRows" border size="small" empty-text="暂无血清库存，点击右上角新增">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="serumName" label="血清名称" width="180" />
      <el-table-column label="对应蛇种" width="150">
        <template #default="{ row }">
          <el-tag v-if="row.snakeId && snakeNameMap[row.snakeId]" size="small">{{ snakeNameMap[row.snakeId] }}</el-tag>
          <span v-else style="color:#c0c4cc;font-size:12px;">未关联</span>
        </template>
      </el-table-column>
      <el-table-column prop="quantity" label="库存" width="80" />
      <el-table-column prop="expireDate" label="有效期" width="110" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="openSerumForm(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="removeSerum(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>

  <!-- ============ 新增/编辑血清（嵌套子弹窗） ============ -->
  <el-dialog v-model="serumFormDialog" :title="serumForm.id ? '编辑血清' : '新增血清'" width="500px" append-to-body>
    <el-form :model="serumForm" label-width="90px">
      <el-form-item label="血清名称" required>
        <el-input v-model="serumForm.serumName" placeholder="如: 抗蝮蛇毒血清" />
      </el-form-item>
      <el-form-item label="对应蛇种">
        <el-select v-model="serumForm.snakeId" placeholder="选择蛇种（可空）" clearable style="width:100%;">
          <el-option v-for="s in snakeOptions" :key="s.id" :label="s.name + (s.speciesName ? ' (' + s.speciesName + ')' : '')" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="库存数量" required>
            <el-input-number v-model="serumForm.quantity" :min="0" style="width:100%;" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="有效期">
            <el-date-picker v-model="serumForm.expireDate" type="date" value-format="YYYY-MM-DD" style="width:100%;" placeholder="选填" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="serumFormDialog = false">取消</el-button>
      <el-button type="primary" @click="saveSerum">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Location } from '@element-plus/icons-vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

import {
  hospitalPage, addHospital, updateHospital, deleteHospital,
  serumList, createSerum, updateSerum, deleteSerum
} from '../api/hospital'
import { snakeList } from '../api/snake'
import request from '../utils/request'

// ========== 医院 ==========
const hospitalRows = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const hospitalDialog = ref(false)

const hospital = reactive({
  id: null, name: '', level: '', province: '', city: '', address: '',
  phone: '', longitude: null, latitude: null, hasSerum: 0
})

async function load() {
  try {
    const data = await hospitalPage({ pageNum: pageNum.value, pageSize: pageSize.value })
    hospitalRows.value = data.records || []
    total.value = data.total || 0
  } catch {}
}

function openHospitalForm(row) {
  Object.assign(hospital, {
    id: null, name: '', level: '', province: '', city: '', address: '',
    phone: '', longitude: null, latitude: null, hasSerum: 0
  })
  if (row) Object.assign(hospital, row)
  hospitalDialog.value = true
  nextTick(initMap)
}

async function saveHospital() {
  try {
    if (hospital.id) { await updateHospital(hospital); ElMessage.success('更新成功') }
    else { await addHospital(hospital); ElMessage.success('新增成功') }
    hospitalDialog.value = false
    load()
  } catch {}
}

async function removeHospital(row) {
  try {
    await ElMessageBox.confirm(`确定删除医院"${row.name}"吗？`, '提示', { type: 'warning' })
    await deleteHospital(row.id)
    ElMessage.success('删除成功'); load()
  } catch {}
}

// ========== 血清 ==========
const serumRows = ref([])
const serumDialog = ref(false)
const serumFormDialog = ref(false)        // 子弹窗单独状态
const currentHospital = ref(null)
const snakeOptions = ref([])
const snakeNameMap = ref({})
const serumForm = reactive({
  id: null, hospitalId: null, snakeId: null,
  serumName: '', quantity: 0, expireDate: null
})

async function loadSnakeOptions() {
  try {
    const data = await snakeList({ pageNum: 1, pageSize: 500 })
    const snakes = data.records || []
    snakeOptions.value = snakes.map(s => ({ id: s.id, name: s.name, speciesName: s.speciesName || '' }))
    snakeNameMap.value = {}
    snakes.forEach(s => { snakeNameMap.value[s.id] = s.name })
  } catch {}
}

async function showSerum(row) {
  currentHospital.value = row
  serumDialog.value = true
  try { serumRows.value = (await serumList(row.id)) || [] } catch {}
  loadSnakeOptions()
}

function openSerumForm(row) {
  Object.assign(serumForm, {
    id: null, hospitalId: currentHospital.value?.id || null,
    snakeId: null, serumName: '', quantity: 0, expireDate: null
  })
  if (row) Object.assign(serumForm, row)
  serumFormDialog.value = true
}

async function saveSerum() {
  if (!serumForm.serumName) { ElMessage.warning('血清名称必填'); return }
  try {
    if (serumForm.id) {
      await updateSerum(serumForm)
    } else {
      await createSerum(serumForm.hospitalId, serumForm)
    }
    ElMessage.success('保存成功')
    serumFormDialog.value = false
    serumRows.value = (await serumList(currentHospital.value.id)) || []
  } catch {}
}

async function removeSerum(row) {
  try {
    await ElMessageBox.confirm('确定删除这条血清记录吗？', '提示', { type: 'warning' })
    await deleteSerum(row.id)
    ElMessage.success('删除成功')
    serumRows.value = (await serumList(currentHospital.value.id)) || []
  } catch {}
}

// ========== Leaflet 地图 ==========
const mapContainerRef = ref(null)
const mapKeyword = ref('')
let mapInstance = null
let markerInstance = null

// 自定义 marker（用 divIcon 避免默认图标 404）
const redMarkerIcon = L.divIcon({
  className: 'custom-marker',
  html: '<div style="width:16px;height:16px;border-radius:50%;background:#f56c6c;border:3px solid #fff;box-shadow:0 1px 4px rgba(0,0,0,0.4);"></div>',
  iconSize: [16, 16],
  iconAnchor: [8, 8]
})

function initMap() {
  if (!mapContainerRef.value) return

  if (mapInstance) {
    mapInstance.remove()
    mapInstance = null
  }

  const hasCoord = hospital.longitude && hospital.latitude
  const center = hasCoord ? [hospital.latitude, hospital.longitude] : [34.2, 108.9] // 西安默认
  const zoom = hasCoord ? 15 : 5

  mapInstance = L.map(mapContainerRef.value, {
    center: center,
    zoom: zoom,
    zoomControl: true,
    attributionControl: false
  })

  // 高德中文瓦片（免费，无需 key）
  L.tileLayer('https://webrd0{s}.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}', {
    subdomains: ['0', '1', '2', '3'],
    maxZoom: 18
  }).addTo(mapInstance)

  // 地图点击 → 设坐标 + 逆地理编码填省市区
  mapInstance.on('click', async (e) => {
    hospital.latitude = Number(e.latlng.lat.toFixed(6))
    hospital.longitude = Number(e.latlng.lng.toFixed(6))
    placeMarker(e.latlng.lat, e.latlng.lng)
    await reverseGeocode(e.latlng.lng, e.latlng.lat)
  })

  // 已有坐标 → 放 marker
  if (hasCoord) {
    placeMarker(hospital.latitude, hospital.longitude)
  }

  // 让 Leaflet 重新测量容器大小（dialog 刚打开时容器可能 0 宽）
  setTimeout(() => mapInstance.invalidateSize(), 100)
}

function placeMarker(lat, lng) {
  if (markerInstance) {
    markerInstance.setLatLng([lat, lng])
  } else {
    markerInstance = L.marker([lat, lng], { icon: redMarkerIcon }).addTo(mapInstance)
  }
}

function focusMap() {
  if (!mapInstance) return
  if (hospital.longitude && hospital.latitude) {
    mapInstance.setView([hospital.latitude, hospital.longitude], 16)
    placeMarker(hospital.latitude, hospital.longitude)
  } else {
    ElMessage.info('先在地图上点击选点')
  }
}

// 搜索地址 → 后端代理高德地理编码
async function searchAddress() {
  if (!mapKeyword.value.trim()) return
  try {
    const data = await request({
      url: '/admin/hospital/geo-search',
      method: 'get',
      params: { q: mapKeyword.value.trim() }
    })
    const list = data || []
    if (list.length === 0) {
      ElMessage.warning('未搜索到相关地点（请确认后端已配置高德 Web API Key）')
      return
    }
    applySearchResult(list[0])
  } catch (err) {
    ElMessage.error('搜索失败：' + (err.message || '网络错误'))
  }
}

function applySearchResult(r) {
  const lat = parseFloat(r.lat || r.latitude)
  const lon = parseFloat(r.lng || r.lon || r.longitude)
  if (!lat || !lon) return
  hospital.latitude = Number(lat.toFixed(6))
  hospital.longitude = Number(lon.toFixed(6))
  if (r.province) hospital.province = r.province
  if (r.city) hospital.city = r.city
  if (r.address || r.display_name) hospital.address = r.address || r.display_name
  if (!hospital.name && r.name) hospital.name = r.name
  if (mapInstance) {
    mapInstance.setView([lat, lon], 16)
    placeMarker(lat, lon)
  }
  ElMessage.success('已定位: ' + (r.name || r.address || r.display_name))
}

// 逆地理编码 → 自动填省份/城市/详细地址
async function reverseGeocode(lng, lat) {
  try {
    const r = await request({
      url: '/admin/hospital/geo-reverse',
      method: 'get',
      params: { lng, lat }
    })
    if (r.province) hospital.province = r.province
    if (r.city) hospital.city = r.city
    if (r.formatted_address || r.address) hospital.address = r.formatted_address || r.address
  } catch {
    // 静默失败，不影响选点
  }
}

onMounted(load)
</script>

<style scoped>
.header-bar { display: flex; justify-content: space-between; align-items: center; }

/* Leaflet 地图 */
.leaflet-map {
  width: 100%;
  height: 340px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
  overflow: hidden;
}
/* 修复 Leaflet 默认灰色背景闪烁 */
.leaflet-container { background: #aad3df; font-family: inherit; }

.map-wrap { width: 100%; }
.map-controls { display: flex; align-items: center; margin-bottom: 8px; flex-wrap: wrap; gap: 8px; }

.coord-row {
  margin-top: 8px;
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}
.coord-item { display: flex; align-items: center; gap: 6px; }
.coord-item > span { font-size: 13px; color: #606266; }
</style>
