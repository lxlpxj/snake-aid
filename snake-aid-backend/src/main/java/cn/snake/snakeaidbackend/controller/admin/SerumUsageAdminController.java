package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.SerumUsage;
import cn.snake.snakeaidbackend.service.SerumUsageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/serum-usage")
@Tag(name = "后台-血清使用规范")
public class SerumUsageAdminController {

    @Resource
    private SerumUsageService service;

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public Result<PageResult<SerumUsage>> page(
            @RequestParam(name = "pageNum", defaultValue = "1") long current,
            @RequestParam(name = "pageSize", defaultValue = "10") long size,
            @RequestParam(required = false) String keyword) {
        return Result.success(service.page(current, size, keyword));
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    public Result<SerumUsage> get(@PathVariable Long id) {
        return Result.success(service.get(id));
    }

    @Operation(summary = "新增或更新")
    @PostMapping("/save")
    public Result<SerumUsage> save(@RequestBody SerumUsage body) {
        return Result.success(service.save(body));
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.success();
    }
}
