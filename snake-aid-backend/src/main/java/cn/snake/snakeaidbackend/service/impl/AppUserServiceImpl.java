package cn.snake.snakeaidbackend.service.impl;

import cn.snake.snakeaidbackend.entity.AppUser;
import cn.snake.snakeaidbackend.mapper.AppUserMapper;
import cn.snake.snakeaidbackend.service.AppUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * APP 用户实现（简单 CRUD）
 */
@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser> implements AppUserService {
}
