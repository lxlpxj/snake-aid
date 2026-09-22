<template>
	<view class="steps">
		<!-- 顶部免责声明条（FR8，固定显示，不可关闭） -->
		<view class="disclaimer-bar">
			⚠ 自救不等于治疗，必须尽快就医！本应用指引仅供参考，不能替代专业医疗。
		</view>

		<!-- 毒型选择（FR3 分型自救差异） -->
		<view class="type-picker">
			<text class="picker-label">咬伤类型：</text>
			<picker mode="selector" :range="venomTypes" :value="typeIndex" @change="onTypeChange">
				<view class="picker-display">
					{{ currentType.label }} ▾
				</view>
			</picker>
		</view>

		<!-- 毒型差异提示 -->
		<view class="type-hint" v-if="currentType.key !== 'general'">
			<text class="hint-title">💡 针对{{ currentType.label }}的特殊注意</text>
			<text class="hint-content" v-for="(h, i) in currentType.hints" :key="i">• {{ h }}</text>
		</view>

		<!-- 6 步指引 -->
		<view class="step-list">
			<view v-for="(step, idx) in displaySteps" :key="idx" class="step-card">
				<view class="step-num">{{ idx + 1 }}</view>
				<view class="step-body">
					<view class="step-title">{{ step.title }}</view>
					<view class="step-content">{{ step.content }}</view>
					<view v-if="step.warning" class="step-warning">
						⚠ {{ step.warning }}
					</view>
				</view>
			</view>
		</view>

		<!-- FR7 禁止动作警示（醒目块） -->
		<view class="forbidden-card">
			<view class="forbidden-title">⛔ 绝对禁止</view>
			<view class="forbidden-grid">
				<view class="forbidden-item">❌ 切开伤口</view>
				<view class="forbidden-item">❌ 用嘴吸毒</view>
				<view class="forbidden-item">❌ 冰敷伤口</view>
				<view class="forbidden-item">❌ 奔跑/运动</view>
				<view class="forbidden-item">❌ 绑太紧</view>
				<view class="forbidden-item">❌ 喝酒</view>
			</view>
		</view>

		<!-- 蛇种识别入口 -->
		<view class="snake-entry" @click="goSnake">
			<text class="entry-question">不确定是什么蛇？</text>
			<text class="entry-btn">去图库识别 →</text>
		</view>

		<!-- 底部固定：120 + 医院 -->
		<view class="bottom-bar">
			<view class="btn-call" @click="call120">
				🚑 一键 120
			</view>
			<view class="btn-hospital" @click="goHospital">
				🏥 就近找医院
			</view>
		</view>
	</view>
</template>

<script>
// FR3：不同毒型的差异化步骤（只差异化后 4 步，前 2 步通用）
const STEP_TEMPLATES = {
	general: [
		{ title: '脱离现场', content: '立即远离蛇所在区域，避免再次被咬。保持冷静，不要奔跑。', warning: '不要奔跑！' },
		{ title: '保持冷静', content: '坐下或平躺，放松心情。情绪激动会加速血液循环，加重中毒。', warning: '不要剧烈活动！' },
		{ title: '制动伤肢', content: '用夹板或硬板固定咬伤部位，减少活动。咬伤部位保持低于心脏水平。', warning: '绑扎不要过紧！' },
		{ title: '松解束缚', content: '立即解开咬伤部位附近的衣物、首饰，避免肿胀后压迫。' },
		{ title: '清洗包扎', content: '用清水或肥皂水冲洗伤口，不要切开，不要吸吮。用干净纱布松松包扎。', warning: '不要用嘴吸毒！' },
		{ title: '尽快就医', content: '拨打 120 送往有血清的医院。尽量记住蛇的特征告诉医生。', warning: '冰敷不可取！' }
	],
	neurotoxic: [  // 神经毒（如金环蛇、银环蛇）
		{ title: '脱离现场', content: '立即远离蛇所在区域，避免再次被咬。', warning: '不要奔跑！' },
		{ title: '保持冷静', content: '坐下或平躺，放松心情，尽量减少说话和吞咽。', warning: '不要剧烈活动！' },
		{ title: '制动伤肢', content: '固定咬伤部位，保持低于心脏水平。制动是关键！', warning: '绑扎不要过紧！' },
		{ title: '松解束缚', content: '解开伤肢附近的衣物首饰。注意：神经毒会逐渐影响呼吸肌。' },
		{ title: '清洗包扎', content: '清水冲洗伤口，不要切开或吸吮。保持呼吸道通畅！', warning: '呼吸微弱时立即呼救！' },
		{ title: '尽快就医', content: '立即拨打 120！神经毒 1-2 小时可致呼吸衰竭，需尽快使用抗神经毒血清。', warning: '时间就是生命！' }
	],
	hemotoxic: [  // 血液毒（如蝰蛇、五步蛇）
		{ title: '脱离现场', content: '立即远离蛇所在区域，避免再次被咬。', warning: '不要奔跑！' },
		{ title: '保持冷静', content: '坐下或平躺。血液毒会导致伤口出血不止和组织坏死。', warning: '不要剧烈活动！' },
		{ title: '制动伤肢', content: '固定咬伤部位，保持低于心脏水平。减少血液流动。', warning: '绑扎不要过紧！' },
		{ title: '松解束缚', content: '解开伤肢附近衣物首饰。警惕伤口肿胀和出血。' },
		{ title: '清洗包扎', content: '清水冲洗伤口后用干净纱布轻压止血。不要切开伤口！', warning: '不要切开！不要吸毒！' },
		{ title: '尽快就医', content: '立即前往有血清的医院！血液毒可致 DIC（弥漫性血管内凝血），需尽快止血和使用血清。', warning: '肿胀严重需切开减压，但必须由医生做！' }
	],
	cytotoxic: [  // 细胞毒（如眼镜蛇科部分）
		{ title: '脱离现场', content: '立即远离蛇所在区域。', warning: '不要奔跑！' },
		{ title: '保持冷静', content: '坐下或平躺。细胞毒会造成局部组织坏死。', warning: '不要剧烈活动！' },
		{ title: '制动伤肢', content: '固定咬伤部位，保持低于心脏水平。', warning: '绑扎不要过紧！' },
		{ title: '松解束缚', content: '解开伤肢附近衣物首饰，避免肿胀压迫。' },
		{ title: '清洗包扎', content: '清水冲洗伤口，松松包扎。保持伤口清洁避免感染。' },
		{ title: '尽快就医', content: '尽快前往医院！细胞毒会造成皮肤坏死和肌肉损伤，需清创和使用血清。', warning: '不要自行涂抹任何药膏！' }
	],
	mixed: [  // 混合毒（如蝮蛇）
		{ title: '脱离现场', content: '立即远离蛇所在区域。混合毒同时作用于神经和血液。', warning: '不要奔跑！' },
		{ title: '保持冷静', content: '坐下或平躺，尽量减少活动。', warning: '不要剧烈活动！' },
		{ title: '制动伤肢', content: '固定咬伤部位，保持低于心脏水平。', warning: '绑扎不要过紧！' },
		{ title: '松解束缚', content: '解开伤肢附近衣物首饰。同时警惕呼吸和出血两个方向。' },
		{ title: '清洗包扎', content: '清水冲洗伤口，轻压止血。不要切开或吸吮。', warning: '不要用嘴吸毒！' },
		{ title: '尽快就医', content: '立即前往有血清的医院！混合毒病情复杂，需多种血清联合使用。', warning: '时间最关键！' }
	]
}

export default {
	data() {
		return {
			typeIndex: 0,
			venomTypes: ['通用（不确定）', '神经毒', '血液毒', '细胞毒', '混合毒'],
			venomTypeKeys: ['general', 'neurotoxic', 'hemotoxic', 'cytotoxic', 'mixed'],
			typeMeta: {
				general:    { label: '通用指引', hints: ['不确定毒型时使用通用步骤', '立即拨打 120 最关键'] },
				neurotoxic: { label: '神经毒', hints: ['代表蛇种：金环蛇、银环蛇、海蛇', '风险：呼吸肌麻痹 → 呼吸衰竭', '关键：保持呼吸道通畅，尽快用血清'] },
				hemotoxic:  { label: '血液毒', hints: ['代表蛇种：蝰蛇、五步蛇、竹叶青', '风险：DIC → 出血不止 / 脏器衰竭', '关键：伤口轻压止血，尽快用血清'] },
				cytotoxic:  { label: '细胞毒', hints: ['代表蛇种：眼镜蛇、舟山眼镜蛇', '风险：局部组织坏死 → 溃疡', '关键：保持伤口清洁，需医生清创'] },
				mixed:      { label: '混合毒', hints: ['代表蛇种：蝮蛇、眼镜王蛇', '风险：同时有神经毒 + 血液毒症状', '关键：病情最复杂，立即就医'] }
			}
		}
	},
	computed: {
		currentKey() { return this.venomTypeKeys[this.typeIndex] },
		currentType() { return this.typeMeta[this.currentKey] },
		displaySteps() { return STEP_TEMPLATES[this.currentKey] }
	},
	onLoad() {
		// 从蛇种详情页传过来的毒型
		const pages = getCurrentPages()
		const prev = pages[pages.length - 2]
		if (prev && prev.$page && prev.$page.options && prev.$page.options.venomType) {
			const vt = prev.$page.options.venomType
			const map = { '神经毒': 1, '血液毒': 2, '细胞毒': 3, '混合毒': 4 }
			if (map[vt] !== undefined) this.typeIndex = map[vt]
		}
	},
	methods: {
		onTypeChange(e) { this.typeIndex = Number(e.detail.value) },
		goSnake() { uni.switchTab({ url: '/pages/snake/list' }) },
		call120() { uni.makePhoneCall({ phoneNumber: '120' }) },
		goHospital() { uni.switchTab({ url: '/pages/hospital/list' }) }
	}
}
</script>

<style>
.steps { padding: 24rpx 24rpx 220rpx; }

.disclaimer-bar {
	background: #FFF3CD; color: #856404;
	padding: 16rpx 24rpx; font-size: 26rpx;
	text-align: center; line-height: 1.5;
	margin-bottom: 16rpx; border-radius: 8rpx; font-weight: 500;
}

/* 毒型选择器 */
.type-picker {
	display: flex; align-items: center;
	background: #E3F2FD; padding: 16rpx 20rpx;
	border-radius: 12rpx; margin-bottom: 12rpx;
}
.picker-label { font-size: 26rpx; color: #1565C0; }
.picker-display {
	background: #FFF; padding: 10rpx 20rpx; border-radius: 30rpx;
	font-size: 26rpx; color: #1565C0; border: 1rpx solid #1565C0;
}

.type-hint {
	background: #FFF8E1; border-left: 6rpx solid #FF9800;
	padding: 16rpx 20rpx; border-radius: 0 8rpx 8rpx 0;
	margin-bottom: 16rpx;
}
.hint-title { display: block; font-size: 26rpx; font-weight: bold; color: #E65100; margin-bottom: 8rpx; }
.hint-content { display: block; font-size: 24rpx; color: #5D4037; line-height: 1.6; }

/* 步骤卡片 */
.step-list { }
.step-card {
	display: flex; gap: 20rpx; background: #FFF;
	border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx;
	box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}
.step-num {
	width: 56rpx; height: 56rpx; border-radius: 50%;
	background: #C62828; color: #FFF; font-size: 30rpx; font-weight: bold;
	display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.step-body { flex: 1; }
.step-title { font-size: 30rpx; font-weight: bold; color: #222; }
.step-content { font-size: 26rpx; color: #555; margin-top: 6rpx; line-height: 1.6; }
.step-warning {
	margin-top: 10rpx; background: #FFEBEE; color: #C62828;
	font-size: 24rpx; padding: 8rpx 16rpx; border-radius: 6rpx;
}

/* FR7 禁止动作 */
.forbidden-card {
	background: #FFEBEE; border-radius: 16rpx; padding: 20rpx;
	margin-top: 24rpx; border: 1rpx solid #EF9A9A;
}
.forbidden-title { font-size: 28rpx; font-weight: bold; color: #C62828; margin-bottom: 16rpx; }
.forbidden-grid { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 12rpx; }
.forbidden-item {
	text-align: center; padding: 10rpx 6rpx; background: #FFF;
	border-radius: 8rpx; font-size: 24rpx; color: #C62828;
}

.snake-entry {
	display: flex; justify-content: space-between; align-items: center;
	background: #E8F5E9; border-radius: 12rpx; padding: 20rpx 24rpx;
	margin-top: 24rpx;
}
.entry-question { font-size: 26rpx; color: #2E7D32; }
.entry-btn { font-size: 26rpx; color: #1565C0; font-weight: bold; }

/* 底部固定按钮 */
.bottom-bar {
	position: fixed; bottom: 0; left: 0; right: 0;
	background: #FFF; padding: 20rpx 24rpx; padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
	display: flex; gap: 20rpx; box-shadow: 0 -2rpx 12rpx rgba(0,0,0,0.1);
	z-index: 100;
}
.btn-call {
	flex: 1; background: #C62828; color: #FFF; text-align: center;
	padding: 20rpx; border-radius: 40rpx; font-size: 30rpx; font-weight: bold;
}
.btn-hospital {
	flex: 1; background: #1565C0; color: #FFF; text-align: center;
	padding: 20rpx; border-radius: 40rpx; font-size: 30rpx; font-weight: bold;
}
</style>
