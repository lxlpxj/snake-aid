package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 地区字典
 */
@Data
@TableName("region")
public class Region implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父级ID */
    private Long parentId;

    /** 地区名称 */
    private String name;

    /** 层级：1-省, 2-市 */
    private Integer level;
}
