package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 抗蛇毒血清使用规范（由 MEDICAL_ADVISOR 录入维护）
 */
@Data
@TableName("serum_usage")
public class SerumUsage implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 血清名称：如"抗眼镜蛇毒血清" */
    private String serumName;

    /** 对应蛇种（可关联 snake_species.id，可为空表示通用） */
    private Long snakeId;

    /** 适用蛇毒类型：neurotoxic / hemotoxic / cytotoxic / mixed */
    private String venomType;

    /** 常规使用剂量（单位：安瓿/支） */
    private String dosage;

    /** 稀释方式：如"20-50ml 生理盐水稀释" */
    private String dilution;

    /** 给药途径：如"静脉滴注" / "肌注" */
    private String route;

    /** 滴速建议：如"前 30 分钟 10-20 滴/分" */
    private String infusionRate;

    /** 皮试要求：如"必须皮试，方法：..." */
    private String skinTest;

    /** 过敏处理：内容 text */
    private String allergyHandling;

    /** 注意事项 text */
    private String precautions;

    /** 来源依据：如"中国蛇伤急救指南 2023" */
    private String reference;

    /** 审核状态：0=草稿, 2=已发布 */
    private Integer auditStatus;

    /** 展示状态 */
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
