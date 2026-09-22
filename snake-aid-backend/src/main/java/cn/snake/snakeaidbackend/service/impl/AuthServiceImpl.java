package cn.snake.snakeaidbackend.service.impl;

import cn.snake.snakeaidbackend.common.ResultCode;
import cn.snake.snakeaidbackend.entity.AppUser;
import cn.snake.snakeaidbackend.entity.SysUser;
import cn.snake.snakeaidbackend.exception.BusinessException;
import cn.snake.snakeaidbackend.mapper.AppUserMapper;
import cn.snake.snakeaidbackend.mapper.SysUserMapper;
import cn.snake.snakeaidbackend.security.JwtUtil;
import cn.snake.snakeaidbackend.service.AuthService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 鉴权服务实现
 *
 * 两类登录：
 *   - 后台账号登录：username + password（BCrypt）→ role = SYS_ADMIN / CONTENT_ADMIN / MEDICAL_ADVISOR
 *   - APP 端短信登录：phone + smsCode（开发固定 123456）→ role = APP_USER
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final AppUserMapper appUserMapper;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redis;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> login(String username, String password) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
        if (user == null) throw new BusinessException(ResultCode.USER_NOT_EXIST);
        if (user.getStatus() != null && user.getStatus() == 0) throw new BusinessException(ResultCode.USER_DISABLED);
        if (!passwordEncoder.matches(password, user.getPassword())) throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);

        // role 直接传 SysUser.roleType（字符串：SYS_ADMIN / CONTENT_ADMIN / MEDICAL_ADVISOR）
        String token = jwtUtil.generate(user.getId(), user.getUsername(), user.getRoleType());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("role", user.getRoleType());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        return result;
    }

    @Override
    public Map<String, Object> appLoginBySms(String phone, String smsCode) {
        String cacheKey = "sms:code:" + phone;
        String cached = redis.opsForValue().get(cacheKey);
        if (cached == null || !cached.equals(smsCode)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "验证码错误或已过期（开发固定码 123456）");
        }
        redis.delete(cacheKey);

        AppUser user = appUserMapper.selectOne(
                new LambdaQueryWrapper<AppUser>().eq(AppUser::getPhone, phone)
        );
        if (user == null) {
            user = new AppUser();
            user.setPhone(phone);
            user.setNickname("用户" + phone.substring(phone.length() - 4));
            user.setCreateTime(LocalDateTime.now());
            appUserMapper.insert(user);
        }
        user.setLastLoginTime(LocalDateTime.now());
        appUserMapper.updateById(user);

        // APP 普通用户统一角色 = APP_USER
        String token = jwtUtil.generate(user.getId(), user.getPhone(), "APP_USER");

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("phone", user.getPhone());
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        return result;
    }

    @Override
    public void sendSmsCode(String phone) {
        String code = "123456";
        redis.opsForValue().set("sms:code:" + phone, code, 5, TimeUnit.MINUTES);
        log.info("[SMS-MOCK] phone={}, code={}", phone, code);
    }

    @Override
    public AppUser registerAppUser(String phone, String nickname) {
        Long count = appUserMapper.selectCount(
                new LambdaQueryWrapper<AppUser>().eq(AppUser::getPhone, phone)
        );
        if (count != null && count > 0) throw new BusinessException(ResultCode.PHONE_ALREADY_REGISTERED);

        AppUser user = new AppUser();
        user.setPhone(phone);
        user.setNickname(nickname);
        user.setCreateTime(LocalDateTime.now());
        appUserMapper.insert(user);
        return user;
    }

    @Override
    public void logout(Long userId) {
        log.info("[LOGOUT] userId={}", userId);
    }
}
