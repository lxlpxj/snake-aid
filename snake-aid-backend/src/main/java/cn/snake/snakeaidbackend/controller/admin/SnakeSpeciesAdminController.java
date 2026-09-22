package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.SnakeSpecies;
import cn.snake.snakeaidbackend.security.JwtUtil;
import cn.snake.snakeaidbackend.service.SnakeSpeciesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/snake")
@Tag(name = "后台-蛇种库管理")
public class SnakeSpeciesAdminController {

    @Resource
    private SnakeSpeciesService snakeSpeciesService;

    @Resource
    private JwtUtil jwtUtil;

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public Result<PageResult<SnakeSpecies>> page(
            @RequestParam(required = false) Long current,
            @RequestParam(required = false) Long pageNum,
            @RequestParam(required = false) Long size,
            @RequestParam(required = false) Long pageSize,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) Integer isVenomous,
            @RequestParam(required = false) String keyword) {
        long c = current != null ? current : (pageNum != null ? pageNum : 1L);
        long s = size != null ? size : (pageSize != null ? pageSize : 10L);
        return Result.success(snakeSpeciesService.pageForAdmin(c, s, auditStatus, isVenomous, keyword));
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    public Result<SnakeSpecies> detail(@PathVariable Long id) {
        return Result.success(snakeSpeciesService.getDetailForApp(id));
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<Long> create(@RequestBody SnakeSpecies entity) {
        // TODO: 解析 imageUrls/isCovers
        return Result.success(snakeSpeciesService.create(entity, List.of(), List.of()));
    }

    @Operation(summary = "更新")
    @PutMapping
    public Result<Void> update(@RequestBody SnakeSpecies entity) {
        snakeSpeciesService.update(entity, List.of(), List.of());
        return Result.success();
    }

    @Operation(summary = "下线")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        snakeSpeciesService.delete(id);
        return Result.success();
    }

    @Operation(summary = "提交审核")
    @PostMapping("/{id}/submit-audit")
    public Result<Void> submitAudit(@PathVariable Long id) {
        snakeSpeciesService.submitAudit(id);
        return Result.success();
    }

    @Operation(summary = "审核通过/驳回")
    @PostMapping("/{id}/audit")
    public Result<Void> audit(@PathVariable Long id,
                              @RequestBody Map<String, Object> body) {
        boolean pass = Boolean.TRUE.equals(body.get("pass"));
        Long auditBy = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        snakeSpeciesService.audit(id, pass, auditBy);
        return Result.success();
    }
}
