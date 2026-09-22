package cn.snake.snakeaidbackend.common;

import lombok.Getter;

/**
 * 后台用户角色枚举（与前端 Layout.vue 的 role 字段对齐）
 *
 * 三大角色权限边界：
 *   CONTENT_ADMIN      → 蛇种库 / 医院 / 血清库存 / 科普文章 的增删改
 *   MEDICAL_ADVISOR    → 审核蛇种/文章准确性、维护急救指南
 *   SYS_ADMIN          → 用户权限、系统统计、所有接口
 *
 * APP 普通用户不在这里——他们是 app_user 表的，不需要硬编码角色，
 * 因为需求里的"伤者本人/同行者/学习人群"是使用场景，不是权限分级。
 */
@Getter
public enum RoleEnum {

    /** 系统管理员 */
    SYS_ADMIN("SYS_ADMIN", "系统管理员"),
    /** 内容管理员（运营人员） */
    CONTENT_ADMIN("CONTENT_ADMIN", "内容管理员"),
    /** 医疗顾问（医生/急救专家） */
    MEDICAL_ADVISOR("MEDICAL_ADVISOR", "医疗顾问");

    private final String code;
    private final String name;

    RoleEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RoleEnum of(String code) {
        if (code == null) return null;
        for (RoleEnum e : values()) {
            if (e.code.equals(code)) return e;
        }
        return null;
    }
}
