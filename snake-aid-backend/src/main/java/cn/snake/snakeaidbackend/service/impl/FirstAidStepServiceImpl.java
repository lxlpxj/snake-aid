package cn.snake.snakeaidbackend.service.impl;

import cn.snake.snakeaidbackend.entity.FirstAidStep;
import cn.snake.snakeaidbackend.mapper.FirstAidStepMapper;
import cn.snake.snakeaidbackend.service.FirstAidStepService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 自救步骤实现（简单 CRUD）
 */
@Service
public class FirstAidStepServiceImpl extends ServiceImpl<FirstAidStepMapper, FirstAidStep> implements FirstAidStepService {
}
