<template>
  <view class="page">
    <!-- 顶部标题 -->
    <view class="top-bar">
      <text class="top-title">🐍 认 蛇 图 鉴</text>
      <text class="top-sub">玩一认一练一测 · 四步闭环</text>
    </view>

    <!-- 4 步进度条 -->
    <view class="step-bar">
      <view v-for="(s,i) in steps" :key="i" class="step-item" 
            :class="{ active: currentStep===i, done: currentStep>i }" 
            @click="gotoStep(i)">
        <view class="step-num">{{ currentStep>i ? '✓' : (i+1) }}</view>
        <text class="step-label">{{ s }}</text>
      </view>
    </view>

    <!-- ============ 第1步：玩一玩（场景题）============ -->
    <view v-if="currentStep===0" class="step-panel">
      <view class="panel-header">
        <text class="panel-emoji">🎮</text>
        <text class="panel-title">玩一玩 · 情境小游戏</text>
        <text class="panel-desc">不同场景遇到蛇，你选什么？</text>
      </view>
      <view class="scene-card">
        <view class="scene-svg-wrap">
          <svg viewBox="0 0 400 220" class="scene-svg">
            <rect width="400" height="220" fill="#E8F5E9"/>
            <rect y="150" width="400" height="70" fill="#81C784"/>
            <path d="M180 220 L200 150 L220 150 L240 220 Z" fill="#D7CCC8"/>
            <!-- 人物 -->
            <g transform="translate(50,90)" class="anim-person-stand">
              <circle cx="15" cy="15" r="12" fill="#FFCCBC"/>
              <circle cx="10" cy="13" r="2" fill="#5D4037"/>
              <circle cx="20" cy="13" r="2" fill="#5D4037"/>
              <path d="M10 20 Q15 23 20 20" fill="none" stroke="#5D4037" stroke-width="1.5"/>
              <rect x="8" y="28" width="14" height="25" rx="3" fill="#1565C0"/>
              <rect x="9" y="53" width="5" height="15" rx="2" fill="#795548"/>
              <rect x="16" y="53" width="5" height="15" rx="2" fill="#795548"/>
            </g>
            <!-- 蛇 -->
            <g transform="translate(260,140)" class="anim-s">
              <path d="M0 40 Q20 10 40 30 Q60 50 80 20 Q100 -5 110 15" fill="none" stroke="#558B2F" stroke-width="10" stroke-linecap="round"/>
              <circle cx="105" cy="12" r="3" fill="#FFEB3B"/>
              <path d="M112 16 L120 14 M112 16 L120 18" stroke="#D32F2F" stroke-width="2"/>
              <g transform="translate(50,-25)" class="anim-warn">
                <ellipse rx="28" ry="18" fill="#FFEB3B" stroke="#F57F17" stroke-width="2"/>
                <text text-anchor="middle" y="5" font-size="14" fill="#D84315">⚠ 草丛有蛇！</text>
              </g>
            </g>
          </svg>
        </view>
        <text class="scene-q">你走在田埂上，突然发现前方草丛有蛇！怎么办？</text>
        <view class="opt-grid">
          <view v-for="(o,i) in playOptions" :key="i" class="opt-btn"
                :class="{ right: picked===i && o.ok, wrong: picked===i && !o.ok }"
                @click="pickPlay(i)">
            <text class="opt-icon">{{ o.icon }}</text>
            <text class="opt-text">{{ o.text }}</text>
          </view>
        </view>
        <view v-if="picked>=0" class="scene-feedback" :class="{ good: playOptions[picked].ok, bad: !playOptions[picked].ok }">
          <text>{{ playOptions[picked].ok ? '✅ 好样的！安全第一！' : '❌ 危险！那样会激怒蛇！' }}</text>
          <text class="fb-detail">{{ playOptions[picked].fb }}</text>
        </view>
      </view>
      <button class="btn-next" :disabled="picked<0" @click="currentStep=1">→ 下一步：认一认</button>
    </view>

    <!-- ============ 第2步：认一认（蛇种图鉴）============ -->
    <view v-if="currentStep===1" class="step-panel">
      <view class="panel-header">
        <text class="panel-emoji">🔍</text>
        <text class="panel-title">认一认 · 12 种常见蛇</text>
        <text class="panel-desc">看头型、花纹、颜色，认清楚每一种</text>
      </view>
      <!-- 筛选 -->
      <view class="filter-row">
        <view class="chip" :class="{on:filter==='all'}" @click="filter='all'">全部</view>
        <view class="chip danger" :class="{on:filter==='poison'}" @click="filter='poison'">⚠️ 有毒</view>
        <view class="chip safe" :class="{on:filter==='safe'}" @click="filter='safe'">✅ 无毒</view>
      </view>
      <!-- 蛇种网格 -->
      <view class="snake-grid">
        <view v-for="s in filteredSnakes" :key="s.id" class="snake-card"
              :class="{danger:s.isVenomous===1}" @click="openDetail(s)">
          <view class="snake-svg-box">
            <svg viewBox="0 0 120 60" class="snake-svg-mini">
              <!-- 蛇身 -->
              <path :d="s.svgPath || defaultPath" 
                    fill="none" :stroke="s.isVenomous===1 ? '#B71C1C' : '#388E3C'" 
                    stroke-width="8" stroke-linecap="round"/>
              <!-- 头 -->
              <g :transform="'translate('+(s.headX||100)+','+(s.headY||30)+')'">
                <polygon v-if="s.headShape==='三角形'" points="0,-8 15,0 0,8" fill="#B71C1C"/>
                <ellipse v-else cx="7" cy="0" rx="12" ry="8" :fill="s.isVenomous===1 ? '#D32F2F' : '#4CAF50'"/>
                <circle v-if="s.isVenomous===1" cx="12" cy="-2" r="1.5" fill="#FFEB3B"/>
              </g>
            </svg>
            <view class="venom-badge" v-if="s.isVenomous===1">⚠️ 毒</view>
            <view class="safe-badge" v-else>✓ 无毒</view>
          </view>
          <text class="snake-name">{{ s.name }}</text>
          <text class="snake-latin">{{ s.scientificName || '' }}</text>
          <text class="snake-feature">{{ s.headShape }} · {{ s.pattern }}</text>
        </view>
      </view>
      <button class="btn-next" @click="currentStep=2">→ 下一步：练一练</button>
    </view>

    <!-- ============ 第3步：练一练（特征选蛇）============ -->
    <view v-if="currentStep===2" class="step-panel">
      <view class="panel-header">
        <text class="panel-emoji">🧠</text>
        <text class="panel-title">练一练 · 看特征选蛇</text>
        <text class="panel-desc">给出头型/花纹/颜色，你能选出是哪种蛇吗？</text>
      </view>
      <view class="quiz-card">
        <view class="quiz-progress">第 {{ trainIdx+1 }} / {{ trainPool.length }} 题</view>
        <view class="quiz-features">
          <view class="feat-tag">头型：{{ trainQ.headShape }}</view>
          <view class="feat-tag">花纹：{{ trainQ.pattern }}</view>
          <view class="feat-tag">颜色：{{ trainQ.color }}</view>
          <view class="feat-tag" v-if="trainQ.isVenomous===1">⚠️ 有毒</view>
        </view>
        <text class="quiz-q">这条蛇最可能是？</text>
        <view class="opt-grid">
          <view v-for="(o,i) in trainOpts" :key="i" class="opt-btn big"
                :class="{ right: trainPicked===i && o===trainQ.name, wrong: trainPicked===i && o!==trainQ.name }"
                @click="pickTrain(i)">
            <text class="opt-emoji">🐍</text>
            <text class="opt-text">{{ o }}</text>
          </view>
        </view>
        <view v-if="trainPicked>=0" class="scene-feedback" :class="{ good: trainOpts[trainPicked]===trainQ.name }">
          <text>{{ trainOpts[trainPicked]===trainQ.name ? '✅ 答对了！' : '❌ 不对哦~' }}</text>
          <text class="fb-detail">{{ trainQ.name }}：{{ trainQ.appearanceDesc || trainQ.symptoms || '分布于 '+(trainQ.distribution||'南方山林') }}</text>
        </view>
        <button v-if="trainPicked>=0" class="btn-next-small" @click="nextTrain">下一题 →</button>
      </view>
      <button class="btn-next" @click="currentStep=3">→ 下一步：测一测</button>
    </view>

    <!-- ============ 第4步：测一测（闯关测试）============ -->
    <view v-if="currentStep===3" class="step-panel">
      <view class="panel-header">
        <text class="panel-emoji">🏆</text>
        <text class="panel-title">测一测 · 最终大考</text>
        <text class="panel-desc">10 道闯关题，80 分以上才算过！</text>
      </view>
      <!-- 未开始 -->
      <view v-if="quizState==='idle'" class="quiz-start">
        <text class="quiz-intro">📝 10 道单选题 · 每道 10 分</text>
        <text class="quiz-intro">⏱ 不限时间 · 答错即时反馈</text>
        <button class="btn-big" @click="startQuiz">🚀 开始测试</button>
      </view>
      <!-- 进行中 -->
      <view v-else-if="quizState==='playing'" class="quiz-card">
        <view class="quiz-progress">第 {{ quizIdx+1 }} / {{ quizList.length }} 题 · 当前 {{ score }} 分</view>
        <text class="quiz-q">{{ quizList[quizIdx].question }}</text>
        <view class="opt-grid">
          <view v-for="(o,i) in quizList[quizIdx].options" :key="i" class="opt-btn"
                :class="{ right: quizPicked===i && i===quizList[quizIdx].correctAnswer, 
                          wrong: quizPicked===i && i!==quizList[quizIdx].correctAnswer }"
                @click="pickQuiz(i)">
            <text class="opt-text">{{ String.fromCharCode(65+i) }}. {{ o }}</text>
          </view>
        </view>
        <view v-if="quizPicked>=0" class="scene-feedback" :class="{ good: quizPicked===quizList[quizIdx].correctAnswer }">
          <text>{{ quizPicked===quizList[quizIdx].correctAnswer ? '✅ +10 分！' : '❌ 答错了' }}</text>
          <text class="fb-detail">{{ quizList[quizIdx].explanation || '' }}</text>
        </view>
        <button v-if="quizPicked>=0" class="btn-next-small" @click="nextQuiz">{{ quizIdx<quizList.length-1 ? '下一题 →' : '查看成绩' }}</button>
      </view>
      <!-- 结果 -->
      <view v-else-if="quizState==='done'" class="quiz-result">
        <view class="score-circle" :class="{pass:score>=80}">
          <text class="score-num">{{ score }}</text>
          <text class="score-label">分</text>
        </view>
        <text class="result-title">{{ score>=80 ? '🎉 恭喜通关！蛇类专家！' : '💪 继续努力，再来一次！' }}</text>
        <text class="result-sub">答对 {{ quizList.filter((q,i)=>quizAnswers[i]===q.correctAnswer).length }} / {{ quizList.length }} 题</text>
        <button class="btn-big" @click="restartQuiz">🔄 重新测试</button>
      </view>
    </view>

    <!-- 蛇种详情弹层 -->
    <view v-if="detailSnake" class="detail-mask" @click.self="detailSnake=null">
      <view class="detail-card">
        <view class="detail-badge" :class="{danger:detailSnake.isVenomous===1}">
          {{ detailSnake.isVenomous===1 ? '⚠️ 剧毒！' : '✅ 无毒' }}
        </view>
        <text class="detail-name">{{ detailSnake.name }}</text>
        <text class="detail-latin">{{ detailSnake.scientificName }}</text>
        <!-- 大图 SVG -->
        <view class="detail-svg-box">
          <svg viewBox="0 0 300 150" class="detail-svg">
            <path :d="detailSnake.svgPath || defaultPathBig" 
                  fill="none" :stroke="detailSnake.isVenomous===1 ? '#B71C1C' : '#388E3C'" 
                  stroke-width="14" stroke-linecap="round" class="anim-s-svg"/>
          </svg>
        </view>
        <view class="detail-facts">
          <view class="fact"><text class="f-k">头型</text><text class="f-v">{{ detailSnake.headShape }}</text></view>
          <view class="fact"><text class="f-k">花纹</text><text class="f-v">{{ detailSnake.pattern }}</text></view>
          <view class="fact"><text class="f-k">颜色</text><text class="f-v">{{ detailSnake.color }}</text></view>
          <view class="fact" v-if="detailSnake.venomType"><text class="f-k">毒性</text><text class="f-v">{{ detailSnake.venomType }}</text></view>
          <view class="fact" v-if="detailSnake.distribution"><text class="f-k">分布</text><text class="f-v">{{ detailSnake.distribution }}</text></view>
        </view>
        <view v-if="detailSnake.appearanceDesc" class="detail-section">
          <text class="sec-title">📖 外观描述</text>
          <text class="sec-body">{{ detailSnake.appearanceDesc }}</text>
        </view>
        <view v-if="detailSnake.symptoms" class="detail-section">
          <text class="sec-title">🚨 中毒症状</text>
          <text class="sec-body">{{ detailSnake.symptoms }}</text>
        </view>
        <view v-if="detailSnake.firstAidSpecifics" class="detail-section">
          <text class="sec-title">💊 急救要点</text>
          <text class="sec-body">{{ detailSnake.firstAidSpecifics }}</text>
        </view>
        <button class="btn-close" @click="detailSnake=null">关闭</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const API = 'http://127.0.0.1:8080/api'
const steps = ['玩一玩','认一认','练一练','测一测']
const currentStep = ref(0)

// ========== 蛇种数据（先硬编码 12 种作 fallback，后端拉到就替换）==========
const hardcodedSnakes = [
  { id:1, name:'五步蛇', scientificName:'Deinagkistrodon acutus', isVenomous:1, venomType:'血液毒', headShape:'三角形', pattern:'方形斑纹', color:'棕褐+白斑', tailShape:'尖尾', distribution:'南方山区', appearanceDesc:'大型剧毒蛇，头大三角，背部有方形大块斑纹', symptoms:'伤口剧痛、肿胀、出血不止、组织坏死', firstAidSpecifics:'最危险蛇种之一！咬伤后死亡率高，必须立刻送医！', headX:100, headY:30, svgPath:'M10 40 Q30 20 50 35 Q70 50 90 25 Q100 15 100 30' },
  { id:2, name:'眼镜蛇', scientificName:'Naja naja', isVenomous:1, venomType:'神经毒', headShape:'椭圆形(可扩颈)', pattern:'颈部眼镜斑纹', color:'黑褐色', distribution:'南方森林', appearanceDesc:'受惊时颈部扩张如眼镜，可喷毒', symptoms:'眼睑下垂、呼吸困难、心跳麻痹', firstAidSpecifics:'神经毒！抑制呼吸，必须插管抢救！', headX:100, headY:30, svgPath:'M10 40 Q30 15 50 35 Q70 55 90 25 Q95 15 100 30' },
  { id:3, name:'竹叶青', scientificName:'Trimeresurus stejnegeri', isVenomous:1, venomType:'血液毒', headShape:'三角形', pattern:'侧线白点', color:'翠绿色', tailShape:'红色尾尖', distribution:'南方山林', appearanceDesc:'树栖蛇，绿色伪装，尾部红色', symptoms:'伤口灼痛、肿胀、出血', firstAidSpecifics:'毒性中等，但咬伤多在山区难以及时就医', headX:100, headY:30, svgPath:'M10 40 Q30 25 50 38 Q70 50 90 30 Q98 18 100 32' },
  { id:4, name:'银环蛇', scientificName:'Bungarus multicinctus', isVenomous:1, venomType:'神经毒', headShape:'椭圆形', pattern:'黑白相间横纹', color:'黑+白环', distribution:'南方湿地', appearanceDesc:'夜行性，黑白环纹，看似美丽实则极毒', symptoms:'肌肉麻痹、呼吸衰竭、心跳停止', firstAidSpecifics:'中国毒性最强的蛇！无局部症状但会猝死！', headX:100, headY:30, svgPath:'M10 40 Q30 20 50 35 Q70 50 90 25 Q100 12 100 30' },
  { id:5, name:'金环蛇', scientificName:'Bungarus fasciatus', isVenomous:1, venomType:'神经毒', headShape:'椭圆形', pattern:'黄黑相间宽环', color:'黑+黄环', distribution:'南方水田', appearanceDesc:'黄黑宽环交错，与银环蛇同属', symptoms:'与银环蛇类似，神经麻痹', firstAidSpecifics:'剧毒，但相对温顺不主动攻击', headX:100, headY:30, svgPath:'M10 40 Q30 15 50 35 Q70 55 90 20 Q100 8 100 30' },
  { id:6, name:'乌梢蛇', scientificName:'Zaocys dhumnades', isVenomous:0, headShape:'椭圆形', pattern:'背中线黑纹', color:'青褐色', tailShape:'渐细', distribution:'全国平原', appearanceDesc:'大型无毒蛇，行动迅速，背中线有黑纹', firstAidSpecifics:'常见食材/药材，无毒无害', headX:100, headY:30, svgPath:'M10 40 Q30 25 50 38 Q70 50 90 30 Q100 20 100 32' },
  { id:7, name:'黑眉锦蛇', scientificName:'Elaphe taeniura', isVenomous:0, headShape:'椭圆形', pattern:'梯状斑纹', color:'灰褐+黑眉', distribution:'全国农村', appearanceDesc:'大型无毒蛇，眼后有黑纹如眉毛，常进家捕鼠', firstAidSpecifics:'益蛇！帮你家灭鼠，不要杀！', headX:100, headY:30, svgPath:'M10 40 Q30 22 50 36 Q70 48 90 28 Q100 18 100 32' },
  { id:8, name:'菜花蛇', scientificName:'Ptyas mucosus', isVenomous:0, headShape:'椭圆形', pattern:'淡黄色斑纹', color:'黄褐+菜花斑', distribution:'南方农田', appearanceDesc:'大型无毒蛇，俗名菜花蛇', firstAidSpecifics:'被误认为有毒常被杀害，实际无毒', headX:100, headY:30, svgPath:'M10 40 Q30 20 50 35 Q70 50 90 25 Q100 15 100 30' },
  { id:9, name:'王锦蛇', scientificName:'Elaphe carinata', isVenomous:0, headShape:'椭圆形', pattern:'黄色王字纹', color:'黄黑相间', distribution:'北方山林', appearanceDesc:'大型无毒蛇，头上有王字形斑纹，可捕食其他蛇类', firstAidSpecifics:'勇猛好斗，但无毒！蛇类天敌', headX:100, headY:30, svgPath:'M10 40 Q30 25 50 38 Q70 50 90 30 Q100 20 100 32' },
  { id:10, name:'赤链蛇', scientificName:'Lycodon rufozonatum', isVenomous:0, headShape:'椭圆形', pattern:'红黑相间环纹', color:'黑+红环', distribution:'全国水边', appearanceDesc:'体型较小，红黑环纹，常被误认为毒蛇', firstAidSpecifics:'后沟牙微毒，一般无大碍', headX:100, headY:30, svgPath:'M10 40 Q30 25 50 38 Q70 50 90 30 Q100 20 100 32' },
  { id:11, name:'短尾蝮', scientificName:'Gloydius brevicaudus', isVenomous:1, venomType:'血液毒', headShape:'三角形', pattern:'两行圆斑', color:'灰褐+圆斑', tailShape:'短粗', distribution:'全国农田', appearanceDesc:'小型剧毒蛇，常盘在洞口", symptoms:'伤口肿痛、出血、组织坏死', firstAidSpecifics:'中国分布最广的毒蛇，夏季农田高发', headX:100, headY:30, svgPath:'M10 40 Q30 20 50 35 Q70 50 90 28 Q100 18 100 32' },
  { id:12, name:'中华水蛇', scientificName:'Enhydris chinensis', isVenomous:0, headShape:'椭圆形', pattern:'黑色斑点', color:'黄褐+黑斑', distribution:'南方淡水', appearanceDesc:'半水栖蛇，常见于池塘水田', firstAidSpecifics:'微毒无害，主要吃鱼蛙', headX:100, headY:30, svgPath:'M10 40 Q30 25 50 38 Q70 50 90 30 Q100 20 100 32' }
]

const snakes = ref([...hardcodedSnakes])
const filter = ref('all')
const filteredSnakes = computed(()=>{
  if (filter.value==='all') return snakes.value
  if (filter.value==='poison') return snakes.value.filter(s=>s.isVenomous===1)
  return snakes.value.filter(s=>s.isVenomous===0)
})

onMounted(async ()=>{
  try {
    const res = await new Promise((resolve,reject)=>{
      uni.request({ url:API+'/app/snake/page?current=1&size=20', timeout:3000,
        success: r=>resolve(r.data), fail: reject })
    })
    if (res.code===200 && res.data?.records?.length) {
      const list = res.data.records
      // 补充缺失字段
      list.forEach(s=>{ if(!s.svgPath){ s.svgPath = hardcodedSnakes[0].svgPath; s.headX=100; s.headY=30 } })
      snakes.value = list
      console.log('✅ 蛇种从后端拉了 '+list.length+' 条')
    }
  } catch(e) {
    console.log('⚠️ 用硬编码 12 种 fallback')
  }
  // 初始化训练和测试
  initTrain()
  initQuiz()
})

const gotoStep = (i)=>currentStep.value = i
const defaultPath = 'M5 35 Q20 15 40 28 Q60 42 80 20 Q95 8 100 28'
const defaultPathBig = 'M10 100 Q60 40 110 80 Q160 120 210 70 Q260 20 290 80'

// ========== 玩一玩 ==========
const playOptions = ref([
  { icon:'🏃‍♂️', text:'转身慢慢后退', ok:true, fb:'正确！蛇不主动追人，慢慢退保持安全距离' },
  { icon:'👋', text:'弯腰捡石头扔蛇', ok:false, fb:'危险！激怒蛇会攻击！' },
  { icon:'📱', text:'站在原地打电话', ok:false, fb:'动也不动反而可能被蛇凑近！' },
  { icon:'🚶', text:'大跨步快速跑过', ok:false, fb:'跑太快会惊动蛇，而且可能踩到！' }
])
const picked = ref(-1)
const pickPlay = (i)=>picked.value = i

// ========== 练一练 ==========
const trainPool = ref([])
const trainIdx = ref(0)
const trainQ = computed(()=>trainPool.value[trainIdx.value] || {})
const trainOpts = ref([])
const trainPicked = ref(-1)
function initTrain(){
  // 从蛇种池抽 8 道特征题
  const pool = []
  snakes.value.forEach(s=>{
    pool.push({ headShape:s.headShape, pattern:s.pattern, color:s.color, name:s.name, appearanceDesc:s.appearanceDesc, symptoms:s.symptoms, distribution:s.distribution })
  })
  // 打乱取 8 题
  trainPool.value = pool.sort(()=>Math.random()-0.5).slice(0, Math.min(8, pool.length))
  regenerateOpts()
}
function regenerateOpts(){
  if (!trainQ.value.name) return
  const correct = trainQ.value.name
  const wrongs = snakes.value.filter(s=>s.name!==correct).sort(()=>Math.random()-0.5).slice(0,3).map(s=>s.name)
  trainOpts.value = [...wrongs, correct].sort(()=>Math.random()-0.5)
}
function pickTrain(i){ trainPicked.value = i }
function nextTrain(){
  trainIdx.value++
  trainPicked.value = -1
  regenerateOpts()
}

// ========== 测一测 ==========
const quizList = ref([])
const quizIdx = ref(0)
const score = ref(0)
const quizState = ref('idle')
const quizPicked = ref(-1)
const quizAnswers = ref([])
function initQuiz(){
  // 10 道题库（固定+随机混合）
  const bank = [
    { question:'蛇咬伤后第一时间该做什么？', options:['立刻用嘴吸伤口','冷静离开现场并呼救','敷草药止血','高举伤肢跑回家'], correctAnswer:1, explanation:'用嘴吸会让毒液进入救护者体内！先冷静呼救！' },
    { question:'毒蛇头型一般是？', options:['圆形','椭圆形','三角形/菱形','方形'], correctAnswer:2, explanation:'三角形头是毒蛇典型特征（但也有例外）' },
    { question:'被蛇咬伤后伤肢应该？', options:['抬高过心脏','与心脏同平放低','用力绑紧','冰敷消肿'], correctAnswer:1, explanation:'平放制动！抬高加速毒扩散，冰敷伤组织！' },
    { question:'五步蛇属于哪种毒？', options:['神经毒','血液毒','混合毒','细胞毒'], correctAnswer:1, explanation:'五步蛇是血液毒，伤口出血不止、组织坏死' },
    { question:'遇到蛇最安全的做法是？', options:['抓起来泡酒','扔石头打它','慢慢后退保持距离','拍照后快速跑开'], correctAnswer:2, explanation:'蛇不主动追人，慢慢退是最稳妥的' },
    { question:'银环蛇的花纹是？', options:['黑白环纹','黄黑环纹','三角斑纹','方形斑块'], correctAnswer:0, explanation:'银环蛇黑白环，金环蛇黄黑环' },
    { question:'蛇咬伤后以下哪项正确？', options:['喝酒消毒','大量喝水排尿','吃止痛药','冰敷伤口'], correctAnswer:1, explanation:'喝水促进代谢排毒，酒精/止痛药/冰敷全错' },
    { question:'眼镜蛇属于？', options:['血液毒','神经毒','混合毒','无毒'], correctAnswer:1, explanation:'眼镜蛇是神经毒，会导致呼吸麻痹' },
    { question:'竹叶青的典型特征是？', options:['绿色+红尾尖','黑+白环','黄+黑环','三角头+方斑'], correctAnswer:0, explanation:'翠绿色树栖蛇，尾部红色是标志' },
    { question:'发现家人被蛇咬伤后你应该？', options:['先拍照发朋友圈','立刻打120说清楚情况','自己开车送去','找草药敷上'], correctAnswer:1, explanation:'立刻打120！说清：蛇形、地点、症状！让医院准备血清！' }
  ]
  quizList.value = bank.sort(()=>Math.random()-0.5)
}
function startQuiz(){
  quizIdx.value=0; score.value=0; quizState.value='playing'
  quizPicked.value=-1; quizAnswers.value=[]
}
function pickQuiz(i){
  quizPicked.value = i
  quizAnswers.value[quizIdx.value] = i
  if (i===quizList.value[quizIdx.value].correctAnswer) score.value += 10
}
function nextQuiz(){
  if (quizIdx.value < quizList.value.length-1) {
    quizIdx.value++
    quizPicked.value = -1
  } else {
    quizState.value = 'done'
  }
}
function restartQuiz(){
  initQuiz(); startQuiz()
}

// 详情弹层
const detailSnake = ref(null)
function openDetail(s){ detailSnake.value = s }
</script>

<style>
.page { min-height: 100vh; background: #FFF8E1; padding-bottom: 40rpx; }
.top-bar { padding: 30rpx; text-align: center; background: linear-gradient(135deg,#2E7D32,#66BB6A); }
.top-title { font-size: 48rpx; font-weight: bold; color: white; display:block; }
.top-sub { font-size: 24rpx; color: #E8F5E9; margin-top: 6rpx; }

/* 4 步进度 */
.step-bar { display:flex; padding: 20rpx; background: white; margin: 20rpx; border-radius: 16rpx; box-shadow: 0 4rpx 20rpx rgba(0,0,0,.08); }
.step-item { flex:1; text-align:center; }
.step-num { width:48rpx; height:48rpx; border-radius:50%; background:#E0E0E0; color:#757575; font-size:24rpx; line-height:48rpx; margin:0 auto 6rpx; font-weight:bold; }
.step-item.active .step-num { background:#2E7D32; color:white; animation: pulse 1.2s infinite; }
.step-item.done .step-num { background:#66BB6A; color:white; }
.step-label { font-size:22rpx; color:#666; }
.step-item.active .step-label { color:#2E7D32; font-weight:bold; }

@keyframes pulse { 0%,100%{transform:scale(1)} 50%{transform:scale(1.1)} }

.step-panel { padding: 20rpx; }
.panel-header { text-align:center; margin-bottom:20rpx; }
.panel-emoji { font-size:48rpx; display:block; }
.panel-title { font-size:36rpx; font-weight:bold; color:#1B5E20; display:block; margin-top:8rpx; }
.panel-desc { font-size:24rpx; color:#757575; margin-top:6rpx; }

/* 场景卡 */
.scene-card { background:white; border-radius:20rpx; padding:24rpx; box-shadow:0 6rpx 30rpx rgba(0,0,0,.08); }
.scene-svg-wrap { background:#E8F5E9; border-radius:16rpx; overflow:hidden; }
.scene-svg { width:100%; height:auto; }
.scene-q { font-size:30rpx; font-weight:bold; color:#333; display:block; margin:20rpx 0; text-align:center; }

/* 选项 */
.opt-grid { display:flex; flex-wrap:wrap; gap:16rpx; }
.opt-btn { flex:1 1 calc(50% - 16rpx); min-width:280rpx; background:#F5F5F5; border-radius:16rpx; padding:24rpx; text-align:center; border:4rpx solid transparent; transition:all .2s; }
.opt-btn:active { transform:scale(.96); }
.opt-btn.right { background:#E8F5E9; border-color:#4CAF50; }
.opt-btn.wrong { background:#FFEBEE; border-color:#EF5350; }
.opt-icon { font-size:48rpx; display:block; }
.opt-emoji { font-size:36rpx; }
.opt-text { font-size:26rpx; color:#333; margin-top:8rpx; display:block; }
.opt-btn.big { padding:30rpx; }

.scene-feedback { padding:20rpx; border-radius:12rpx; margin-top:16rpx; font-size:26rpx; text-align:center; }
.scene-feedback.good { background:#E8F5E9; color:#2E7D32; }
.scene-feedback.bad { background:#FFEBEE; color:#C62828; }
.fb-detail { display:block; font-size:22rpx; margin-top:8rpx; }

/* 按钮 */
.btn-next, .btn-big { background:linear-gradient(135deg,#2E7D32,#66BB6A); color:white; border:none; border-radius:50rpx; padding:24rpx 48rpx; font-size:28rpx; font-weight:bold; margin:20rpx auto; display:block; }
.btn-next:disabled { background:#BDBDBD; }
.btn-next-small { background:#1976D2; color:white; border:none; border-radius:30rpx; padding:16rpx 32rpx; font-size:24rpx; margin:16rpx auto; display:block; }

/* 筛选 */
.filter-row { display:flex; gap:16rpx; margin-bottom:20rpx; }
.chip { padding:12rpx 24rpx; border-radius:30rpx; background:white; font-size:24rpx; color:#666; }
.chip.on { background:#2E7D32; color:white; }
.chip.danger.on { background:#C62828; }
.chip.safe.on { background:#4CAF50; }

/* 蛇网格 */
.snake-grid { display:flex; flex-wrap:wrap; gap:16rpx; }
.snake-card { width:calc(50% - 8rpx); background:white; border-radius:16rpx; padding:16rpx; box-shadow:0 4rpx 16rpx rgba(0,0,0,.06); border:4rpx solid transparent; transition:all .2s; }
.snake-card.danger { border-color:#EF9A9A; background:#FFFAFA; }
.snake-card:active { transform:scale(.97); }
.snake-svg-box { position:relative; background:#F5F5F5; border-radius:12rpx; padding:10rpx; }
.snake-svg-mini { width:100%; height:80rpx; }
.venom-badge { position:absolute; top:8rpx; right:8rpx; background:#D32F2F; color:white; font-size:18rpx; padding:4rpx 10rpx; border-radius:12rpx; }
.safe-badge { position:absolute; top:8rpx; right:8rpx; background:#4CAF50; color:white; font-size:18rpx; padding:4rpx 10rpx; border-radius:12rpx; }
.snake-name { font-size:28rpx; font-weight:bold; color:#333; display:block; margin-top:10rpx; }
.snake-latin { font-size:18rpx; color:#999; font-style:italic; display:block; }
.snake-feature { font-size:22rpx; color:#757575; display:block; margin-top:6rpx; }

/* 练习/测试 */
.quiz-card { background:white; border-radius:20rpx; padding:24rpx; box-shadow:0 6rpx 30rpx rgba(0,0,0,.08); }
.quiz-progress { text-align:center; font-size:26rpx; color:#2E7D32; font-weight:bold; margin-bottom:16rpx; }
.quiz-features { display:flex; flex-wrap:wrap; gap:10rpx; margin-bottom:16rpx; }
.feat-tag { background:#E3F2FD; color:#1565C0; padding:8rpx 16rpx; border-radius:20rpx; font-size:22rpx; }
.quiz-q { font-size:30rpx; font-weight:bold; color:#333; display:block; margin:16rpx 0; text-align:center; }
.quiz-start { text-align:center; padding:40rpx; }
.quiz-intro { font-size:28rpx; color:#555; display:block; margin:10rpx 0; }

/* 结果 */
.quiz-result { text-align:center; padding:40rpx; }
.score-circle { width:200rpx; height:200rpx; border-radius:50%; background:#FFEBEE; margin:0 auto 20rpx; display:flex; align-items:center; justify-content:center; flex-direction:column; border:8rpx solid #EF5350; }
.score-circle.pass { background:#E8F5E9; border-color:#4CAF50; }
.score-num { font-size:80rpx; font-weight:bold; color:#C62828; }
.score-circle.pass .score-num { color:#2E7D32; }
.score-label { font-size:24rpx; color:#757575; }
.result-title { font-size:32rpx; font-weight:bold; display:block; margin:16rpx 0; color:#333; }
.result-sub { font-size:26rpx; color:#666; display:block; }

/* 详情弹层 */
.detail-mask { position:fixed; top:0; left:0; right:0; bottom:0; background:rgba(0,0,0,.5); z-index:999; display:flex; align-items:center; justify-content:center; padding:40rpx; }
.detail-card { background:white; border-radius:24rpx; padding:32rpx; width:100%; max-height:90vh; overflow-y:auto; position:relative; }
.detail-badge { position:absolute; top:20rpx; right:20rpx; background:#4CAF50; color:white; padding:10rpx 24rpx; border-radius:30rpx; font-size:24rpx; }
.detail-badge.danger { background:#D32F2F; }
.detail-name { font-size:40rpx; font-weight:bold; color:#333; display:block; padding-right:150rpx; }
.detail-latin { font-size:22rpx; color:#999; font-style:italic; display:block; margin-top:6rpx; }
.detail-svg-box { background:#F5F5F5; border-radius:16rpx; padding:20rpx; margin:20rpx 0; }
.detail-svg { width:100%; height:200rpx; }
.anim-s-svg { animation: snakeWiggle 2s ease-in-out infinite; transform-origin:center; transform-box:fill-box; }
@keyframes snakeWiggle { 0%,100%{transform:translateY(0)} 50%{transform:translateY(-4px)} }
.detail-facts { display:flex; flex-wrap:wrap; gap:10rpx; margin-bottom:16rpx; }
.fact { background:#F5F5F5; padding:10rpx 16rpx; border-radius:12rpx; font-size:22rpx; }
.f-k { color:#999; margin-right:6rpx; }
.f-v { color:#333; font-weight:bold; }
.detail-section { margin-top:16rpx; }
.sec-title { font-size:26rpx; font-weight:bold; color:#1B5E20; display:block; margin-bottom:6rpx; }
.sec-body { font-size:26rpx; color:#555; line-height:1.6; }
.btn-close { background:#757575; color:white; border:none; border-radius:40rpx; padding:18rpx 48rpx; font-size:26rpx; margin:20rpx auto 0; display:block; }

/* SVG 场景动画 */
.anim-person-stand { animation:personBreathe 2.5s ease-in-out infinite; transform-origin:center; transform-box:fill-box; }
.anim-s { animation:snakeSway 1.8s ease-in-out infinite; transform-origin:center; transform-box:fill-box; }
.anim-warn { animation: warnPulse 1.2s ease-in-out infinite; transform-origin:center; transform-box:fill-box; }
@keyframes personBreathe { 0%,100%{transform:scaleY(1)} 50%{transform:scaleY(1.02)} }
@keyframes snakeSway { 0%,100%{transform:rotate(-2deg)} 50%{transform:rotate(2deg)} }
@keyframes warnPulse { 0%,100%{transform:scale(1);opacity:1} 50%{transform:scale(1.1);opacity:0.8} }
</style>