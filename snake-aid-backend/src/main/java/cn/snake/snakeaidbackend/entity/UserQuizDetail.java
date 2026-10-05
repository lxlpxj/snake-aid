package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("user_quiz_detail")
public class UserQuizDetail implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long quizId;
    private Long questionId;
    private String userAnswer;
    private String correctAnswer;
    private Integer isCorrect;
    private Integer score;
}
