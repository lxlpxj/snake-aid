package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 等级配置表（管理员可改）
 */
@Data
@TableName("level_config")
public class LevelConfig implements Serializable {

    /** 等级 1-10 */
    @TableId
    private Integer level;

    /** 升到这级需要累积积分 */
    private Integer requiredPoints;

    /** 称号 */
    private String title;

    /** 图标 emoji */
    private String icon;
}
