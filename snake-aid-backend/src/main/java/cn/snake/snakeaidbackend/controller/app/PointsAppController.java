package cn.snake.snakeaidbackend.controller.app;

import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.LevelConfig;
import cn.snake.snakeaidbackend.entity.UserPointsLog;
import cn.snake.snakeaidbackend.security.JwtUtil;
import cn.snake.snakeaidbackend.service.PointsService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** App端-用户积分中心 */
@RestController
@RequestMapping("/app/points")
@Tag(name = "App端-用户积分中心")
public class PointsAppController {

    @Resource
    private PointsService pointsService;
    @Resource
    private JwtUtil jwtUtil;

    /** 取当前登录用户 id */
    private Long currentUserId() {
        Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return p instanceof Long ? (Long) p : null;
    }

    @Operation(summary = "我的积分 + 等级 + 升级进度")
    @GetMapping("/profile")
    public Result<Map<String, Object>> profile() {
        Long uid = currentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.success(pointsService.getUserProfile(uid));
    }

    @Operation(summary = "积分流水分页")
    @GetMapping("/logs")
    public Result<IPage<UserPointsLog>> logs(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size) {
        Long uid = currentUserId();
        if (uid == null) return Result.fail(401, "未登录");
        return Result.success(pointsService.getLogs(uid, current, size));
    }

    @Operation(summary = "等级配置表（全部）")
    @GetMapping("/levels")
    public Result<List<LevelConfig>> levels() {
        return Result.success(pointsService.getAllLevels());
    }

    @Operation(summary = "排行榜 TOP 10")
    @GetMapping("/leaderboard")
    public Result<List<Map<String, Object>>> leaderboard(
            @RequestParam(defaultValue = "10") int topN) {
        return Result.success(pointsService.getLeaderboard(topN));
    }

    /** 内部：主动触发一次积分发放（用于注册回调 / 管理员后台测试） */
    @PostMapping("/award")
    public Result<Integer> award(@RequestBody Map<String, Object> body) {
        String event = (String) body.get("eventType");
        Long refId    = body.get("refId")    != null ? ((Number) body.get("refId")).longValue() : null;
        String desc   = (String) body.get("description");
        Long uid      = body.get("userId")   != null ? ((Number) body.get("userId")).longValue() : currentUserId();
        int pts = pointsService.award(uid, event, refId, desc);
        return Result.success(pts);
    }
}
