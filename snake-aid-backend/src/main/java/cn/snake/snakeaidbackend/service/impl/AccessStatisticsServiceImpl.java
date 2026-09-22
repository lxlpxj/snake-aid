package cn.snake.snakeaidbackend.service.impl;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.entity.AccessStatistics;
import cn.snake.snakeaidbackend.mapper.AccessStatisticsMapper;
import cn.snake.snakeaidbackend.service.AccessStatisticsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/**
 * 访问统计实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccessStatisticsServiceImpl implements AccessStatisticsService {

    private final StringRedisTemplate redis;
    private final AccessStatisticsMapper accessStatisticsMapper;

    @Override
    public void increment(String eventType, String province, String city) {
        // TODO: Redis 原子计数 + 定时落库
        String date = LocalDate.now().toString();
        String key = "access:" + date + ":" + eventType + ":"
                + (province == null ? "-" : province) + ":"
                + (city == null ? "-" : city);
        redis.opsForValue().increment(key);
        redis.expire(key, 7, java.util.concurrent.TimeUnit.DAYS);
        log.debug("[ACCESS] key={}", key);
    }

    @Override
    public PageResult<AccessStatistics> page(long current, long size, String eventType) {
        Page<AccessStatistics> page = new Page<>(current, size);
        LambdaQueryWrapper<AccessStatistics> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(eventType)) wrapper.eq(AccessStatistics::getEventType, eventType);
        wrapper.orderByDesc(AccessStatistics::getStatDate);
        IPage<AccessStatistics> result = accessStatisticsMapper.selectPage(page, wrapper);
        return PageResult.of(result);
    }
}
