package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 科普文章（字段对齐前端 ArticlePage.vue）
 */
@Data
@TableName("article")
public class Article implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    /** 分类（如：预防、急救、科普） */
    private String category;

    /** 摘要 */
    private String summary;

    /** Markdown 正文 */
    private String content;

    /** 封面图 URL（前端叫 coverImage 也兼容） */
    private String coverImage;

    /** 审核状态：0-草稿, 1-待审, 2-已发布, 3-已驳回 */
    private Integer auditStatus;

    /** 展示状态：0-下架, 1-上架 */
    private Integer status;

    private Long auditBy;

    private LocalDateTime auditTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
