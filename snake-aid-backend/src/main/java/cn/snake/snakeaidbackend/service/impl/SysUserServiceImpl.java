package cn.snake.snakeaidbackend.service.impl;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.ResultCode;
import cn.snake.snakeaidbackend.entity.SysUser;
import cn.snake.snakeaidbackend.exception.BusinessException;
import cn.snake.snakeaidbackend.mapper.SysUserMapper;
import cn.snake.snakeaidbackend.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 后台管理用户实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<SysUser> page(long current, long size, String roleType, Integer status) {
        Page<SysUser> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(roleType)) wrapper.eq(SysUser::getRoleType, roleType);
        if (status != null) wrapper.eq(SysUser::getStatus, status);
        wrapper.orderByDesc(SysUser::getCreateTime);
        IPage<SysUser> result = sysUserMapper.selectPage(page, wrapper);
        return PageResult.of(result);
    }

    @Override
    public Long create(SysUser entity) {
        if (StringUtils.hasText(entity.getUsername())) {
            Long exist = sysUserMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, entity.getUsername())
            );
            if (exist != null && exist > 0) {
                throw new BusinessException(ResultCode.DATA_ALREADY_EXIST.getCode(), "账号已存在");
            }
        }
        // 密码 BCrypt 加密
        if (StringUtils.hasText(entity.getPassword())) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        }
        entity.setCreateTime(LocalDateTime.now());
        sysUserMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public void update(SysUser entity) {
        SysUser old = sysUserMapper.selectById(entity.getId());
        if (old == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        // 如果传了新密码则重新加密
        if (StringUtils.hasText(entity.getPassword()) && !entity.getPassword().equals(old.getPassword())) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        }
        sysUserMapper.updateById(entity);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysUser old = sysUserMapper.selectById(id);
        if (old == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        SysUser patch = new SysUser();
        patch.setId(id);
        patch.setStatus(status);
        sysUserMapper.updateById(patch);
    }

    @Override
    public void delete(Long id) {
        SysUser old = sysUserMapper.selectById(id);
        if (old == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        if ("SYS_ADMIN".equals(old.getRoleType()) && sysUserMapper.selectCount(null) == 1) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "至少保留一个系统管理员账号");
        }
        sysUserMapper.deleteById(id); // 逻辑删除
    }

    @Override
    public void resetPassword(Long id) {
        SysUser old = sysUserMapper.selectById(id);
        if (old == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        SysUser patch = new SysUser();
        patch.setId(id);
        patch.setPassword(passwordEncoder.encode("123456"));
        sysUserMapper.updateById(patch);
    }

    @Override
    public SysUser getById(Long id) {
        return sysUserMapper.selectById(id);
    }
}
