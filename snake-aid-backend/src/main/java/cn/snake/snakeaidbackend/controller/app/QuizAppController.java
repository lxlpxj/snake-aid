package cn.snake.snakeaidbackend.controller.app;

import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.Question;
import cn.snake.snakeaidbackend.entity.UserQuiz;
import cn.snake.snakeaidbackend.security.JwtUtil;
import cn.snake.snakeaidbackend.service.PointsService;
import cn.snake.snakeaidbackend.service.QuizService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** App端-急救知识考试 */
@RestController
@RequestMapping("/app/quiz")
@Tag(name = "App端-急救知识考试")
public class QuizAppController {

    @Resource
    private QuizService quizService;
    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private PointsService pointsService;

    @Operation(summary = "随机抽题")
    @GetMapping("/pick")
    public Result<List<Question>> pick(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "10") Integer count) {
        return Result.success(quizService.pickQuestions(category, count));
    }

    @Operation(summary = "提交考试+自动判分")
    @PostMapping("/submit")
    public Result<Map<String, Object>> submit(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestBody Map<String, Object> body) {
        String category = (String) body.get("category");
        Integer durationSec = body.get("durationSec") != null ? ((Number) body.get("durationSec")).intValue() : 0;
        Map<Long, String> answers = new HashMap<>();

        // 兼容两种前端格式：
        // 格式 A: { answers: { "1": "A", "2": "B" } }
        // 格式 B: { details: [{ questionId: 1, userAnswer: "A" }, ...] }
        Object answersObj = body.get("answers");
        if (answersObj instanceof Map) {
            for (Map.Entry<?, ?> e : ((Map<?, ?>) answersObj).entrySet()) {
                try { answers.put(Long.parseLong(e.getKey().toString()), String.valueOf(e.getValue())); } catch (NumberFormatException ignored) {}
            }
        } else {
            Object detailsObj = body.get("details");
            if (detailsObj instanceof List) {
                for (Object item : (List<?>) detailsObj) {
                    if (item instanceof Map) {
                        Map<?, ?> m = (Map<?, ?>) item;
                        Object qid = m.get("questionId");
                        Object ans = m.get("userAnswer");
                        if (qid != null && ans != null) {
                            answers.put(((Number) qid).longValue(), ans.toString());
                        }
                    }
                }
            }
        }

        // 防空：如果 answers 还是空，直接返回 0 分不查库
        if (answers.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("score", 0); empty.put("totalScore", 0); empty.put("correctCount", 0);
            empty.put("totalCount", 0); empty.put("accuracy", 0); empty.put("details", List.of());
            return Result.success(empty);
        }
        Long userId = null;
        String userName = "匿名用户";
        try {
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);
                Claims claims = jwtUtil.parse(token);
                userId = jwtUtil.getUserId(claims);
                userName = jwtUtil.getUsername(claims);
            }
        } catch (Exception ignored) {
        }
        Map<String, Object> result = quizService.submitQuiz(userId, userName, category, durationSec, answers);

        // ===== 积分发放 =====
        if (userId != null && result != null) {
            try {
                int score = ((Number) result.getOrDefault("score", 0)).intValue();
                int quizId = result.get("quizId") != null ? ((Number) result.get("quizId")).intValue() : 0;
                // 基础分：完成考试 +30
                int got = pointsService.award(userId, "quiz_submit", (long) quizId,
                        "完成在线考试 " + (category != null ? category : "综合"));
                // 奖励：正确率 ≥ 80% 额外 +20
                if (score >= 80) {
                    got += pointsService.award(userId, "quiz_excellent", (long) quizId,
                            "考试成绩优秀（" + score + "分）");
                }
                result.put("pointsEarned", got);
            } catch (Exception ignore) {}
        }

        return Result.success(result);
    }

    @Operation(summary = "我的考试历史")
    @GetMapping("/history")
    public Result<List<UserQuiz>> history(
            @RequestHeader(value = "Authorization", required = false) String auth) {
        Long userId = null;
        try {
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);
                Claims claims = jwtUtil.parse(token);
                userId = jwtUtil.getUserId(claims);
            }
        } catch (Exception ignored) {
        }
        return Result.success(quizService.listUserQuizzes(userId));
    }

    @Operation(summary = "考试答题详情")
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        return Result.success(quizService.getQuizDetail(id));
    }
}
