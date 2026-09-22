package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 医院信息
 */
@Data
@TableName("hospital")
public class Hospital implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 医院名称 */
    private String name;

    /** 详细地址 */
    private String address;

    private String province;

    private String city;

    private BigDecimal longitude;

    private BigDecimal latitude;

    /** 急救电话 */
    private String phone;

    /** 医院等级 */
    private String level;

    /** 是否有血清：0-无, 1-有 */
    private Integer hasSerum;

    /** 距离（km，动态计算，不存数据库） */
    @TableField(exist = false)
    private Double distance;

    @TableLogic
    private Integer isDeleted;

    private LocalDateTime updateTime;
}
