package cn.snake.snakeaidbackend.service;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.entity.SerumUsage;
import cn.snake.snakeaidbackend.mapper.SerumUsageMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SerumUsageService {

    @Resource
    private SerumUsageMapper mapper;

    public PageResult<SerumUsage> page(long current, long size, String keyword) {
        Page<SerumUsage> page = new Page<>(current, size);
        LambdaQueryWrapper<SerumUsage> w = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            w.and(q -> q.like(SerumUsage::getSerumName, keyword)
                    .or().like(SerumUsage::getDosage, keyword));
        }
        w.orderByDesc(SerumUsage::getId);
        return PageResult.of(mapper.selectPage(page, w));
    }

    public SerumUsage get(Long id) {
        return mapper.selectById(id);
    }

    public SerumUsage save(SerumUsage s) {
        if (s.getAuditStatus() == null) s.setAuditStatus(2);
        if (s.getStatus() == null) s.setStatus(1);
        if (s.getId() == null) {
            s.setCreateTime(LocalDateTime.now());
            mapper.insert(s);
        } else {
            s.setUpdateTime(LocalDateTime.now());
            mapper.updateById(s);
        }
        return s;
    }

    public boolean delete(Long id) {
        return mapper.deleteById(id) > 0;
    }

    /** App端：已发布的血清规范列表（可按蛇种id/蛇毒类型过滤） */
    public List<SerumUsage> listPublished(Long snakeId, String venomType) {
        LambdaQueryWrapper<SerumUsage> w = new LambdaQueryWrapper<>();
        w.eq(SerumUsage::getStatus, 1).eq(SerumUsage::getAuditStatus, 2);
        if (snakeId != null) w.eq(SerumUsage::getSnakeId, snakeId);
        if (venomType != null && !venomType.isEmpty()) w.eq(SerumUsage::getVenomType, venomType);
        w.orderByAsc(SerumUsage::getSnakeId).orderByDesc(SerumUsage::getId);
        return mapper.selectList(w);
    }
}
