package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.FirstAidStep;
import cn.snake.snakeaidbackend.service.FirstAidStepService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/** 后台 - 急救闯关关卡管理 */
@RestController
@RequestMapping("/admin/first-aid-step")
@Tag(name = "后台-急救闯关关卡")
public class FirstAidStepAdminController {

    @Resource
    private FirstAidStepService firstAidStepService;

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public Result<PageResult<FirstAidStep>> page(
            @RequestParam(name = "pageNum", defaultValue = "1") long current,
            @RequestParam(name = "pageSize", defaultValue = "10") long size,
            @RequestParam(required = false) String keyword) {
        QueryWrapper<FirstAidStep> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("title", keyword).or().like("hint", keyword));
        }
        wrapper.orderByAsc("step_order");
        Page<FirstAidStep> page = firstAidStepService.page(new Page<>(current, size), wrapper);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "获取全部（不分页，按排序）")
    @GetMapping("/list")
    public Result<java.util.List<FirstAidStep>> list() {
        QueryWrapper<FirstAidStep> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("step_order");
        return Result.success(firstAidStepService.list(wrapper));
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    public Result<FirstAidStep> get(@PathVariable Long id) {
        return Result.success(firstAidStepService.getById(id));
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<FirstAidStep> create(@RequestBody FirstAidStep body) {
        firstAidStepService.save(body);
        return Result.success(body);
    }

    @Operation(summary = "更新")
    @PutMapping
    public Result<FirstAidStep> update(@RequestBody FirstAidStep body) {
        firstAidStepService.updateById(body);
        return Result.success(body);
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        firstAidStepService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "启用/禁用切换")
    @PostMapping("/{id}/toggle")
    public Result<Void> toggle(@PathVariable Long id) {
        FirstAidStep step = firstAidStepService.getById(id);
        if (step != null) {
            step.setEnabled(step.getEnabled() != null && step.getEnabled() == 1 ? 0 : 1);
            firstAidStepService.updateById(step);
        }
        return Result.success();
    }
}
