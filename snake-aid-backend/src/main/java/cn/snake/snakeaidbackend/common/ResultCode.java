package cn.snake.snakeaidbackend.common;

import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    // 参数错误 1xxx
    PARAM_ERROR(1001, "参数错误"),
    PARAM_VALID_ERROR(1002, "参数校验失败"),

    // 鉴权错误 2xxx
    UNAUTHORIZED(2001, "未登录或登录已过期"),
    FORBIDDEN(2002, "无权限访问"),
    TOKEN_INVALID(2003, "Token 无效"),
    TOKEN_EXPIRED(2004, "Token 已过期"),

    // 用户错误 3xxx
    USER_NOT_EXIST(3001, "用户不存在"),
    USER_PASSWORD_ERROR(3002, "密码错误"),
    USER_DISABLED(3003, "账号已禁用"),
    PHONE_ALREADY_REGISTERED(3004, "手机号已注册"),

    // 业务错误 4xxx
    DATA_NOT_FOUND(4001, "数据不存在"),
    DATA_ALREADY_EXIST(4002, "数据已存在"),
    CONTENT_NOT_AUDITED(4003, "内容未审核通过，不可见"),
    AUDIT_STATUS_ERROR(4004, "审核状态错误"),

    // 系统错误 5xxx
    SYSTEM_ERROR(5001, "系统内部错误"),
    THIRD_PARTY_ERROR(5002, "第三方服务调用失败");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 兼容旧调用（原 getMsg 改名 getMessage）
     */
    public String getMsg() {
        return message;
    }
}
