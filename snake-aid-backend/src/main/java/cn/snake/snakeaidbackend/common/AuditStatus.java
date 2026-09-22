package cn.snake.snakeaidbackend.common;

import lombok.Getter;

/**
 * 内容审核状态枚举（蛇种库、自救指南、科普文章通用）
 */
@Getter
public enum AuditStatus {

    DRAFT(0, "草稿"),
    PENDING(1, "待审核"),
    PUBLISHED(2, "已发布"),
    OFFLINE(3, "已下线");

    private final Integer code;
    private final String name;

    AuditStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
