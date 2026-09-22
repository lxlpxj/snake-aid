package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 后台操作日志
 *
 * category 分类（决定哪些角色能看见）：
 *   SYSTEM  — 账号管理、角色权限、系统异常   → 仅 SYS_ADMIN 可见
 *   CONTENT — 蛇种/文章/医院/库存 CRUD       → 仅 CONTENT_ADMIN 可见
 *   AUDIT   — 审核通过/驳回、内容下线/编辑   → 仅 MEDICAL_ADVISOR 可见
 *   LOGIN   — 登录/登出                     → 所有角色可见
 */
@Data
@TableName("operation_log")
public class OperationLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 操作人ID */
    private Long userId;

    /** 操作人账号（冗余字段，避免联表查询） */
    private String username;

    /** 日志分类：SYSTEM / CONTENT / AUDIT / LOGIN */
    private String category;

    /** 操作描述 */
    private String operation;

    /** 请求方法（Controller 方法名） */
    private String method;

    /** 请求参数（JSON） */
    private String params;

    /** 操作 IP */
    private String ip;

    /** 操作时间 */
    private LocalDateTime createTime;
}
