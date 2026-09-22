package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.FirstAidGuide;
import cn.snake.snakeaidbackend.service.FirstAidGuideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/guide")
@Tag(name = "后台-急救指南管理")
public class FirstAidGuideAdminController {

    @Resource
    private FirstAidGuideService guideService;

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public Result<PageResult<FirstAidGuide>> page(
            @RequestParam(name = "pageNum", defaultValue = "1") long current,
            @RequestParam(name = "pageSize", defaultValue = "10") long size,
            @RequestParam(required = false) Integer auditStatus) {
        return Result.success(guideService.pageForAdmin(current, size, auditStatus));
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<FirstAidGuide> create(@RequestBody FirstAidGuide body) {
        FirstAidGuide saved = guideService.createSimple(body);
        return Result.success(saved);
    }

    @Operation(summary = "更新")
    @PutMapping
    public Result<FirstAidGuide> update(@RequestBody FirstAidGuide body) {
        FirstAidGuide saved = guideService.updateSimple(body);
        return Result.success(saved);
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        guideService.delete(id);
        return Result.success();
    }

    @Operation(summary = "提交审核")
    @PostMapping("/{id}/submit-audit")
    public Result<Void> submitAudit(@PathVariable Long id) {
        guideService.submitAudit(id);
        return Result.success();
    }

    @Operation(summary = "审核通过/驳回")
    @PostMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long auditBy = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        guideService.audit(id, Boolean.TRUE.equals(body.get("pass")), auditBy);
        return Result.success();
    }
}
