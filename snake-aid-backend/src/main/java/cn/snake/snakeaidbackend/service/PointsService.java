package cn.snake.snakeaidbackend.service;

import cn.snake.snakeaidbackend.entity.AppUser;
import cn.snake.snakeaidbackend.entity.LevelConfig;
import cn.snake.snakeaidbackend.entity.UserPointsLog;
import cn.snake.snakeaidbackend.mapper.AppUserMapper;
import cn.snake.snakeaidbackend.mapper.LevelConfigMapper;
import cn.snake.snakeaidbackend.mapper.UserPointsLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户积分核心服务：发放 + 防刷 + 升级
 */
@Slf4j
@Service
public class PointsService {

    @Resource
    private AppUserMapper appUserMapper;
    @Resource
    private UserPointsLogMapper pointsLogMapper;
    @Resource
    private LevelConfigMapper levelConfigMapper;

    // ====== 积分规则：每种事件的基础分值 + 每日上限 ======
    private static final Map<String, int[]> RULES = new HashMap<>();
    // {事件名, [基础积分, 每日上限次数]}
    static {
        RULES.put("register_bonus",   new int[]{50, 0});   // 一次性（不检查上限，用 ref_id=null 去重）
        RULES.put("quiz_submit",      new int[]{30, 3});   // 30 分，每日最多 3 次
        RULES.put("quiz_excellent",   new int[]{20, 3});   // 考得好额外 +20
        RULES.put("snake_view",       new int[]{2,  10});
        RULES.put("guide_read",       new int[]{5,  5});
        RULES.put("article_read",     new int[]{3,  5});
        RULES.put("hospital_view",    new int[]{1,  0});   // 不限
        RULES.put("quiz_complete_bonus", new int[]{100, 0});
    }

    /**
     * 发放积分（对外统一入口）
     *
     * @param userId     用户
     * @param eventType  事件类型
     * @param refId      关联 ID（如 snake_id / article_id）——用于"同对象当日只加一次"
     * @param description 描述（可选，动态生成）
     * @return 实际发放的积分（0 = 被防刷拒绝）
     */
    @Transactional
    public int award(Long userId, String eventType, Long refId, String description) {
        if (userId == null || eventType == null) return 0;

        int[] rule = RULES.get(eventType);
        if (rule == null) {
            log.warn("未配置积分规则的事件类型: {}", eventType);
            return 0;
        }
        int basePoints = rule[0];
        int dailyCap   = rule[1];

        // ===== 防刷 1：每日次数上限 =====
        if (dailyCap > 0) {
            LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            long todayCount = pointsLogMapper.selectCount(
                    new LambdaQueryWrapper<UserPointsLog>()
                            .eq(UserPointsLog::getUserId, userId)
                            .eq(UserPointsLog::getEventType, eventType)
                            .ge(UserPointsLog::getCreateTime, todayStart)
            );
            if (todayCount >= dailyCap) {
                log.debug("用户 {} 事件 {} 达每日上限 {} 次，跳过加分", userId, eventType, dailyCap);
                return 0;
            }
        }

        // ===== 防刷 2：同 refId 当日只加一次 =====
        if (refId != null) {
            LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            Long exist = pointsLogMapper.selectOne(
                    new LambdaQueryWrapper<UserPointsLog>()
                            .eq(UserPointsLog::getUserId, userId)
                            .eq(UserPointsLog::getEventType, eventType)
                            .eq(UserPointsLog::getRefId, refId)
                            .ge(UserPointsLog::getCreateTime, todayStart)
                            .last("LIMIT 1")
            ) == null ? null : -1L;
            if (exist != null) {
                return 0;
            }
        }

        // ===== 防刷 3：register_bonus 只发一次 =====
        if ("register_bonus".equals(eventType)) {
            Long exist = pointsLogMapper.selectCount(
                    new LambdaQueryWrapper<UserPointsLog>()
                            .eq(UserPointsLog::getUserId, userId)
                            .eq(UserPointsLog::getEventType, eventType)
            );
            if (exist > 0) return 0;
        }

        // ===== 发放 =====
        AppUser user = appUserMapper.selectById(userId);
        if (user == null) return 0;

        int newPoints = (user.getPoints() == null ? 0 : user.getPoints()) + basePoints;
        int newLevel  = computeLevel(newPoints);

        user.setPoints(newPoints);
        user.setLevel(newLevel);
        appUserMapper.updateById(user);

        UserPointsLog plog = new UserPointsLog();
        plog.setUserId(userId);
        plog.setEventType(eventType);
        plog.setPoints(basePoints);
        plog.setRefId(refId);
        plog.setDescription(description != null ? description : buildDesc(eventType, basePoints));
        plog.setCreateTime(LocalDateTime.now());
        pointsLogMapper.insert(plog);

        log.info("[积分] 用户 {} 获得 {} 分 (事件={}), 总积分={}, 等级={}", userId, basePoints, eventType, newPoints, newLevel);
        return basePoints;
    }

    /**
     * 根据累积积分算等级
     */
    public int computeLevel(int totalPoints) {
        List<LevelConfig> configs = levelConfigMapper.selectList(
                new LambdaQueryWrapper<LevelConfig>().orderByDesc(LevelConfig::getRequiredPoints)
        );
        for (LevelConfig c : configs) {
            if (totalPoints >= c.getRequiredPoints()) return c.getLevel();
        }
        return 1;
    }

    /** 事件描述 */
    private String buildDesc(String event, int pts) {
        return switch (event) {
            case "quiz_submit"      -> "完成考试 +" + pts;
            case "quiz_excellent"   -> "考试成绩优秀 +" + pts;
            case "snake_view"       -> "学习蛇种知识 +" + pts;
            case "guide_read"       -> "阅读急救指南 +" + pts;
            case "article_read"     -> "阅读科普文章 +" + pts;
            case "hospital_view"    -> "查看附近医院 +" + pts;
            case "register_bonus"   -> "注册奖励 +" + pts;
            case "quiz_complete_bonus" -> "完成全部题库 +" + pts;
            default -> event + " +" + pts;
        };
    }

    // ====== App 端查询 ======

    /** 用户资料（带等级标题/图标的完整信息） */
    public Map<String, Object> getUserProfile(Long userId) {
        AppUser user = appUserMapper.selectById(userId);
        Map<String, Object> r = new HashMap<>();
        if (user == null) return r;
        r.put("id", user.getId());
        r.put("phone", user.getPhone());
        r.put("nickname", user.getNickname());
        r.put("avatar", user.getAvatar());
        r.put("points", user.getPoints() == null ? 0 : user.getPoints());
        r.put("level", user.getLevel() == null ? 1 : user.getLevel());

        // 找这一级的配置
        LevelConfig curConfig = levelConfigMapper.selectById(user.getLevel() == null ? 1 : user.getLevel());
        r.put("levelTitle", curConfig == null ? "" : curConfig.getTitle());
        r.put("levelIcon", curConfig == null ? "" : curConfig.getIcon());

        // 下一级（升级进度）
        List<LevelConfig> all = levelConfigMapper.selectList(null);
        all.sort((a, b) -> Integer.compare(a.getLevel(), b.getLevel()));
        LevelConfig next = all.stream().filter(c -> c.getLevel() > (user.getLevel() == null ? 1 : user.getLevel())).findFirst().orElse(null);
        if (next != null) {
            int curPts = user.getPoints() == null ? 0 : user.getPoints();
            int curNeed = curConfig == null ? 0 : curConfig.getRequiredPoints();
            int nextNeed = next.getRequiredPoints();
            int progress = Math.min(100, (int) Math.round((curPts - curNeed) * 100.0 / (nextNeed - curNeed)));
            r.put("nextLevel", next.getLevel());
            r.put("nextTitle", next.getTitle());
            r.put("progress", Math.max(0, progress));
            r.put("pointsToNext", Math.max(0, nextNeed - curPts));
        } else {
            r.put("nextLevel", user.getLevel());
            r.put("nextTitle", curConfig == null ? "" : curConfig.getTitle());
            r.put("progress", 100);
            r.put("pointsToNext", 0);
        }
        return r;
    }

    /** 积分流水分页 */
    public IPage<UserPointsLog> getLogs(Long userId, long current, long size) {
        return pointsLogMapper.selectPage(
                new Page<>(current, size),
                new LambdaQueryWrapper<UserPointsLog>()
                        .eq(UserPointsLog::getUserId, userId)
                        .orderByDesc(UserPointsLog::getCreateTime)
        );
    }

    /** 排行榜 TOP N */
    public List<Map<String, Object>> getLeaderboard(int topN) {
        List<AppUser> users = appUserMapper.selectList(
                new LambdaQueryWrapper<AppUser>()
                        .orderByDesc(AppUser::getPoints)
                        .last("LIMIT " + topN)
        );
        List<LevelConfig> all = levelConfigMapper.selectList(null);
        return users.stream().map(u -> {
            Map<String, Object> r = new HashMap<>();
            r.put("id", u.getId());
            r.put("nickname", u.getNickname());
            r.put("avatar", u.getAvatar());
            r.put("points", u.getPoints() == null ? 0 : u.getPoints());
            r.put("level", u.getLevel() == null ? 1 : u.getLevel());
            LevelConfig cfg = all.stream().filter(c -> c.getLevel().equals(u.getLevel())).findFirst().orElse(null);
            r.put("levelTitle", cfg == null ? "" : cfg.getTitle());
            r.put("levelIcon", cfg == null ? "" : cfg.getIcon());
            return r;
        }).toList();
    }

    /** 全部等级配置 */
    public List<LevelConfig> getAllLevels() {
        List<LevelConfig> all = levelConfigMapper.selectList(null);
        all.sort((a, b) -> Integer.compare(a.getLevel(), b.getLevel()));
        return all;
    }
}
