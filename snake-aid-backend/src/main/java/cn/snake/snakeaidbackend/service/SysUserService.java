package cn.snake.snakeaidbackend.service;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.entity.SysUser;

/**
 * 后台管理用户服务
 */
public interface SysUserService {

    /**
     * 后台用户分页
     *
     * @param current  页码（从1开始）
     * @param size     每页大小
     * @param roleType 角色字符串：SYS_ADMIN / CONTENT_ADMIN / MEDICAL_ADVISOR（可为 null）
     * @param status   状态：0-禁用 1-正常（可为 null）
     */
    PageResult<SysUser> page(long current, long size, String roleType, Integer status);

    Long create(SysUser entity);

    void update(SysUser entity);

    void updateStatus(Long id, Integer status);

    void delete(Long id);

    void resetPassword(Long id);

    SysUser getById(Long id);
}
