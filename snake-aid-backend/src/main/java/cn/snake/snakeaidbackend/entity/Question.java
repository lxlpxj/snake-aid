package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("question")
public class Question implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** FIRST_AID急救知识 / SNAKE蛇种识别 / HOSPITAL医院常识 */
    private String category;
    /** 1-单选 2-多选 3-判断 */
    private Integer questionType;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    /** 正确答案 A/B/C/D 或多选 AB */
    private String correctAnswer;
    private Integer score;
    private String analysis;
    /** 0-禁用 1-启用 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
