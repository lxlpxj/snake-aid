package cn.snake.snakeaidbackend.service;

import cn.snake.snakeaidbackend.entity.SnakeSpecies;
import cn.snake.snakeaidbackend.mapper.SnakeSpeciesMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI 视觉蛇种识别服务
 *
 * 支持多 provider（豆包/通义千问/智谱），未配置 API Key 时自动降级为"请用户描述特征"
 *
 * 配置示例（application.yml）：
 *   ai:
 *     vision:
 *       enabled: true
 *       provider: doubao        # doubao | qwen | glm
 *       api-key: your-key-here
 *       model: doubao-vision-pro
 */
@Slf4j
@Service
public class AIVisionService {

    @Value("${ai.vision.enabled:false}")
    private boolean enabled;

    @Value("${ai.vision.provider:doubao}")
    private String provider;

    @Value("${ai.vision.api-key:}")
    private String apiKey;

    @Value("${ai.vision.model:doubao-vision-pro-32k}")
    private String model;

    @Resource
    private SnakeSpeciesMapper snakeMapper;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(10))
            .build();

    /**
     * 识别蛇种
     *
     * @param base64Image 用户上传图片的 base64（不带 data: 前缀）
     * @param mimeType    图片类型，默认 image/jpeg
     */
    public Map<String, Object> recognize(String base64Image, String mimeType) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (!enabled || apiKey == null || apiKey.isBlank()) {
            log.info("AI vision disabled or no API key, fallback to feature-guided recognition");
            return buildFallbackResult();
        }

        try {
            String providerUpper = provider.toUpperCase(Locale.ROOT);
            String aiReply = switch (providerUpper) {
                case "DEEPSEEK" -> callDeepSeek(base64Image, mimeType);
                case "DOUBAO"   -> callDoubao(base64Image, mimeType);
                case "QWEN"     -> callQwen(base64Image, mimeType);
                case "GLM"      -> callGlm(base64Image, mimeType);
                default         -> throw new IllegalStateException("Unknown provider: " + provider);
            };

            log.info("AI vision reply ({}): {}", provider, aiReply.length() > 200 ? aiReply.substring(0, 200) + "..." : aiReply);

            // 从 AI 回复里提取蛇名（尝试匹配 DB 中的蛇种）
            List<String> matchedSnakeNames = findSnakeNamesInReply(aiReply);
            List<SnakeSpecies> matchedSnakes = new ArrayList<>();
            for (String name : matchedSnakeNames) {
                SnakeSpecies s = snakeMapper.selectOne(
                        new LambdaQueryWrapper<SnakeSpecies>()
                                .eq(SnakeSpecies::getName, name)
                                .last("LIMIT 1")
                );
                if (s != null) matchedSnakes.add(s);
            }

            result.put("aiReply", aiReply);
            result.put("matchedSnakes", matchedSnakes);
            result.put("confidence", estimateConfidence(matchedSnakeNames, aiReply));
            result.put("success", true);
            result.put("usingAI", true);

        } catch (Exception e) {
            log.error("AI vision call failed, fallback: {}", e.getMessage());
            result = buildFallbackResult();
            result.put("aiError", e.getMessage());
        }

        return result;
    }

    /** 降级结果：提示用户描述特征 */
    private Map<String, Object> buildFallbackResult() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("success", true);
        r.put("usingAI", false);
        r.put("message", "⚠ AI 视觉识别未启用（未配置 API Key）。请描述以下特征，我帮你匹配：");
        r.put("prompt", Arrays.asList(
                "1. 头形：三角形？还是椭圆形/圆形？",
                "2. 花纹：横斑纹？纵条纹？还是斑点？",
                "3. 颜色：黑色？绿色？棕色？还是花色？",
                "4. 尾部：尖细？还是粗壮？有红尾吗？",
                "5. 发现地点：南方山区？还是北方平原？水边？"
        ));

        // 顺便把现有蛇种列表返回，让用户对照
        List<SnakeSpecies> all = snakeMapper.selectList(
                new LambdaQueryWrapper<SnakeSpecies>()
                        .eq(SnakeSpecies::getAuditStatus, 2)
                        .eq(SnakeSpecies::getStatus, 1)
                        .last("LIMIT 10")
        );
        r.put("referenceSnakes", all);
        return r;
    }

    /**
     * 调 DeepSeek 视觉 API
     * 文档: https://platform.deepseek.com/
     * 模型: deepseek-vl2（多模态，支持图片）
     */
    private String callDeepSeek(String base64, String mime) throws IOException, InterruptedException {
        String url = "https://api.deepseek.com/v1/chat/completions";

        String json = """
                {
                  "model": "%s",
                  "reasoning_effort": "none",
                  "messages": [
                    {
                      "role": "system",
                      "content": "你是中国蛇类专家。只能根据以下已知蛇种回答，不确定就说不确定。中国常见毒蛇：银环蛇、金环蛇、中华眼镜蛇、舟山眼镜蛇、尖吻蝮(五步蛇)、竹叶青、短尾蝮、原矛头蝮、圆斑蝰、眼镜王蛇、莽山烙铁头。无毒蛇：王锦蛇、乌梢蛇、黑眉锦蛇。回答格式：先给出蛇名（如果确定），再简要说明识别依据（花纹/颜色/头形/尾部）和毒性。"
                    },
                    {
                      "role": "user",
                      "content": [
                        {"type": "text", "text": "这是什么蛇？帮我识别一下。"},
                        {"type": "image_url", "image_url": {"url": "data:%s;base64,%s"}}
                      ]
                    }
                  ],
                  "max_tokens": 600
                }
                """.formatted(model, mime, base64);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .timeout(java.time.Duration.ofSeconds(45))
                .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new IOException("DeepSeek API error: HTTP " + resp.statusCode() + " " + resp.body());
        }
        return extractAiContent(resp.body());
    }

    /**
     * 调豆包视觉 API（字节跳动）
     * 文档: https://www.volcengine.com/docs/82379
     */
    private String callDoubao(String base64, String mime) throws IOException, InterruptedException {
        String url = "https://ark.cn-beijing.volces.com/api/v3/chat/completions";

        String json = """
                {
                  "model": "%s",
                  "messages": [
                    {
                      "role": "system",
                      "content": "你是中国蛇类专家。只能根据以下已知蛇种回答，不确定就说不确定。中国常见毒蛇：银环蛇、金环蛇、中华眼镜蛇、舟山眼镜蛇、尖吻蝮(五步蛇)、竹叶青、短尾蝮、原矛头蝮、圆斑蝰、眼镜王蛇、莽山烙铁头。无毒蛇：王锦蛇、乌梢蛇、黑眉锦蛇。输出格式：先给蛇名（用括号括起来），再简要说明特征和毒性。"
                    },
                    {
                      "role": "user",
                      "content": [
                        {"type": "text", "text": "这是什么蛇？帮我识别一下，要中文蛇名。"},
                        {"type": "image_url", "image_url": {"url": "data:%s;base64,%s"}}
                      ]
                    }
                  ],
                  "max_tokens": 500
                }
                """.formatted(model, mime, base64);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .timeout(java.time.Duration.ofSeconds(30))
                .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new IOException("Doubao API error: HTTP " + resp.statusCode() + " " + resp.body());
        }
        return extractAiContent(resp.body());
    }

    /**
     * 调通义千问视觉 API（阿里）
     */
    private String callQwen(String base64, String mime) throws IOException, InterruptedException {
        String url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

        String json = """
                {
                  "model": "%s",
                  "messages": [
                    {"role": "system", "content": "你是中国蛇类专家，只能根据图片识别。"},
                    {"role": "user", "content": [
                        {"type": "text", "text": "这是什么蛇？给出中文蛇名。"},
                        {"type": "image_url", "image_url": {"url": "data:%s;base64,%s"}}
                    ]}
                  ]
                }
                """.formatted(model, mime, base64);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .timeout(java.time.Duration.ofSeconds(30))
                .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new IOException("Qwen API error: HTTP " + resp.statusCode());
        }
        return extractAiContent(resp.body());
    }

    /**
     * 调智谱视觉 API
     */
    private String callGlm(String base64, String mime) throws IOException, InterruptedException {
        String url = "https://open.bigmodel.cn/api/paas/v4/chat/completions";

        String json = """
                {
                  "model": "%s",
                  "messages": [
                    {"role": "user", "content": [
                        {"type": "text", "text": "这是什么蛇？给出中文蛇名。"},
                        {"type": "image_url", "image_url": {"url": "data:%s;base64,%s"}}
                    ]}
                  ]
                }
                """.formatted(model, mime, base64);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .timeout(java.time.Duration.ofSeconds(30))
                .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new IOException("GLM API error: HTTP " + resp.statusCode());
        }
        return extractAiContent(resp.body());
    }

    /** 从 JSON 响应里提取 choices[0].message.content（跳过 reasoning_content 思考过程） */
    private String extractAiContent(String json) {
        try {
            // 先找完整的 message 对象（跳过 reasoning_content，只取 content）
            // 格式: "message":{"reasoning_content":"...","content":"..."}
            Pattern p = Pattern.compile("\"message\"\\s*:\\s*\\{.*?\"content\"\\s*:\\s*\"(.*?)\".*?\\}", Pattern.DOTALL);
            Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1)
                        .replace("\\\"", "\"")
                        .replace("\\n", "\n")
                        .replace("\\t", "\t");
            }
            // 备选：直接找 content（注意跳过 reasoning_content）
            Pattern p2 = Pattern.compile("(?:\"content\"\\s*:\\s*\"(?!.*reasoning)(.*?)\")", Pattern.DOTALL);
            Matcher m2 = p2.matcher(json);
            if (m2.find()) {
                String raw = m2.group(1);
                // 简单过滤：取最后一段不是 reasoning 的
                String[] parts = json.split("\"content\"");
                for (int i = parts.length - 1; i >= 0; i--) {
                    String part = parts[i];
                    if (!part.contains("reasoning_content")) {
                        Matcher pm = Pattern.compile("\"\\s*:\\s*\"(.*?)\"").matcher(part);
                        if (pm.find()) return pm.group(1).replace("\\\"", "\"").replace("\\n", "\n").replace("\\t", "\t");
                    }
                }
            }
            // 再备选：找 text 字段
            Pattern p3 = Pattern.compile("\"text\"\\s*:\\s*\"(.*?)\"");
            Matcher m3 = p3.matcher(json);
            if (m3.find()) return m3.group(1);
        } catch (Exception e) {
            log.warn("extract content failed: {}", e.getMessage());
        }
        return json.length() > 500 ? json.substring(0, 500) + "..." : json;
    }

    /** 在 AI 回复里找已知蛇名（排除"不是XX""排除XX"等否定语境） */
    private List<String> findSnakeNamesInReply(String reply) {
        if (reply == null) return List.of();
        // AI 明确说不确定/无法确定 → 不匹配任何蛇
        if (reply.contains("无法确定") || reply.contains("不确定") || reply.contains("无法判断")) {
            return List.of();
        }

        List<SnakeSpecies> all = snakeMapper.selectList(
                new LambdaQueryWrapper<SnakeSpecies>().eq(SnakeSpecies::getAuditStatus, 2)
        );
        List<String> found = new ArrayList<>();
        String clean = reply;
        int thinkIdx = clean.lastIndexOf("</think>");
        if (thinkIdx >= 0) clean = clean.substring(thinkIdx);
        int thinkIdx2 = clean.indexOf("**识别结果**");
        if (thinkIdx2 >= 0) clean = clean.substring(thinkIdx2);

        for (SnakeSpecies s : all) {
            String name = s.getName();
            if (!clean.contains(name)) continue;
            int idx = clean.indexOf(name);
            String before = idx >= 4 ? clean.substring(Math.max(0, idx - 4), idx) : "";
            // 检查后面 8 个字符有没有否定
            int afterEnd = Math.min(clean.length(), idx + name.length() + 8);
            String after = clean.substring(idx + name.length(), afterEnd);
            // 也检查后面有没有"均不符""不匹配""差异较大"
            String fullContext = before + after;
            if (fullContext.contains("不是") || fullContext.contains("排除") || fullContext.contains("非")
                    || fullContext.contains("不符") || fullContext.contains("不匹配") || fullContext.contains("差异")
                    || fullContext.contains("均不") || fullContext.contains("也与")) {
                continue;
            }
            if (!found.contains(name)) found.add(name);
        }
        if (found.size() > 3) {
            return found.subList(0, 3);
        }
        return found;
    }

    /** 根据匹配数量估计置信度 */
    private double estimateConfidence(List<String> matched, String reply) {
        if (matched.size() == 1) return 0.85;
        if (matched.size() > 1) return 0.6;
        // AI 自己说不确定的话置信度低
        if (reply != null && (reply.contains("不确定") || reply.contains("可能") || reply.contains("无法"))) {
            return 0.4;
        }
        return 0.5;
    }
}
