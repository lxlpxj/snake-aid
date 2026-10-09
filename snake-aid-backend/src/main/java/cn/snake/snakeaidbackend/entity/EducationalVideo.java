package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 科普视频 / 急救教学视频
 */
@Data
@TableName("educational_video")
public class EducationalVideo implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 视频标题 */
    private String title;

    /** 分类：急救操作 / 蛇种识别 / 案例讲解 / 其他 */
    private String category;

    /** 视频简介 */
    private String summary;

    /** 视频 URL（/uploads/xxx.mp4 形式） */
    private String videoUrl;

    /** 封面图 URL（可选） */
    private String coverUrl;

    /** 视频时长（秒） */
    private Integer duration;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 作者/主讲人（可选） */
    private String author;

    /** 排序权重，越大越靠前 */
    private Integer sort;

    /** 审核状态：0-草稿, 1-待审, 2-已发布, 3-已驳回 */
    private Integer auditStatus;

    /** 展示状态：0-下架, 1-上架 */
    private Integer status;

    /** 播放次数 */
    private Integer playCount;

    private Long auditBy;

    private LocalDateTime auditTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
