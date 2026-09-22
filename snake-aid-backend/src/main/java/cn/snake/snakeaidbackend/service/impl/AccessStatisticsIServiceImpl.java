package cn.snake.snakeaidbackend.service.impl;

import cn.snake.snakeaidbackend.entity.AccessStatistics;
import cn.snake.snakeaidbackend.mapper.AccessStatisticsMapper;
import cn.snake.snakeaidbackend.service.AccessStatisticsIService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 访问统计底层 IService 实现（简单 CRUD，供定时任务落库使用）
 */
@Service
public class AccessStatisticsIServiceImpl
        extends ServiceImpl<AccessStatisticsMapper, AccessStatistics>
        implements AccessStatisticsIService {
}
