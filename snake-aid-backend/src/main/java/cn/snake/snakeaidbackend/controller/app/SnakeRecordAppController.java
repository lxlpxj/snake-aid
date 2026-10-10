package cn.snake.snakeaidbackend.controller.app;

import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.SnakeRecord;
import cn.snake.snakeaidbackend.service.SnakeRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * APP端蛇伤病历 —— 自己的自己看
 *
 * 注：/app/** 在 SecurityConfig 全 permitAll，但病历接口
 * 通过 currentUserId() 获取 JWT 中的 userId 做归属校验。
 * 未登录用户调用会返回 401。
 */
@RestController
@RequestMapping("/app/snake-record")
@Tag(name = "APP-蛇伤病历")
public class SnakeRecordAppController {

    @Resource
    private SnakeRecordService service;

    private Long currentUserId() {
        try {
            Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (p instanceof Long) return (Long) p;
            if (p instanceof String) return Long.parseLong((String) p);
        } catch (Exception ignored) {}
        return null;
    }

    @Operation(summary = "我的病历列表")
    @GetMapping("/mine")
    public Result<List<SnakeRecord>> mine() {
        Long uid = currentUserId();
        if (uid == null) return Result.fail(401, "请先登录");
        return Result.success(service.listByUserId(uid));
    }

    @Operation(summary = "我的病历详情")
    @GetMapping("/{id}")
    public Result<SnakeRecord> get(@PathVariable Long id) {
        Long uid = currentUserId();
        if (uid == null) return Result.fail(401, "请先登录");
        SnakeRecord r = service.getMine(id, uid);
        if (r == null) return Result.fail(404, "病历不存在");
        return Result.success(r);
    }

    @Operation(summary = "创建/更新病历（自动归属当前用户）")
    @PostMapping("/save")
    public Result<SnakeRecord> save(@RequestBody SnakeRecord body) {
        Long uid = currentUserId();
        if (uid == null) return Result.fail(401, "请先登录");
        return Result.success(service.saveMine(body, uid));
    }

    @Operation(summary = "删除我的病历")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long uid = currentUserId();
        if (uid == null) return Result.fail(401, "请先登录");
        service.deleteMine(id, uid);
        return Result.success();
    }
}
