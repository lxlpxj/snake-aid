package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * APP 端普通用户
 */
@Data
@TableName("app_user")
public class AppUser implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 手机号（登录凭证） */
    private String phone;

    /** 昵称 */
    private String nickname;

    /** 头像 */
    private String avatar;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;

    private LocalDateTime createTime;

    /** 总积分（累计不消耗） */
    private Integer points;

    /** 当前等级 1-10 */
    private Integer level;
}
