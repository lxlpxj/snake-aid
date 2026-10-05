package cn.snake.snakeaidbackend.service;

import cn.snake.snakeaidbackend.entity.AiChatHistory;
import cn.snake.snakeaidbackend.mapper.AiChatHistoryMapper;
import cn.snake.snakeaidbackend.mapper.SnakeSpeciesMapper;
import cn.snake.snakeaidbackend.entity.SnakeSpecies;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI 蛇伤急救助手 - 本地知识库 v2（组合意图 + 多轮上下文 + 动态回答）
 * 零 API Key，纯本地匹配
 */
@Slf4j
@Service
public class AiAssistantService {

    @Resource
    private AiChatHistoryMapper historyMapper;
    @Resource
    private SnakeSpeciesMapper snakeMapper;

    // ====== 知识库 ======
    private static final Map<String, String[]> KNOWLEDGE = new LinkedHashMap<>();
    private static final Map<String, String> INTENT_MAP = new LinkedHashMap<>();

    static {
        KNOWLEDGE.put("snake_identify", new String[]{
                "银环蛇", "金环蛇", "眼镜蛇", "五步蛇", "竹叶青", "蝮蛇",
                "尖吻蝮", "王锦蛇", "乌梢蛇", "海蛇", "圆斑蝰", "眼镜王蛇",
                "莽山烙铁头", "短尾蝮", "原矛头蝮", "舟山眼镜蛇", "中华眼镜蛇"
        });
        KNOWLEDGE.put("first_aid", new String[]{
                "急救", "自救", "被咬", "咬伤", "怎么办", "处理", "伤口",
                "止血", "包扎", "制动", "固定", "急救法", "救我", "中毒了"
        });
        KNOWLEDGE.put("symptom", new String[]{
                "症状", "表现", "红肿", "疼痛", "出血", "发黑", "组织坏死",
                "呼吸", "麻痹", "神经毒", "血循毒", "混合毒", "会死吗", "严重吗"
        });
        KNOWLEDGE.put("hospital", new String[]{
                "医院", "去哪", "附近", "抗蛇毒血清", "血清", "治疗",
                "哪个医院", "120", "救护车", "急诊"
        });
        KNOWLEDGE.put("prevention", new String[]{
                "预防", "避免", "野外", "注意", "穿戴", "防护", "夜间",
                "打草惊蛇", "怎么防", "防止被咬", "避免被蛇咬"
        });
        KNOWLEDGE.put("venom_type", new String[]{
                "毒型", "毒性", "神经毒", "血液毒", "血循毒", "混合毒",
                "什么毒", "哪种毒", "毒型是什么"
        });
        KNOWLEDGE.put("snake_bite_symptom", new String[]{
                "被咬了什么症状", "咬了会怎样", "咬伤的症状", "被蛇咬了有什么症状"
        });

        INTENT_MAP.put("snake_identify", "蛇种识别");
        INTENT_MAP.put("first_aid", "急救步骤");
        INTENT_MAP.put("symptom", "症状判断");
        INTENT_MAP.put("hospital", "就医咨询");
        INTENT_MAP.put("prevention", "预防建议");
        INTENT_MAP.put("venom_type", "毒型知识");
        INTENT_MAP.put("snake_bite_symptom", "蛇咬伤症状");
    }

    /**
     * 对话主入口 - 带上下文
     */
    public Map<String, Object> chat(Long userId, String userMessage) {
        // 1. 取用户最近对话上下文（最后 5 条用户消息）
        List<String> recentMsgs = getRecentUserMessages(userId, 5);

        // 2. 识别意图（支持组合）
        String intent = detectIntent(userMessage, recentMsgs);

        // 3. 提取蛇种（如果有）
        String snakeName = extractSnakeName(userMessage);
        if (snakeName == null && !recentMsgs.isEmpty()) {
            snakeName = extractSnakeName(String.join(" ", recentMsgs));
        }

        // 4. 生成回答（上下文感知）
        String reply = generateReply(intent, userMessage, snakeName, recentMsgs);

        // 5. 存历史
        if (userId != null) {
            saveHistory(userId, "user", userMessage, intent);
            saveHistory(userId, "assistant", reply, intent);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("reply", reply);
        result.put("intent", intent);
        result.put("intentLabel", INTENT_MAP.getOrDefault(intent, "通用咨询"));
        result.put("snakeName", snakeName);
        return result;
    }

    /** 取用户历史用户消息（不含 AI 回复） */
    private List<String> getRecentUserMessages(Long userId, int limit) {
        if (userId == null) return List.of();
        List<AiChatHistory> all = historyMapper.selectList(
                new LambdaQueryWrapper<AiChatHistory>()
                        .eq(AiChatHistory::getUserId, userId)
                        .eq(AiChatHistory::getRole, "user")
                        .orderByDesc(AiChatHistory::getCreateTime)
                        .last("LIMIT " + limit)
        );
        return all.stream().map(AiChatHistory::getContent).collect(Collectors.toList());
    }

    /** 提取蛇名 */
    private String extractSnakeName(String msg) {
        if (msg == null) return null;
        List<SnakeSpecies> all = snakeMapper.selectList(null);
        for (SnakeSpecies s : all) {
            if (msg.contains(s.getName())) return s.getName();
        }
        // 知识库硬编码蛇名（防止 DB 里没有）
        for (String name : KNOWLEDGE.get("snake_identify")) {
            if (msg.contains(name)) return name;
        }
        return null;
    }

    /** 查蛇种完整信息 */
    private SnakeSpecies findSnake(String name) {
        if (name == null) return null;
        return snakeMapper.selectList(null).stream()
                .filter(s -> name.equals(s.getName()))
                .findFirst().orElse(null);
    }

    /**
     * 组合意图识别
     * 例如："被五步蛇咬了怎么办" → snake_identify + first_aid → combo: snake_aid
     */
    public String detectIntent(String msg, List<String> recentMsgs) {
        if (msg == null || msg.isBlank()) return "general";

        String lower = msg.toLowerCase();

        // ====== 1. 上下文追问检测 ======
        // "它"、"这个"、"然后呢"、"还有呢"、"继续说"
        boolean isFollowUp = msg.matches(".*[它这个那].*|.*然后.*|.*还有.*|.*继续.*|.*接着.*|.*说说.*|.*讲讲.*");
        boolean isShortMsg = msg.length() <= 8;
        if ((isFollowUp || isShortMsg) && recentMsgs != null && !recentMsgs.isEmpty()) {
            // 从上一个用户消息推断
            String last = recentMsgs.get(0);
            String lastIntent = detectIntentSimple(last);
            // 追问症状
            if (msg.contains("症状") || msg.contains("表现") || msg.contains("会怎样") || msg.contains("有什么")) {
                return "snake_bite_symptom";
            }
            // 追问急救
            if (msg.contains("怎么办") || msg.contains("处理") || msg.contains("急救") || msg.contains("怎么救")) {
                return "first_aid";
            }
            // 追问医院
            if (msg.contains("医院") || msg.contains("血清")) {
                return "hospital";
            }
            // 追问毒型
            if (msg.contains("毒") || msg.contains("毒性")) {
                return "venom_type";
            }
            // 继承上一个意图
            if (!"general".equals(lastIntent)) return lastIntent;
        }

        // ====== 2. 组合意图检测 ======
        String snakeName = extractSnakeName(msg);
        boolean hasFirstAid = msg.matches(".*被咬.*怎么办.*|.*咬伤.*处理.*|.*被.*蛇.*咬.*");
        boolean hasSnake = snakeName != null;
        boolean hasSymptom = msg.contains("症状") || msg.contains("表现") || msg.contains("严重") || msg.contains("会死");

        // 组合 1：蛇种 + 急救 → 针对该蛇种的急救
        if (hasSnake && (hasFirstAid || msg.contains("怎么办") || msg.contains("处理"))) {
            return "combo_snake_aid";
        }
        // 组合 2：蛇种 + 症状 → 针对该蛇种的症状
        if (hasSnake && hasSymptom) {
            return "combo_snake_symptom";
        }
        // 组合 3：蛇种 + 毒型
        if (hasSnake && (msg.contains("毒") || msg.contains("毒性") || msg.contains("毒型"))) {
            return "combo_snake_venom";
        }
        // 组合 4：蛇种 + 医院（哪种血清）
        if (hasSnake && (msg.contains("血清") || msg.contains("医院"))) {
            return "combo_snake_hospital";
        }

        // ====== 3. 简单关键词匹配 ======
        return detectIntentSimple(msg);
    }

    private String detectIntentSimple(String msg) {
        // 蛇种优先
        if (extractSnakeName(msg) != null) return "snake_identify";

        String best = "general";
        int bestScore = 0;
        for (Map.Entry<String, String[]> entry : KNOWLEDGE.entrySet()) {
            if ("snake_identify".equals(entry.getKey())) continue;
            int score = 0;
            for (String kw : entry.getValue()) {
                if (msg.contains(kw)) score += 2;
            }
            if (score > bestScore) {
                bestScore = score;
                best = entry.getKey();
            }
        }
        return bestScore > 0 ? best : "general";
    }

    /**
     * 生成回答 - 支持组合意图 + 上下文
     */
    private String generateReply(String intent, String userMsg, String snakeName, List<String> recentMsgs) {
        // 先查具体蛇
        SnakeSpecies snake = snakeName != null ? findSnake(snakeName) : null;

        return switch (intent) {
            // ========== 组合意图（更智能） ==========
            case "combo_snake_aid" -> replySnakeAidCombo(snakeName, snake);
            case "combo_snake_symptom" -> replySnakeSymptomCombo(snakeName, snake);
            case "combo_snake_venom" -> replySnakeVenomCombo(snakeName, snake);
            case "combo_snake_hospital" -> replySnakeHospitalCombo(snakeName, snake);

            // ========== 单一意图 ==========
            case "snake_identify" -> replySnakeIdentify(userMsg, snake);
            case "first_aid" -> replyFirstAid();
            case "symptom", "snake_bite_symptom" -> replySymptom();
            case "hospital" -> replyHospital();
            case "prevention" -> replyPrevention();
            case "venom_type" -> replyVenomType();
            default -> replyGeneral();
        };
    }

    // ======= 组合回答（更智能） =======

    private String replySnakeAidCombo(String snakeName, SnakeSpecies snake) {
        StringBuilder sb = new StringBuilder();
        boolean venomous = snake != null && snake.getIsVenomous() != null && snake.getIsVenomous() == 1;
        String venomType = snake != null && snake.getVenomType() != null ? snake.getVenomType() : "";

        sb.append("🚨 **被").append(snakeName != null ? snakeName : "毒蛇").append("咬伤！急救要点** 🚨\n\n");

        if (venomous) {
            sb.append("⚠️ **这是毒蛇！立即就医！**\n\n");
        }

        // 根据毒型给针对性建议
        if (venomType.contains("神经")) {
            sb.append("🧪 属于**神经毒类**（如银环蛇/眼镜蛇）：\n");
            sb.append("   → 伤口可能不红不肿，但数小时内会呼吸困难！\n");
            sb.append("   → 重点观察呼吸，一旦出现困难立刻告诉医生\n\n");
        } else if (venomType.contains("血循") || venomType.contains("血液")) {
            sb.append("🩸 属于**血循毒类**（如五步蛇/竹叶青）：\n");
            sb.append("   → 伤口会剧烈疼痛、迅速肿胀、出血不止\n");
            sb.append("   → 不要压迫伤口，让毒液自然流出一些\n\n");
        } else if (!venomType.isEmpty()) {
            sb.append("🧪 毒型：").append(venomType).append("\n\n");
        }

        // 7 步急救（精简版，组合场景已在前缀针对性提醒）
        sb.append("**立即做这 5 件事**：\n");
        sb.append("① 保持冷静，不要跑，坐下躺下\n");
        sb.append("② 伤肢制动（固定在低于心脏位置）\n");
        sb.append("③ 用清水冲洗伤口 15 分钟（不要用碘酒/酒精）\n");
        sb.append("④ 摘掉戒指手镯（防止肿胀勒死）\n");
        sb.append("⑤ 立刻打 120 或去最近有血清的医院\n\n");

        sb.append("❌ **千万不要**：切开伤口 / 吸吮毒液 / 用止血带 / 冰敷\n");

        if (snake != null && snake.getIsVenomous() == 1) {
            sb.append("\n💉 可能需要 **").append(snakeName).append("抗蛇毒血清**，");
            sb.append("建议出发前打电话确认医院有没有！");
        }

        return sb.toString();
    }

    private String replySnakeSymptomCombo(String snakeName, SnakeSpecies snake) {
        StringBuilder sb = new StringBuilder();
        String venomType = snake != null && snake.getVenomType() != null ? snake.getVenomType() : "";

        sb.append("📋 **").append(snakeName != null ? snakeName : "毒蛇").append("咬伤典型症状**\n\n");

        if (venomType.contains("神经")) {
            sb.append("🧪 **神经毒类**：\n");
            sb.append("   • 伤口轻微疼痛或麻木，可能不红不肿\n");
            sb.append("   • 早期：眼睑下垂、视物模糊、声音嘶哑\n");
            sb.append("   • 中期：肌肉酸痛、吞咽困难、呼吸困难\n");
            sb.append("   • 晚期：呼吸肌麻痹、全身瘫痪、昏迷\n");
            sb.append("   ⏰ 进展很快！银环蛇咬伤可能 1-2 小时就危险！\n");
        } else if (venomType.contains("血循") || venomType.contains("血液")) {
            sb.append("🩸 **血循毒类**：\n");
            sb.append("   • 伤口剧烈灼痛、迅速肿胀（1-2 小时扩散到整个肢体）\n");
            sb.append("   • 伤口出血不止、皮肤出现紫色瘀斑\n");
            sb.append("   • 可能出现血泡、组织坏死（发黑溃烂）\n");
            sb.append("   • 严重时头晕、心悸、血压下降、肾衰竭\n");
        } else {
            sb.append("   • 一般毒蛇咬伤共同症状：\n");
            sb.append("     → 伤口疼痛、肿胀、出血\n");
            sb.append("     → 头晕、恶心、心慌\n");
            sb.append("     → 严重时出现对应毒型的典型表现\n");
        }

        sb.append("\n⚠️ **注意**：被 ").append(snakeName != null ? snakeName : "毒蛇")
                .append(" 咬伤后，哪怕症状很轻，");
        sb.append("也**必须立即就医**！有些毒型（如银环蛇）早期可能看起来没事，\n几小时后突然恶化！");

        return sb.toString();
    }

    private String replySnakeVenomCombo(String snakeName, SnakeSpecies snake) {
        StringBuilder sb = new StringBuilder();
        String venomType = snake != null && snake.getVenomType() != null ? snake.getVenomType() : "未知";

        sb.append("🧪 **").append(snakeName != null ? snakeName : "这条蛇").append("的毒性**\n\n");
        sb.append("毒型：**").append(venomType).append("**\n\n");

        if (venomType.contains("神经")) {
            sb.append("🔵 神经毒作用于神经系统：\n");
            sb.append("   → 阻断神经-肌肉接头，导致肌肉麻痹\n");
            sb.append("   → 危险：呼吸肌麻痹 → 窒息死亡\n");
            sb.append("   → 典型蛇种：银环蛇、眼镜蛇、海蛇\n");
        } else if (venomType.contains("血循") || venomType.contains("血液")) {
            sb.append("🔴 血循毒作用于血液循环：\n");
            sb.append("   → 破坏血管壁和凝血因子\n");
            sb.append("   → 危险：出血不止、组织坏死、肾衰竭\n");
            sb.append("   → 典型蛇种：五步蛇、竹叶青、蝰蛇\n");
        } else if (venomType.contains("混合")) {
            sb.append("🟠 混合毒 = 神经毒 + 血循毒：\n");
            sb.append("   → 两种毒性同时发作，更凶险\n");
            sb.append("   → 典型蛇种：眼镜王蛇、蝮蛇\n");
        }

        sb.append("\n💡 知道毒型后，医生可以快速选用对应类型的抗蛇毒血清！");
        return sb.toString();
    }

    private String replySnakeHospitalCombo(String snakeName, SnakeSpecies snake) {
        StringBuilder sb = new StringBuilder();
        String venomType = snake != null && snake.getVenomType() != null ? snake.getVenomType() : "";

        sb.append("🏥 **被").append(snakeName != null ? snakeName : "毒蛇").append("咬伤该去哪**\n\n");
        sb.append("✅ **必须去有抗蛇毒血清的医院！**\n\n");

        if (venomType.contains("神经")) {
            sb.append("💉 需要：**银环蛇毒血清 / 眼镜蛇毒血清**\n");
        } else if (venomType.contains("血循") || venomType.contains("血液")) {
            sb.append("💉 需要：**五步蛇毒血清 / 蝰蛇毒血清 / 竹叶青毒血清**\n");
        } else if (venomType.contains("混合")) {
            sb.append("💉 需要：**多种血清联合使用**（如眼镜王蛇毒血清）\n");
        }

        sb.append("\n📍 **去哪里找血清**：\n");
        sb.append("   ① App 首页 → 「附近医院」查看合作医院清单\n");
        sb.append("   ② 直接打 **120** 告诉急救人员「被毒蛇咬伤」\n");
        sb.append("   ③ 出发前可以打医院电话确认有没有对应血清\n\n");
        sb.append("⏰ **黄金时间**：咬伤后 6 小时内使用血清效果最好！");

        return sb.toString();
    }

    // ======= 单一意图回答（也升级了） =======

    private String replySnakeIdentify(String msg, SnakeSpecies snake) {
        // 如果有蛇种详情
        if (snake != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("🐍 **").append(snake.getName()).append("**\n\n");
            sb.append("🆔 学名：").append(snake.getScientificName() != null ? snake.getScientificName() : "暂无").append("\n\n");
            sb.append("☠️ 是否有毒：").append(snake.getIsVenomous() != null && snake.getIsVenomous() == 1
                    ? "✅ **有毒！** 请立即就医" : "❌ 无毒").append("\n\n");
            if (snake.getVenomType() != null) {
                sb.append("🧪 毒型：").append(snake.getVenomType()).append("\n\n");
            }
            if (snake.getDistribution() != null) {
                sb.append("📍 主要分布：").append(snake.getDistribution()).append("\n\n");
            }
            if (snake.getAppearanceDesc() != null) {
                sb.append("📖 特征：").append(snake.getAppearanceDesc()).append("\n\n");
            }
            if (snake.getIsVenomous() != null && snake.getIsVenomous() == 1) {
                sb.append("⚠️ **重要提示**：如果被咬请立即用手机拍下蛇（不要抓它！），\n");
                sb.append("保持冷静、制动伤肢、尽快前往有抗蛇毒血清的医疗机构！");
            } else {
                sb.append("✅ 无毒蛇咬伤一般无大碍，但建议清洁伤口、打破伤风以防感染。");
            }
            return sb.toString();
        }

        // 没匹配到，让用户描述特征
        return """
                🐍 我没认出这条蛇，给我更多线索吧！

                告诉我：
                ① 蛇的颜色（如黑黄相间/绿色/棕色带花纹）
                ② 头形（三角形头 → 大多数毒蛇；圆形头 → 多数无毒蛇）
                ③ 体型（多长？粗细？）
                ④ 在哪里发现的（南方/北方？山区/平原？水边？）
                ⑤ 你还记得蛇名的一部分吗？

                💡 或者直接输入蛇名试试：「银环蛇」「竹叶青」「五步蛇」
                """;
    }

    private String replyFirstAid() {
        return """
                🚨 **毒蛇咬伤急救 7 步走** 🚨

                ① **保持冷静** → 不要慌张，不要剧烈运动
                ② **制动伤肢** → 坐下/躺下，把咬伤的腿/手固定在低于心脏的位置
                ③ **移除紧箍物** → 摘掉戒指、手镯、袜子（肿胀会勒紧）
                ④ **用清水冲洗伤口** → 至少 15 分钟，不要用碘酒/酒精
                ⑤ **不要做这些！** → ❌不要切开伤口 ❌不要吸吮毒液 ❌不要用止血带 ❌不要冰敷
                ⑥ **立刻就医** → 打 120 或开车去最近的医院
                ⑦ **拍下蛇的照片** → 如果能安全拍到（不要抓！），告诉医生蛇的特征

                💡 想知道**某条特定蛇**该怎么急救？告诉我蛇的名字！
                   比如「被五步蛇咬了怎么办？」我会针对性回答
                """;
    }

    private String replySymptom() {
        return """
                📋 **毒蛇咬伤常见症状**

                🧪 **神经毒类**（银环蛇、眼镜蛇、海蛇）：
                   → 伤口可能不红不肿，但非常危险！
                   → 早期：眼睑下垂、视物模糊、声音嘶哑
                   → 中期：肌肉酸痛、吞咽困难
                   → 晚期：呼吸困难、呼吸麻痹 → 窒息
                   ⏰ 银环蛇咬伤可能 1-2 小时就到危险阶段！

                🩸 **血循毒类**（五步蛇、竹叶青、蝰蛇）：
                   → 伤口剧烈灼痛、迅速肿胀（1-2 小时扩散整个肢体）
                   → 出血不止、皮肤瘀斑、血泡
                   → 严重：组织坏死（发黑）、肾衰竭、心悸休克

                🟠 **混合毒**（眼镜王蛇、蝮蛇）：两者同时出现

                🏥 **无论出现什么症状，哪怕很轻微，都要立即就医！**

                💡 想知道**某条蛇的特定症状**？告诉我蛇名！
                   比如「竹叶青咬伤有什么症状？」
                """;
    }

    private String replyHospital() {
        return """
                🏥 **就医指南**

                ✅ **毒蛇咬伤必须去有抗蛇毒血清的医院！**

                📍 **怎么找**：
                   ① App → 「附近医院」页面查看合作医院清单
                   ② 直接拨 **120** 告诉急救人员「被毒蛇咬伤」
                   ③ 出发前打电话确认医院有没有血清

                ⚠️ **重要提醒**：
                   • 血清分种类！不同毒蛇需要不同血清
                   • 告诉医生蛇的样子 / 拍的照片，帮助快速判断
                   • 如果知道蛇名，告诉我，我帮你查要用哪种血清

                🚨 **黄金 6 小时**：越早用血清越好！超过 8 小时效果大减。
                """;
    }

    private String replyPrevention() {
        return """
                🛡️ **野外防蛇咬伤指南**

                🌿 **出发前**：
                   • 穿长裤、长袖、高帮鞋（厚皮革/橡胶最好）
                   • 戴手套清理灌木/草丛
                   • 带一根长棍 → **打草惊蛇**！

                🌳 **行进时**：
                   • 走已有路径，少钻浓密草丛
                   • 夜间用强光手电照亮前方
                   • 不要手伸进树洞、岩石缝

                🏕️ **营地**：
                   • 帐篷拉链随时拉好
                   • 物品放高处，不要堆地上
                   • 清晨/傍晚是蛇活动高峰期

                💡 记住：蛇一般不会主动攻击人！
                看到蛇 → 慢慢后退，不要惊吓它！
                """;
    }

    private String replyVenomType() {
        return """
                🧪 **毒蛇毒型科普**

                🔵 **神经毒**（银环蛇、眼镜蛇、海蛇）：
                   作用于神经系统 → 肌肉麻痹、呼吸衰竭
                   伤口可能不红不肿 → 但数小时内可致命！

                🔴 **血循毒**（五步蛇、竹叶青、蝰蛇）：
                   破坏血管和凝血 → 肿胀、出血、组织坏死
                   伤口剧痛、迅速肿胀

                🟠 **混合毒**（眼镜王蛇、蝮蛇）：
                   两者同时出现 → 更加凶险

                ✅ 知道毒型可帮助医生快速选血清。
                但对普通人来说 → **被蛇咬了立刻就医**更重要！

                💡 想知道**某条蛇的毒型**？告诉我蛇名！
                """;
    }

    private String replyGeneral() {
        return """
                🐍 你好！我是**蛇伤急救 AI 助手** 🐍

                我可以帮你（直接问就行）：
                📸 **识别蛇种** → 「银环蛇有毒吗？」
                🚨 **急救指导** → 「被蛇咬了怎么办？」
                🩺 **特定蛇急救** → 「被五步蛇咬了怎么办？」（更精准！）
                📋 **特定蛇症状** → 「竹叶青咬伤有什么症状？」
                🏥 **就医建议** → 「附近哪里有血清？」
                🧪 **毒型科普** → 「银环蛇是什么毒？」
                🛡️ **预防方法** → 「野外怎么防蛇？」

                💬 **试试这些**：
                • 「被银环蛇咬了怎么办？」
                • 「五步蛇有什么症状？」
                • 「竹叶青毒性大吗？」

                ⚠️ 我提供参考信息，不能替代专业医疗诊断！
                真正被蛇咬伤 → **立即拨打 120！**
                """;
    }

    // ======= 工具 =======

    private void saveHistory(Long userId, String role, String content, String intent) {
        try {
            AiChatHistory h = new AiChatHistory();
            h.setUserId(userId);
            h.setRole(role);
            h.setContent(content);
            h.setIntent(intent);
            h.setCreateTime(LocalDateTime.now());
            historyMapper.insert(h);
        } catch (Exception e) {
            log.warn("save ai history failed: {}", e.getMessage());
        }
    }

    public List<AiChatHistory> getHistory(Long userId) {
        return historyMapper.selectList(
                new LambdaQueryWrapper<AiChatHistory>()
                        .eq(AiChatHistory::getUserId, userId)
                        .orderByDesc(AiChatHistory::getCreateTime)
                        .last("LIMIT 20")
        );
    }

    public void clearHistory(Long userId) {
        historyMapper.delete(new LambdaQueryWrapper<AiChatHistory>()
                .eq(AiChatHistory::getUserId, userId));
    }
}
