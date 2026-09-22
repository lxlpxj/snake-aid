package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 内容版本记录
 */
@Data
@TableName("content_version")
public class ContentVersion implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 版本号 */
    private String versionNumber;

    /** 更新说明 */
    private String updateDesc;

    /** 发布时间 */
    private LocalDateTime releaseTime;

    /** 是否当前最新版本 */
    private Integer isCurrent;
}
