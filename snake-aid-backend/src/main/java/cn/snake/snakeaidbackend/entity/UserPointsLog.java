package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户积分流水
 */
@Data
@TableName("user_points_log")
public class UserPointsLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 事件类型: quiz_submit, snake_view, guide_read, article_read, hospital_view, register_bonus */
    private String eventType;

    /** 加/减多少（正数加，负数减） */
    private Integer points;

    /** 关联 ID（snake_species.id / article.id / user_quiz.id 等） */
    private Long refId;

    private String description;

    private LocalDateTime createTime;
}
