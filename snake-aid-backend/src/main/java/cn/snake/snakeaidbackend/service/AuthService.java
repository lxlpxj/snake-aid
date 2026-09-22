package cn.snake.snakeaidbackend.service;

import cn.snake.snakeaidbackend.entity.AppUser;

import java.util.Map;

/**
 * 鉴权服务（后台 + APP）
 */
public interface AuthService {

    /**
     * 后台账号密码登录
     *
     * @param username 账号
     * @param password 密码
     * @return token + 用户信息
     */
    Map<String, Object> login(String username, String password);

    /**
     * APP 端手机号 + 验证码登录
     *
     * @param phone    手机号
     * @param smsCode  短信验证码
     * @return token + 用户信息
     */
    Map<String, Object> appLoginBySms(String phone, String smsCode);

    /**
     * 发送短信验证码（Redis 存）
     *
     * @param phone 手机号
     */
    void sendSmsCode(String phone);

    /**
     * 注册 APP 用户
     *
     * @param phone    手机号
     * @param nickname 昵称
     * @return AppUser
     */
    AppUser registerAppUser(String phone, String nickname);

    /**
     * 登出（Redis 拉黑 token）
     *
     * @param userId 用户 ID
     */
    void logout(Long userId);
}
