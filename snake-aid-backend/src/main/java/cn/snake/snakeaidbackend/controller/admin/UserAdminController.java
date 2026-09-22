package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.SysUser;
import cn.snake.snakeaidbackend.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/user")
@Tag(name = "后台-用户权限管理（仅 SYS_ADMIN 可访问）")
public class UserAdminController {

    @Resource
    private SysUserService sysUserService;

    @Operation(summary = "后台用户列表")
    @GetMapping("/page")
    public Result<PageResult<SysUser>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String roleType,
            @RequestParam(required = false) Integer status) {
        return Result.success(sysUserService.page(current, size, roleType, status));
    }

    @Operation(summary = "创建后台用户（密码自动 BCrypt 加密）")
    @PostMapping
    public Result<Long> create(@RequestBody SysUser entity) {
        return Result.success(sysUserService.create(entity));
    }

    @Operation(summary = "更新")
    @PutMapping
    public Result<Void> update(@RequestBody SysUser entity) {
        sysUserService.update(entity);
        return Result.success();
    }

    @Operation(summary = "启用/禁用账号")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                     @RequestParam Integer status) {
        sysUserService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "删除后台用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.delete(id);
        return Result.success();
    }

    @Operation(summary = "重置密码为 123456")
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        sysUserService.resetPassword(id);
        return Result.success();
    }
}
