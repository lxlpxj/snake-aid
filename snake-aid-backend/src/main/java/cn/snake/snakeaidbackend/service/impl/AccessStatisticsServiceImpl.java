package cn.snake.snakeaidbackend.service.impl;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.entity.AccessStatistics;
import cn.snake.snakeaidbackend.mapper.AccessStatisticsMapper;
import cn.snake.snakeaidbackend.service.AccessStatisticsIService;
import cn.snake.snakeaidbackend.service.AccessStatisticsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDate;

@Slf4j
@Service
public class AccessStatisticsServiceImpl
        extends ServiceImpl<AccessStatisticsMapper, AccessStatistics>
        implements AccessStatisticsService, AccessStatisticsIService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void increment(String eventType, String province, String city) {
        LocalDate today = LocalDate.now();
        try {
            jdbcTemplate.update(
                "INSERT INTO access_statistics (event_type, province, city, stat_date, count) " +
                "VALUES (?, ?, ?, ?, 1) " +
                "ON DUPLICATE KEY UPDATE count = count + 1",
                eventType, province, city, today
            );
        } catch (Exception e) {
            log.warn("increment access stat failed: {}", e.getMessage());
        }
    }

    @Override
    public PageResult<AccessStatistics> page(long current, long size, String eventType) {
        LambdaQueryWrapper<AccessStatistics> wrapper = new LambdaQueryWrapper<>();
        if (eventType != null && !eventType.isEmpty()) {
            wrapper.eq(AccessStatistics::getEventType, eventType);
        }
        wrapper.orderByDesc(AccessStatistics::getStatDate);
        Page<AccessStatistics> p = this.page(new Page<>(current, size), wrapper);
        return PageResult.of(p);
    }
}