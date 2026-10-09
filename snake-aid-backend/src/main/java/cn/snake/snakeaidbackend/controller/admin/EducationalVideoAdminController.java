package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.EducationalVideo;
import cn.snake.snakeaidbackend.service.EducationalVideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/** 后台 - 科普视频管理 */
@RestController
@RequestMapping("/admin/video")
@Tag(name = "后台-科普视频管理")
public class EducationalVideoAdminController {

    @Resource
    private EducationalVideoService service;

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public Result<PageResult<EducationalVideo>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        return Result.success(service.page(current, size, category, status, keyword));
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    public Result<EducationalVideo> get(@PathVariable Long id) {
        return Result.success(service.get(id));
    }

    @Operation(summary = "新增/修改")
    @PostMapping
    public Result<EducationalVideo> save(@RequestBody EducationalVideo v) {
        return Result.success(service.save(v));
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(service.delete(id));
    }

    @Operation(summary = "启/下架")
    @PostMapping("/{id}/toggle")
    public Result<Boolean> toggle(@PathVariable Long id) {
        return Result.success(service.toggleStatus(id));
    }
}
