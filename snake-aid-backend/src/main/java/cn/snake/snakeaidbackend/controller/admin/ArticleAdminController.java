package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.Article;
import cn.snake.snakeaidbackend.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/article")
@Tag(name = "后台-科普文章管理")
public class ArticleAdminController {

    @Resource
    private ArticleService articleService;

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public Result<PageResult<Article>> page(
            @RequestParam(required = false) Long current,
            @RequestParam(required = false) Long pageNum,
            @RequestParam(required = false) Long size,
            @RequestParam(required = false) Long pageSize,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) String keyword) {
        long c = current != null ? current : (pageNum != null ? pageNum : 1L);
        long s = size != null ? size : (pageSize != null ? pageSize : 10L);
        return Result.success(articleService.pageForAdmin(c, s, auditStatus, keyword));
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<Long> create(@RequestBody Article entity) {
        return Result.success(articleService.create(entity));
    }

    @Operation(summary = "更新")
    @PutMapping
    public Result<Void> update(@RequestBody Article entity) {
        articleService.update(entity);
        return Result.success();
    }

    @Operation(summary = "下线")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.success();
    }

    @Operation(summary = "提交审核")
    @PostMapping("/{id}/submit-audit")
    public Result<Void> submitAudit(@PathVariable Long id) {
        articleService.submitAudit(id);
        return Result.success();
    }

    @Operation(summary = "审核通过/驳回")
    @PostMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable Long id,
                              @RequestBody Map<String, Object> body) {
        Long auditBy = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        articleService.audit(id, Boolean.TRUE.equals(body.get("pass")), auditBy);
        return Result.success();
    }
}
