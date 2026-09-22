package cn.snake.snakeaidbackend.service;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.entity.AccessStatistics;

/**
 * 访问统计服务
 */
public interface AccessStatisticsService {

    void increment(String eventType, String province, String city);

    /**
     * 管理端分页（Controller 调用签名：current, size, eventType）
     */
    PageResult<AccessStatistics> page(long current, long size, String eventType);
}
