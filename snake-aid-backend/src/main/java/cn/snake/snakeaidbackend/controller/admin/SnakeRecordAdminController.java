package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.SnakeRecord;
import cn.snake.snakeaidbackend.service.SnakeRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/snake-record")
@Tag(name = "后台-蛇伤病历（统计用）")
public class SnakeRecordAdminController {

    @Resource
    private SnakeRecordService service;

    @Operation(summary = "分页查询所有病历")
    @GetMapping("/page")
    public Result<PageResult<SnakeRecord>> page(
            @RequestParam(name = "pageNum", defaultValue = "1") long current,
            @RequestParam(name = "pageSize", defaultValue = "10") long size,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String keyword) {
        return Result.success(service.page(current, size, severity, keyword));
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    public Result<SnakeRecord> get(@PathVariable Long id) {
        return Result.success(service.get(id));
    }
}
