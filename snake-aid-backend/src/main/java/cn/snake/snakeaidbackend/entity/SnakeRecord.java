package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 蛇伤病历（APP 用户创建，后端按 userId 隔离）
 */
@Data
@TableName("snake_record")
public class SnakeRecord implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联 app_user.id（后端自动填） */
    private Long userId;

    /** 伤者性别 */
    private String gender;

    /** 伤者年龄 */
    private Integer age;

    /** 咬伤时间 */
    private LocalDateTime biteTime;

    /** 咬伤地点（省/市） */
    private String bitePlace;

    /** 蛇种（关联 snake_species.id，可为空） */
    private Long snakeId;

    /** 蛇种名称（冗余字段） */
    private String snakeName;

    /** 蛇毒类型 */
    private String venomType;

    /** 咬伤部位 */
    private String location;

    /** 局部症状（text） */
    private String localSymptoms;

    /** 全身症状（text） */
    private String systemicSymptoms;

    /** 评估严重程度：mild/moderate/severe/critical */
    private String severity;

    /** 评估分数 */
    private Integer severityScore;

    /** 是否使用血清 */
    private Integer serumUsed;

    /** 血清名称 + 用量 */
    private String serumDetail;

    /** 治疗方案（text） */
    private String treatment;

    /** 住院医院 */
    private String hospital;

    /** 备注 */
    private String remark;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
