package cn.snake.snakeaidbackend.service;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.entity.SnakeSpecies;

import java.util.List;

/**
 * 蛇种图库服务
 */
public interface SnakeSpeciesService {

    /**
     * APP 端分页（只查已发布）
     *
     * @param current    当前页
     * @param size       每页大小
     * @param province   省份（匹配 distribution 字段）
     * @param keyword    关键字（名称/拉丁名）
     * @param isPoisonous 是否有毒
     * @return PageResult
     */
    PageResult<SnakeSpecies> pageForApp(long current, long size, String province, String keyword, Integer isPoisonous);

    /**
     * APP 端详情（已发布才返回）
     *
     * @param id 蛇种 ID
     * @return SnakeSpecies
     */
    SnakeSpecies getDetailForApp(Long id);

    /**
     * 管理端分页（含草稿/待审/已发布）
     *
     * @param current 当前页
     * @param size    每页大小
     * @param status  状态筛选
     * @param keyword 关键字
     * @return PageResult
     */
    PageResult<SnakeSpecies> pageForAdmin(long current, long size, Integer auditStatus, Integer isVenomous, String keyword);

    /**
     * 新增蛇种 + 图片关联
     *
     * @param entity     蛇种实体
     * @param imageUrls  图片地址列表
     * @param isCovers   是否封面（与 imageUrls 一一对应）
     * @return 新增 ID
     */
    Long create(SnakeSpecies entity, List<String> imageUrls, List<Integer> isCovers);

    /**
     * 更新蛇种 + 图片关联
     *
     * @param entity    蛇种实体
     * @param imageUrls 图片地址列表
     * @param isCovers  是否封面
     */
    void update(SnakeSpecies entity, List<String> imageUrls, List<Integer> isCovers);

    /**
     * 下线（逻辑删除 + 状态置为 OFFLINE）
     *
     * @param id 蛇种 ID
     */
    void delete(Long id);

    /**
     * 提交审核（状态 DRAFT → PENDING）
     *
     * @param id 蛇种 ID
     */
    void submitAudit(Long id);

    /**
     * 审核通过/驳回
     *
     * @param id      蛇种 ID
     * @param pass    true=通过，false=驳回
     * @param auditBy 审核人 ID
     */
    void audit(Long id, boolean pass, Long auditBy);

    /**
     * 根据特征组合检索蛇种（APP 特征识别）
     *
     * @param headShape 头型（可空）
     * @param pattern   花纹（可空）
     * @param color     颜色（可空）
     * @param tailShape 尾部（可空）
     * @return 匹配的蛇种列表
     */
    List<SnakeSpecies> findByFeatures(String headShape, String pattern, String color, String tailShape);

    /**
     * 特征识别（带兜底）：匹配不到时返回 matched=false + 最危险蛇种
     *
     * 返回结构：
     * {
     *   "matched": true/false,
     *   "message": "无法识别蛇种，建议按最危险情况处理并立即就医",
     *   "snakes": [ 匹配到的蛇种列表 或 最危险蛇种列表 ]
     * }
     */
    java.util.Map<String, Object> recognizeByFeatures(String headShape, String pattern, String color, String tailShape);
}
