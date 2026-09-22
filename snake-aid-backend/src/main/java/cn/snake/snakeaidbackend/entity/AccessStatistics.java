package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 访问统计
 */
@Data
@TableName("access_statistics")
public class AccessStatistics implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 事件类型 */
    private String eventType;

    private String province;

    private String city;

    private LocalDate statDate;

    private Integer count;
}
