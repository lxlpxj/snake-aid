package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 急救指南（字段对齐前端 GuidePage.vue）
 */
@Data
@TableName("first_aid_guide")
public class FirstAidGuide implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    /** 1=通用指南 0=蛇种专属 */
    private Integer isGeneric;

    /** 关联蛇种ID（通用指南为NULL） */
    private Long snakeId;

    /** 急救步骤 Markdown */
    private String content;

    /** 教学视频URL */
    private String videoUrl;

    private Integer sortOrder;

    /** 0草稿 1待审 2已发布 3驳回 */
    private Integer auditStatus;

    /** 0下架 1上架 */
    private Integer status;

    private Long auditBy;
    private LocalDateTime auditTime;

    @TableLogic
    private Integer isDeleted;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
