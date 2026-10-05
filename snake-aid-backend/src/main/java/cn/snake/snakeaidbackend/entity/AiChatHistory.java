package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/** AI 对话历史 */
@Data
@TableName("ai_chat_history")
public class AiChatHistory implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    /** user / assistant */
    private String role;
    private String content;
    private String intent;
    private LocalDateTime createTime;
}
