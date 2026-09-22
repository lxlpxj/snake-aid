package cn.snake.snakeaidbackend.service;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.entity.Article;

/**
 * 科普文章服务
 */
public interface ArticleService {

    /**
     * APP 端分页（只已发布）
     *
     * @param current 当前页
     * @param size    每页
     * @param keyword 标题关键字
     * @return PageResult
     */
    PageResult<Article> pageForApp(long current, long size, String keyword);

    /**
     * 管理端分页（含全部状态）
     *
     * @param current 当前页
     * @param size    每页
     * @param status  状态
     * @param keyword 关键字
     * @return PageResult
     */
    PageResult<Article> pageForAdmin(long current, long size, Integer auditStatus, String keyword);

    /**
     * 详情
     *
     * @param id 文章 ID
     * @return Article
     */
    Article getDetail(Long id);

    /**
     * 新增
     *
     * @param article 文章
     * @return ID
     */
    Long create(Article article);

    /**
     * 更新
     *
     * @param article 文章
     */
    void update(Article article);

    /**
     * 删除（下线）
     *
     * @param id 文章 ID
     */
    void delete(Long id);

    /**
     * 提交审核
     *
     * @param id 文章 ID
     */
    void submitAudit(Long id);

    /**
     * 审核
     *
     * @param id      文章 ID
     * @param pass    是否通过
     * @param auditBy 审核人
     */
    void audit(Long id, boolean pass, Long auditBy);
}
