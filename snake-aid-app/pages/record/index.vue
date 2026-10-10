<template>
<view class="page">

  <!-- 列表 -->
  <view v-if="mode === 'list'" class="list-wrap">
    <view v-if="recordList.length === 0" class="empty">
      <text class="emoji">📋</text>
      <text class="tip">暂无病历记录</text>
    </view>
    <view v-for="r in recordList" :key="r.id" class="rec" @click="editOne(r)">
      <view class="rec-top">
        <text class="rec-title">{{ r.snakeName || '疑似毒蛇咬伤' }}</text>
        <text class="rec-sev">{{ sevLabel(r.severity) }}</text>
      </view>
      <text class="rec-sub">📍 {{ r.location || '-' }} · {{ r.bitePlace || '-' }}</text>
      <text class="rec-sub">💉 {{ r.serumUsed ? '已用血清' : '未用血清' }}</text>
    </view>
    <view class="fab" @click="openNew">+ 新建</view>
  </view>

  <!-- 表单 -->
  <view v-if="mode === 'form'" class="form-wrap">
    <view class="form-card">
      <text class="title">{{ editingId ? '✏️ 编辑病历' : '📝 新建病历' }}</text>

      <text class="lbl">蛇种</text>
      <view class="input-row"><input class="input" v-model="snakeName" placeholder="如：眼镜蛇" /></view>

      <text class="lbl">毒型</text>
      <view class="chips">
        <text class="chip" :class="{on: venomType===t}" v-for="t in vTypes" :key="t" @click="venomType=t">{{ vLabel(t) }}</text>
      </view>

      <text class="lbl">性别</text>
      <view class="chips">
        <text class="chip" :class="{on: gender==='男'}" @click="gender='男'">男</text>
        <text class="chip" :class="{on: gender==='女'}" @click="gender='女'">女</text>
      </view>

      <text class="lbl">年龄</text>
      <view class="input-row"><input class="input" type="number" v-model="age" placeholder="岁" /></view>

      <text class="lbl">咬伤部位</text>
      <view class="input-row"><input class="input" v-model="location" placeholder="如：右手背" /></view>

      <text class="lbl">咬伤地点</text>
      <view class="input-row"><input class="input" v-model="bitePlace" placeholder="如：广东深圳" /></view>

      <text class="lbl">局部症状</text>
      <view class="textarea-row"><textarea class="textarea" v-model="localSymptoms" placeholder="肿胀、疼痛、水疱..." /></view>

      <text class="lbl">全身症状</text>
      <view class="textarea-row"><textarea class="textarea" v-model="systemicSymptoms" placeholder="头晕、恶心..." /></view>

      <text class="lbl">严重程度</text>
      <view class="chips">
        <text class="chip" :class="{on: severity===s}" v-for="s in sevs" :key="s" @click="severity=s">{{ sevLabel(s) }}</text>
      </view>

      <text class="lbl">是否使用血清</text>
      <view class="chips">
        <text class="chip" :class="{on: serumUsed==='1'}" @click="serumUsed='1'">是</text>
        <text class="chip" :class="{on: serumUsed==='0'}" @click="serumUsed='0'">否</text>
      </view>

      <text class="lbl">血清详情</text>
      <view class="input-row"><input class="input" v-model="serumDetail" placeholder="如：抗眼镜蛇毒血清 4支" /></view>

      <text class="lbl">治疗方案</text>
      <view class="textarea-row"><textarea class="textarea" v-model="treatment" placeholder="急诊处理、手术..." /></view>

      <text class="lbl">住院医院</text>
      <view class="input-row"><input class="input" v-model="hospital" placeholder="医院名称" /></view>

      <text class="lbl">备注</text>
      <view class="textarea-row"><textarea class="textarea" v-model="remark" placeholder="其他说明" /></view>
    </view>

    <view class="btns">
      <view class="btn ghost" @click="back">取消</view>
      <view v-if="editingId" class="btn danger" @click="del">删除</view>
      <view class="btn prim" @click="submit">{{ editingId ? '更新' : '保存' }}</view>
    </view>
  </view>

</view>
</template>

<script>
const API = '/api'

export default {
  data() {
    return {
      mode: 'list',
      editingId: null,
      snakeName: '',
      venomType: 'unknown',
      gender: '男',
      age: '',
      location: '',
      bitePlace: '',
      localSymptoms: '',
      systemicSymptoms: '',
      severity: 'mild',
      serumUsed: '0',
      serumDetail: '',
      treatment: '',
      hospital: '',
      remark: '',
      recordList: [],
      vTypes: ['neurotoxic','hemotoxic','cytotoxic','mixed','unknown'],
      sevs: ['mild','moderate','severe','critical']
    }
  },
  methods: {
    sevLabel(s) { return { mild:'轻度', moderate:'中度', severe:'重度', critical:'极危' }[s] || s },
    vLabel(t) { return { neurotoxic:'神经毒', hemotoxic:'血循毒', cytotoxic:'细胞毒', mixed:'混合毒', unknown:'未识别' }[t] || t },
    request(url, method='GET', body) {
      return new Promise(res => {
        const token = uni.getStorageSync('token') || ''
        const opt = { url: API+url, method, header:{ Authorization:'Bearer '+token }, success: r=>res(r.data), fail: ()=>res({}) }
        if (body) { opt.header['Content-Type']='application/json'; opt.data=body }
        uni.request(opt)
      })
    },
    resetForm() {
      this.editingId = null
      this.snakeName=''; this.venomType='unknown'; this.gender='男'; this.age=''
      this.location=''; this.bitePlace=''; this.localSymptoms=''; this.systemicSymptoms=''
      this.severity='mild'; this.serumUsed='0'; this.serumDetail=''
      this.treatment=''; this.hospital=''; this.remark=''
    },
    async loadList() {
      const j = await this.request('/app/snake-record/mine')
      if (j.code === 200) {
        this.recordList = j.data || []
      } else if (j.code === 401) {
        uni.showModal({
          title: '请先登录',
          content: '查看/新建病历需要登录 APP 账号',
          showCancel: false,
          confirmText: '去登录',
          success: () => { uni.switchTab({ url: '/pages/mine/mine' }) }
        })
      } else if (j.code !== undefined) {
        uni.showToast({ title: j.message || '加载失败', icon: 'none' })
      }
    },
    openNew() { this.resetForm(); this.mode = 'form' },
    editOne(r) {
      this.editingId = r.id
      this.snakeName=r.snakeName||''; this.venomType=r.venomType||'unknown'
      this.gender=r.gender||'男'; this.age=r.age!=null?String(r.age):''
      this.location=r.location||''; this.bitePlace=r.bitePlace||''
      this.localSymptoms=r.localSymptoms||''; this.systemicSymptoms=r.systemicSymptoms||''
      this.severity=r.severity||'mild'; this.serumUsed=r.serumUsed!=null?String(r.serumUsed):'0'
      this.serumDetail=r.serumDetail||''; this.treatment=r.treatment||''
      this.hospital=r.hospital||''; this.remark=r.remark||''
      this.mode = 'form'
    },
    back() { this.mode = 'list'; this.loadList() },
    async submit() {
      const payload = {
        id: this.editingId, snakeName: this.snakeName, venomType: this.venomType,
        gender: this.gender, age: this.age?Number(this.age):null,
        location: this.location, bitePlace: this.bitePlace,
        localSymptoms: this.localSymptoms, systemicSymptoms: this.systemicSymptoms,
        severity: this.severity, serumUsed: Number(this.serumUsed),
        serumDetail: this.serumDetail, treatment: this.treatment,
        hospital: this.hospital, remark: this.remark
      }
      const j = await this.request('/app/snake-record/save', 'POST', payload)
      if (j.code === 200) {
        uni.showToast({ title: this.editingId?'已更新':'已保存', icon:'success' })
        this.resetForm(); this.mode='list'; this.loadList()
      } else {
        uni.showToast({ title: j.msg || '失败', icon:'none' })
      }
    },
    del() {
      uni.showModal({ title:'确认删除', content:'确定删除这条病历？', success: async r=>{
        if (!r.confirm) return
        const j = await this.request('/app/snake-record/'+this.editingId, 'DELETE')
        if (j.code === 200) { uni.showToast({ title:'已删除', icon:'success' }); this.resetForm(); this.mode='list'; this.loadList() }
      }})
    }
  },
  mounted() { this.loadList() }
}
</script>

<style>
.page { min-height: 100vh; background: #F5F5F5; padding: 20rpx; padding-bottom: 120rpx; }

.empty { text-align:center; padding:120rpx 40rpx; }
.empty .emoji { font-size:80rpx; display:block; }
.empty .tip { font-size:28rpx; color:#999; margin-top:20rpx; display:block; }

.rec { background:#fff; border-radius:16rpx; padding:24rpx; margin-bottom:16rpx; }
.rec-top { display:flex; justify-content:space-between; align-items:center; }
.rec-title { font-size:30rpx; font-weight:bold; color:#222; }
.rec-sev { font-size:22rpx; padding:4rpx 14rpx; border-radius:20rpx; background:#E8F5E9; color:#2E7D32; }
.rec-sub { font-size:26rpx; color:#666; display:block; margin-top:6rpx; }

.fab { position:fixed; right:30rpx; bottom:50rpx; background:#6A1B9A; color:#fff; padding:20rpx 40rpx; border-radius:40rpx; font-size:30rpx; box-shadow:0 6rpx 20rpx rgba(106,27,154,0.4); }

.form-card { background:#fff; border-radius:16rpx; padding:28rpx; }
.title { font-size:34rpx; font-weight:bold; color:#222; display:block; margin-bottom:24rpx; }
.lbl { font-size:28rpx; color:#333; display:block; margin:24rpx 0 12rpx; font-weight:500; }

/* ===== 和 login.vue 完全一样的 input 写法 ===== */
.input-row { display:flex; align-items:center; background:#F5F5F5; border-radius:12rpx; padding:0 20rpx; height:88rpx; }
.input { flex:1; font-size:30rpx; color:#222; height:88rpx; }
.textarea-row { background:#F5F5F5; border-radius:12rpx; padding:20rpx; }
.textarea { width:100%; font-size:30rpx; color:#222; min-height:140rpx; line-height:1.6; }
/* ============================================ */

.chips { display:flex; flex-wrap:wrap; gap:16rpx; }
.chip { padding:12rpx 24rpx; background:#F0F0F0; border-radius:30rpx; font-size:26rpx; color:#555; }
.chip.on { background:#6A1B9A; color:#fff; }

.btns { display:flex; gap:16rpx; padding:30rpx 0 40rpx; }
.btn { flex:1; text-align:center; padding:22rpx 0; border-radius:12rpx; font-size:30rpx; }
.btn.prim { background:#6A1B9A; color:#fff; }
.btn.ghost { background:#E0E0E0; color:#555; }
.btn.danger { background:#C62828; color:#fff; }
</style>
