package cn.snake.snakeaidbackend.service.impl;

import cn.snake.snakeaidbackend.entity.Region;
import cn.snake.snakeaidbackend.mapper.RegionMapper;
import cn.snake.snakeaidbackend.service.RegionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 地区字典实现
 */
@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {

    @Override
    public List<Region> listAll() {
        return baseMapper.selectList(
                new LambdaQueryWrapper<Region>().orderByAsc(Region::getLevel).orderByAsc(Region::getId)
        );
    }
}
