package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 自救步骤详情
 */
@Data
@TableName("first_aid_step")
public class FirstAidStep implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联指南ID */
    private Long guideId;

    /** 步骤顺序 */
    private Integer stepOrder;

    /** 步骤标题 */
    private String title;

    /** 步骤详细说明 */
    private String content;

    /** 禁止动作警示 */
    private String warning;
}
