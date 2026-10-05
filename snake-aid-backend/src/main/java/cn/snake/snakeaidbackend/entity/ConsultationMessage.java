package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/** 工单对话消息 */
@Data
@TableName("consultation_message")
public class ConsultationMessage implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ticketId;
    /** USER / ADMIN */
    private String senderType;
    private Long senderId;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
}
