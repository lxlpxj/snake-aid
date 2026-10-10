package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.Complication;
import cn.snake.snakeaidbackend.service.ComplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/complication")
@Tag(name = "后台-并发症处理流程")
public class ComplicationAdminController {

    @Resource
    private ComplicationService service;

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public Result<PageResult<Complication>> page(
            @RequestParam(name = "pageNum", defaultValue = "1") long current,
            @RequestParam(name = "pageSize", defaultValue = "10") long size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        return Result.success(service.page(current, size, category, keyword));
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    public Result<Complication> get(@PathVariable Long id) {
        return Result.success(service.get(id));
    }

    @Operation(summary = "新增或更新")
    @PostMapping("/save")
    public Result<Complication> save(@RequestBody Complication body) {
        return Result.success(service.save(body));
    }

    @Operation(summary = "MEDICAL_ADVISOR 审核发布")
    @PostMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable Long id) {
        service.audit(id);
        return Result.success();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.success();
    }
}
