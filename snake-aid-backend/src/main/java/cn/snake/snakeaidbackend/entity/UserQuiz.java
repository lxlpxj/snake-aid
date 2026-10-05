package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("user_quiz")
public class UserQuiz implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String userName;
    /** 试卷总分 */
    private Integer totalScore;
    /** 实际得分 */
    private Integer score;
    /** 答对题数 */
    private Integer correctCount;
    /** 题目总数 */
    private Integer totalCount;
    private String category;
    /** 用时（秒） */
    private Integer durationSec;
    private LocalDateTime createTime;
}
