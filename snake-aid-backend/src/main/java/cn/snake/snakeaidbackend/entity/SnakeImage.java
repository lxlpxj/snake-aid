package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 蛇种图库图片
 */
@Data
@TableName("snake_image")
public class SnakeImage implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联蛇种ID */
    private Long snakeId;

    /** 图片地址 */
    private String imageUrl;

    /** 是否封面：0-否, 1-是 */
    private Integer isCover;
}
