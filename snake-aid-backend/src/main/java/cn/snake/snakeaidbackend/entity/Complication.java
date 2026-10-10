package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 蛇伤并发症 / 院内处理流程
 */
@Data
@TableName("complication")
public class Complication implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 并发症/流程名称：如"DIC 处理流程" */
    private String title;

    /** 分类：hemotoxic / neurotoxic / cytotoxic / general */
    private String category;

    /** 触发条件/识别要点 */
    private String triggers;

    /** 临床表现 */
    private String symptoms;

    /** 诊断要点 */
    private String diagnosis;

    /** 处理步骤（Markdown，有序列表） */
    private String treatmentSteps;

    /** 支持治疗内容 */
    private String supportiveCare;

    /** 转诊/会诊建议 */
    private String consultation;

    /** 参考依据 */
    private String reference;

    /** 审核状态：0=草稿(CONTENT_ADMIN写) / 2=已发布(MEDICAL_ADVISOR审) */
    private Integer auditStatus;

    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
