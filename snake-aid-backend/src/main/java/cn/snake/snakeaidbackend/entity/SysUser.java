package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 后台管理用户
 *
 * roleType 取值见 {@link cn.snake.snakeaidbackend.common.RoleEnum}：
 *   SYS_ADMIN / CONTENT_ADMIN / MEDICAL_ADVISOR
 *
 * APP 普通用户走 app_user 表，不走这里。
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号 */
    private String username;

    /** BCrypt 加密密码 */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 角色：SYS_ADMIN / CONTENT_ADMIN / MEDICAL_ADVISOR */
    private String roleType;

    /** 手机号 */
    private String phone;

    /** 0-禁用 1-正常 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
