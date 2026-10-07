package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("first_aid_step")
public class FirstAidStep implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer stepOrder;
    private String title;
    private String emoji;
    private String hint;
    private String decisionPoint;
    private String interactionType;
    private String correctAnswer;
    private String wrongAnswer;
    private String feedbackRight;
    private String feedbackWrong;
    private String passCondition;
    private Integer score;
    private Integer sort;
    private Integer enabled;
}
