package cn.snake.snakeaidbackend.service.impl;

import cn.snake.snakeaidbackend.entity.SnakeImage;
import cn.snake.snakeaidbackend.mapper.SnakeImageMapper;
import cn.snake.snakeaidbackend.service.SnakeImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 蛇种图片实现（简单 CRUD）
 */
@Service
public class SnakeImageServiceImpl extends ServiceImpl<SnakeImageMapper, SnakeImage> implements SnakeImageService {
}
