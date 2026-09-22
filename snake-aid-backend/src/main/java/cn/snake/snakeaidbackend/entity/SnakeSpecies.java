package cn.snake.snakeaidbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 蛇种识别图库（字段对齐 init.sql snake_species 表）
 */
@Data
@TableName("snake_species")
public class SnakeSpecies implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 中文名称 */
    private String name;

    /** 拉丁学名 */
    private String scientificName;

    /** 是否有毒：0-无毒, 1-有毒 */
    private Integer isVenomous;

    /** 毒性类型：神经毒/血液毒/混合毒/细胞毒 */
    private String venomType;

    /** 头型：三角形/椭圆形/圆形/菱形... */
    private String headShape;

    /** 花纹特征：横纹/纵纹/斑点/斑块/无... */
    private String pattern;

    /** 颜色特征：黑色/棕色/绿色/黄黑相间... */
    private String color;

    /** 尾部特征：尖尾/短粗/渐细/红色尾尖... */
    private String tailShape;

    /** 外观描述（大段文本） */
    private String appearanceDesc;

    /** 分布地区 */
    private String distribution;

    /** 被咬后特异性症状 */
    private String symptoms;

    /** 特异性处理要点 */
    private String firstAidSpecifics;

    /** 危险等级：1-轻微 2-中等 3-剧毒 4-极危 */
    private Integer hazardLevel;

    /** 封面图 URL */
    private String imageUrl;

    /** 上下架状态：0-下架, 1-启用 */
    private Integer status;

    /** 审核状态：0-草稿, 1-待审, 2-已发布, 3-已驳回 */
    private Integer auditStatus;

    /** 审核人ID */
    private Long auditBy;

    /** 审核时间 */
    private LocalDateTime auditTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
