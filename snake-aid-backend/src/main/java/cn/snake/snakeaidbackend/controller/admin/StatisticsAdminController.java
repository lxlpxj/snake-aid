package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/admin/statistics")
@Tag(name = "Admin-Data Statistics")
public class StatisticsAdminController {

    @Resource private SnakeSpeciesMapper snakeMapper;
    @Resource private HospitalMapper hospitalMapper;
    @Resource private ArticleMapper articleMapper;
    @Resource private SysUserMapper sysUserMapper;
    @Resource private AccessStatisticsMapper accessStatsMapper;
    @Resource private JdbcTemplate jdbc;

    @Operation(summary = "Overview counts")
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("snakeCount", snakeMapper.selectCount(null));
        stats.put("hospitalCount", hospitalMapper.selectCount(null));
        stats.put("articleCount", articleMapper.selectCount(null));
        stats.put("userCount", sysUserMapper.selectCount(null));

        LocalDate today = LocalDate.now();
        stats.put("snakeFilterToday", queryTodayCount("snake_filter", today));
        stats.put("snakeViewToday", queryTodayCount("snake_view", today));
        stats.put("hospitalViewToday", queryTodayCount("hospital_view", today));
        stats.put("quizSubmitToday", queryTodayCount("quiz_submit", today));

        try {
            Integer quizCount = jdbc.queryForObject("SELECT COUNT(*) FROM user_quiz", Integer.class);
            stats.put("quizTotal", quizCount == null ? 0 : quizCount);
        } catch (Exception e) { stats.put("quizTotal", 0); }

        try {
            Integer qCount = jdbc.queryForObject("SELECT COUNT(*) FROM question WHERE status = 1", Integer.class);
            stats.put("questionTotal", qCount == null ? 0 : qCount);
        } catch (Exception e) { stats.put("questionTotal", 0); }

        return Result.success(stats);
    }

    @Operation(summary = "Last 14 days access trend")
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> trend() {
        LocalDate start = LocalDate.now().minusDays(13);
        String sql = "SELECT stat_date, event_type, SUM(count) as cnt " +
                     "FROM access_statistics WHERE stat_date >= ? " +
                     "GROUP BY stat_date, event_type ORDER BY stat_date ASC";
        return Result.success(jdbc.queryForList(sql, start));
    }

    @Operation(summary = "Top 10 provinces")
    @GetMapping("/province")
    public Result<List<Map<String, Object>>> provinceTop() {
        String sql = "SELECT province, SUM(count) as cnt " +
                     "FROM access_statistics WHERE event_type = 'snake_filter' AND province IS NOT NULL " +
                     "GROUP BY province ORDER BY cnt DESC LIMIT 10";
        return Result.success(jdbc.queryForList(sql));
    }

    @Operation(summary = "Snake traits distribution")
    @GetMapping("/snake-traits")
    public Result<Map<String, Object>> snakeTraits() {
        Map<String, Object> result = new HashMap<>();
        String toxicSql = "SELECT is_poisonous, COUNT(*) as cnt FROM snake_species WHERE is_deleted = 0 AND status = 1 GROUP BY is_poisonous";
        result.put("toxicity", jdbc.queryForList(toxicSql));
        String hazardSql = "SELECT status as hazard_level, COUNT(*) as cnt FROM snake_species WHERE is_deleted = 0 GROUP BY status ORDER BY status";
        result.put("hazard", jdbc.queryForList(hazardSql));
        String venomSql = "SELECT venom_type, COUNT(*) as cnt FROM snake_species WHERE is_deleted = 0 AND status = 1 AND venom_type IS NOT NULL GROUP BY venom_type";
        result.put("venomType", jdbc.queryForList(venomSql));
        return Result.success(result);
    }

    @Operation(summary = "Quiz statistics")
    @GetMapping("/quiz-stats")
    public Result<Map<String, Object>> quizStats() {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("aggregate", jdbc.queryForMap("SELECT COUNT(*) total, AVG(score) avg_score, MAX(score) max_score, MIN(score) min_score FROM user_quiz"));
        } catch (Exception ignored) {}
        try {
            result.put("byCategory", jdbc.queryForList("SELECT category, COUNT(*) quiz_count, AVG(score) avg_score FROM user_quiz GROUP BY category"));
        } catch (Exception ignored) {}
        try {
            result.put("topWrongQuestions", jdbc.queryForList(
                "SELECT q.question, COUNT(d.id) wrong_count " +
                "FROM user_quiz_detail d JOIN question q ON d.question_id = q.id " +
                "WHERE d.is_correct = 0 GROUP BY d.question_id ORDER BY wrong_count DESC LIMIT 5"));
        } catch (Exception ignored) {}
        return Result.success(result);
    }

    @Operation(summary = "Hospital + serum stats")
    @GetMapping("/hospital-stats")
    public Result<Map<String, Object>> hospitalStats() {
        Map<String, Object> result = new HashMap<>();
        try { result.put("byProvince", jdbc.queryForList("SELECT province, COUNT(*) as cnt FROM hospital GROUP BY province ORDER BY cnt DESC")); } catch (Exception ignored) {}
        try { result.put("serumStock", jdbc.queryForList("SELECT stock_level, COUNT(*) as cnt FROM serum_stock GROUP BY stock_level")); } catch (Exception ignored) {}
        return Result.success(result);
    }

    private int queryTodayCount(String eventType, LocalDate date) {
        try {
            Integer cnt = jdbc.queryForObject(
                "SELECT COALESCE(SUM(count), 0) FROM access_statistics WHERE event_type = ? AND stat_date = ?",
                Integer.class, eventType, date);
            return cnt == null ? 0 : cnt;
        } catch (Exception e) { return 0; }
    }
    @Operation(summary = "App 用户积分排行榜 TOP N")
    @GetMapping("/leaderboard")
    public Result<List<Map<String, Object>>> leaderboard(
            @RequestParam(defaultValue = "10") int topN) {
        String sql = "SELECT id, nickname, phone, points, level " +
                     "FROM app_user ORDER BY COALESCE(points, 0) DESC LIMIT " + topN;
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        return Result.success(list);
    }

    @Operation(summary = "App 用户等级分布")
    @GetMapping("/level-distribution")
    public Result<List<Map<String, Object>>> levelDistribution() {
        String sql = "SELECT level, COUNT(*) AS cnt FROM app_user GROUP BY level ORDER BY level";
        return Result.success(jdbc.queryForList(sql));
    }
}
