package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/** 人工咨询工单 */
@Data
@TableName("consultation_ticket")
public class ConsultationTicket implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String category;
    /** PENDING / IN_PROGRESS / RESOLVED / CLOSED */
    private String status;
    private Long assigneeId;
    private String lastMessage;
    private LocalDateTime lastTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
