package cn.snake.snakeaidbackend.service.impl;

import cn.snake.snakeaidbackend.entity.SerumStock;
import cn.snake.snakeaidbackend.mapper.SerumStockMapper;
import cn.snake.snakeaidbackend.service.SerumStockService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 血清库存实现（简单 CRUD）
 */
@Service
public class SerumStockServiceImpl extends ServiceImpl<SerumStockMapper, SerumStock> implements SerumStockService {
}
