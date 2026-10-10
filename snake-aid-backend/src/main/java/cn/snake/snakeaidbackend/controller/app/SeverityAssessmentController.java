package cn.snake.snakeaidbackend.controller.app;

import cn.snake.snakeaidbackend.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 蛇伤严重程度评估（临床评分算法）
 *
 * 参考中国蛇伤急救指南 2023 版，按 5 个维度加权评分：
 *   1. 蛇毒类型（0-3 分）
 *   2. 咬伤部位（0-3 分）
 *   3. 局部症状（0-3 分）
 *   4. 全身症状（0-4 分）
 *   5. 伤后时间（0-2 分）
 *
 * 总分 → 分级：
 *   轻度  0-3 分 → 观察 + 基础处理
 *   中度  4-6 分 → 需要血清治疗
 *   重度  7-10 分 → 紧急抢救 + 血清 + 支持治疗
 *   极危 ≥11 分 → 立即 ICU
 */
@RestController
@RequestMapping("/app/assessment")
@Tag(name = "APP-蛇伤严重程度评估")
public class SeverityAssessmentController {

    @Operation(summary = "评估蛇伤严重程度")
    @PostMapping("/severity")
    public Result<Map<String, Object>> assess(@RequestBody Map<String, Object> params) {

        // 1. 蛇毒类型 0-3
        String venomType = (String) params.getOrDefault("venomType", "unknown");
        int venomScore = switch (venomType) {
            case "cytotoxic" -> 1;   // 细胞毒
            case "hemotoxic", "neurotoxic" -> 2;  // 血循毒 / 神经毒
            case "mixed" -> 3;       // 混合毒（最危险）
            default -> 0;            // unknown / non-venomous
        };

        // 2. 咬伤部位 0-3
        String location = (String) params.getOrDefault("location", "limb_lower");
        int locationScore = switch (location) {
            case "trunk", "head", "neck" -> 3;
            case "upper_arm" -> 2;
            case "forearm", "hand" -> 1;
            default -> 0;  // calf / foot / lower
        };

        // 3. 局部症状 0-3
        String localSymptom = (String) params.getOrDefault("localSymptom", "mild");
        int localScore = switch (localSymptom) {
            case "necrosis", "numbness" -> 3;
            case "severe" -> 2;      // 剧痛 + 肿胀 > 半肢体 + 水疱
            case "moderate" -> 1;    // 中痛 + 肿胀 < 半肢体
            default -> 0;            // 轻微疼痛 / 无肿胀
        };

        // 4. 全身症状 0-4（取最严重的）
        @SuppressWarnings("unchecked")
        List<String> systemicList = (List<String>) params.getOrDefault("systemicSymptoms", new ArrayList<>());
        int systemicScore = 0;
        // 按严重程度从高到低，取命中的最高级
        if (systemicList.contains("cardiac_arrest") || systemicList.contains("respiratory_arrest")) {
            systemicScore = 4;
        } else if (systemicList.contains("confusion") || systemicList.contains("seizure") || systemicList.contains("dyspnea")) {
            systemicScore = 3;
        } else if (systemicList.contains("hypotension") || systemicList.contains("vomiting") || systemicList.contains("palpitation")) {
            systemicScore = 2;
        } else if (systemicList.contains("dizziness") || systemicList.contains("nausea") || systemicList.contains("fatigue")) {
            systemicScore = 1;
        }

        // 5. 伤后时间 0-2
        Double hours = params.get("hours") != null ? ((Number) params.get("hours")).doubleValue() : null;
        int timeScore;
        if (hours == null || hours < 1) timeScore = 0;
        else if (hours <= 6) timeScore = 1;
        else timeScore = 2;

        // 总分
        int total = venomScore + locationScore + localScore + systemicScore + timeScore;

        // 分级
        String level;
        String levelName;
        String color;
        String advice;
        List<String> actions = new ArrayList<>();

        if (total <= 3) {
            level = "mild";
            levelName = "轻度";
            color = "#2E7D32";
            advice = "局部处理即可，密切观察 24 小时。如出现加重症状立即就医。";
            actions.add("保持冷静，制动伤肢");
            actions.add("用清水冲洗伤口，不要切开");
            actions.add("尽快前往有血清的医院备案");
            actions.add("观察有无肿胀扩大、头晕、恶心");
        } else if (total <= 6) {
            level = "moderate";
            levelName = "中度";
            color = "#E65100";
            advice = "需要尽快使用抗蛇毒血清（被咬后 4 小时内效果最佳）。";
            actions.add("立即拨打 120，说明被蛇咬伤");
            actions.add("拍下蛇的照片（安全前提下）");
            actions.add("保持伤肢低于心脏，不要活动");
            actions.add("前往有血清库存的医院（本应用可查）");
            actions.add("告知医生蛇种、咬伤时间、症状");
        } else if (total <= 10) {
            level = "severe";
            levelName = "重度";
            color = "#C62828";
            advice = "危险！可能出现呼吸衰竭、凝血障碍。必须紧急抢救 + 足量血清。";
            actions.add("🚨 立即拨打 120，强调\"重度蛇伤\"");
            actions.add("保持伤者平卧，头偏向一侧防误吸");
            actions.add("不要给食物和水");
            actions.add("记录咬伤时间，告知调度员最近的有血清医院");
            actions.add("到达医院后立即申请多价/单价血清");
            actions.add("监测呼吸、血压、尿量");
        } else {
            level = "critical";
            levelName = "极危";
            color = "#880E4F";
            advice = "危及生命！立即启动急救，可能需要 ICU 支持。";
            actions.add("🚨🚨 立即拨打 120 并启动 CPR 预备");
            actions.add("若呼吸停止立即人工呼吸");
            actions.add("若心跳停止立即胸外按压");
            actions.add("急送最近的有 ICU + 血清的医院");
            actions.add("告知医生：严重蛇伤 + 呼吸/循环不稳定");
            actions.add("准备大量血清 + 血液制品");
        }

        // 单项分解
        Map<String, Object> breakdown = new LinkedHashMap<>();
        breakdown.put("venomType", Map.of("label", venomTypeLabel(venomType), "score", venomScore, "max", 3));
        breakdown.put("location", Map.of("label", locationLabel(location), "score", locationScore, "max", 3));
        breakdown.put("localSymptom", Map.of("label", localLabel(localSymptom), "score", localScore, "max", 3));
        breakdown.put("systemicSymptoms", Map.of("label", systemicList.isEmpty() ? "无" : String.join(" + ", systemicList), "score", systemicScore, "max", 4));
        breakdown.put("hours", Map.of("label", hours == null ? "未填" : hours + " 小时", "score", timeScore, "max", 2));

        // 组装结果
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalScore", total);
        result.put("level", level);
        result.put("levelName", levelName);
        result.put("color", color);
        result.put("advice", advice);
        result.put("actions", actions);
        result.put("breakdown", breakdown);

        return Result.success(result);
    }

    private String venomTypeLabel(String t) {
        return switch (t) {
            case "cytotoxic" -> "细胞毒（局部坏死）";
            case "hemotoxic" -> "血循毒（凝血/出血）";
            case "neurotoxic" -> "神经毒（呼吸麻痹）";
            case "mixed" -> "混合毒";
            default -> "未知 / 无毒蛇";
        };
    }

    private String locationLabel(String l) {
        return switch (l) {
            case "head" -> "头部";
            case "neck" -> "颈部";
            case "trunk" -> "躯干";
            case "upper_arm" -> "上臂";
            case "forearm" -> "前臂";
            case "hand" -> "手部";
            case "calf" -> "小腿";
            case "foot" -> "足部";
            default -> "下肢（小腿/足）";
        };
    }

    private String localLabel(String s) {
        return switch (s) {
            case "necrosis" -> "组织坏死";
            case "numbness" -> "感觉丧失/麻木";
            case "severe" -> "剧痛 + 肿胀 > 半肢体 + 水疱";
            case "moderate" -> "中痛 + 肿胀 < 半肢体";
            case "mild" -> "轻微疼痛";
            default -> "无明显肿胀";
        };
    }
}
