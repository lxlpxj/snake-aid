package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 医院血清库存（字段对齐前端 HospitalPage.vue）
 */
@Data
@TableName("serum_stock")
public class SerumStock implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联医院ID */
    private Long hospitalId;

    /** 血清名称（前端叫 serumName） */
    private String serumName;

    /** 血清类型（旧字段兼容） */
    private String serumType;

    /** 库存数量 */
    private Integer quantity;

    /** 对应蛇种ID */
    private Long snakeId;

    /** 有效期 */
    private LocalDate expireDate;

    private LocalDateTime updateTime;
}
