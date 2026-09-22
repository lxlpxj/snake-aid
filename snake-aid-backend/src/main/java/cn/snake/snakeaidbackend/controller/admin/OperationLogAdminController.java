package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.common.RoleEnum;
import cn.snake.snakeaidbackend.entity.OperationLog;
import cn.snake.snakeaidbackend.mapper.OperationLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 后台-操作日志（按角色隔离可见范围）
 *
 * ┌────────────────┬───────────────────────┬──────────────────────────────────┐
 * │ 角色            │ 日志菜单名             │ 可见 category                    │
 * ├────────────────┼───────────────────────┼──────────────────────────────────┤
 * │ SYS_ADMIN      │ 系统操作日志           │ SYSTEM (账号/权限/异常) + LOGIN   │
 * │ CONTENT_ADMIN  │ 业务操作日志           │ CONTENT (蛇种/文章/医院/库存)     │
 * │                │                       │    + LOGIN                        │
 * │ MEDICAL_ADVISOR│ 审核操作日志           │ AUDIT (审核/内容编辑下线)         │
 * │                │                       │    + LOGIN                        │
 * └────────────────┴───────────────────────┴──────────────────────────────────┘
 */
@RestController
@RequestMapping("/admin/log")
@Tag(name = "后台-操作日志")
public class OperationLogAdminController {

    @Resource
    private OperationLogMapper logMapper;

    @Operation(summary = "当前登录角色能看到的日志分类")
    @GetMapping("/categories")
    public Result<List<String>> categories() {
        return Result.success(visibleCategories(currentRole()));
    }

    @Operation(summary = "操作日志分页查询（按当前角色自动过滤范围）")
    @GetMapping("/page")
    public Result<PageResult<OperationLog>> page(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) Long userId) {

        String role = currentRole();
        List<String> allowedCategories = visibleCategories(role);

        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();

        // 1. 强制按角色过滤可见分类（不能越权）
        wrapper.in(OperationLog::getCategory, allowedCategories);

        // 2. 可选的二次筛选
        if (StringUtils.hasText(category) && allowedCategories.contains(category)) {
            wrapper.eq(OperationLog::getCategory, category);
        }
        if (StringUtils.hasText(operation)) {
            wrapper.like(OperationLog::getOperation, operation);
        }
        if (userId != null && RoleEnum.SYS_ADMIN.getCode().equals(role)) {
            // 只有 SYS_ADMIN 能按 userId 筛其他人的日志
            wrapper.eq(OperationLog::getUserId, userId);
        }

        wrapper.orderByDesc(OperationLog::getCreateTime);
        return Result.success(PageResult.of(logMapper.selectPage(new Page<>(pageNum, pageSize), wrapper)));
    }

    /** 当前登录角色 */
    private String currentRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                .findFirst().orElse("");
    }

    /** 角色 → 可见的日志 category 列表 */
    private List<String> visibleCategories(String role) {
        if (RoleEnum.SYS_ADMIN.getCode().equals(role)) {
            return Arrays.asList("SYSTEM", "LOGIN");
        }
        if (RoleEnum.CONTENT_ADMIN.getCode().equals(role)) {
            return Arrays.asList("CONTENT", "LOGIN");
        }
        if (RoleEnum.MEDICAL_ADVISOR.getCode().equals(role)) {
            return Arrays.asList("AUDIT", "LOGIN");
        }
        // 兜底：未知角色什么都看不到
        return Arrays.asList("NONE");
    }
}
