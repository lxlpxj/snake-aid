package cn.snake.snakeaidbackend.service;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.entity.Complication;
import cn.snake.snakeaidbackend.mapper.ComplicationMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplicationService {

    @Resource
    private ComplicationMapper mapper;

    public PageResult<Complication> page(long current, long size, String category, String keyword) {
        Page<Complication> page = new Page<>(current, size);
        LambdaQueryWrapper<Complication> w = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) w.eq(Complication::getCategory, category);
        if (keyword != null && !keyword.isEmpty())
            w.and(q -> q.like(Complication::getTitle, keyword).or().like(Complication::getSymptoms, keyword));
        w.orderByDesc(Complication::getId);
        return PageResult.of(mapper.selectPage(page, w));
    }

    public Complication get(Long id) {
        return mapper.selectById(id);
    }

    public Complication save(Complication c) {
        if (c.getAuditStatus() == null) c.setAuditStatus(2);  // 直接发布，不走草稿流程
        if (c.getStatus() == null) c.setStatus(1);
        if (c.getId() == null) {
            c.setCreateTime(LocalDateTime.now());
            mapper.insert(c);
        } else {
            c.setUpdateTime(LocalDateTime.now());
            mapper.updateById(c);
        }
        return c;
    }

    public boolean delete(Long id) {
        return mapper.deleteById(id) > 0;
    }

    public boolean audit(Long id) {
        Complication c = mapper.selectById(id);
        if (c == null) return false;
        c.setAuditStatus(2);
        c.setUpdateTime(LocalDateTime.now());
        return mapper.updateById(c) > 0;
    }

    public List<Complication> listPublished(String category) {
        LambdaQueryWrapper<Complication> w = new LambdaQueryWrapper<>();
        w.eq(Complication::getStatus, 1).eq(Complication::getAuditStatus, 2);
        if (category != null && !category.isEmpty()) w.eq(Complication::getCategory, category);
        w.orderByAsc(Complication::getCategory).orderByAsc(Complication::getId);
        return mapper.selectList(w);
    }
}
