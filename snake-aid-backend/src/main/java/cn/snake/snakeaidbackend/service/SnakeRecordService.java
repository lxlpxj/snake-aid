package cn.snake.snakeaidbackend.service;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.entity.SnakeRecord;
import cn.snake.snakeaidbackend.mapper.SnakeRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SnakeRecordService {

    @Resource
    private SnakeRecordMapper mapper;

    /** Admin 端：查全部病历 */
    public PageResult<SnakeRecord> page(long current, long size, String severity, String keyword) {
        Page<SnakeRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<SnakeRecord> w = new LambdaQueryWrapper<>();
        if (severity != null && !severity.isEmpty()) w.eq(SnakeRecord::getSeverity, severity);
        if (keyword != null && !keyword.isEmpty())
            w.and(q -> q.like(SnakeRecord::getSnakeName, keyword).or().like(SnakeRecord::getHospital, keyword));
        w.orderByDesc(SnakeRecord::getCreateTime);
        return PageResult.of(mapper.selectPage(page, w));
    }

    /** Admin 端：单条（不限 userId） */
    public SnakeRecord get(Long id) {
        return mapper.selectById(id);
    }

    /** App 端：列表（只看自己的） */
    public List<SnakeRecord> listByUserId(Long userId) {
        LambdaQueryWrapper<SnakeRecord> w = new LambdaQueryWrapper<>();
        w.eq(SnakeRecord::getUserId, userId).orderByDesc(SnakeRecord::getCreateTime);
        return mapper.selectList(w);
    }

    /** App 端：详情（校验归属） */
    public SnakeRecord getMine(Long id, Long userId) {
        LambdaQueryWrapper<SnakeRecord> w = new LambdaQueryWrapper<>();
        w.eq(SnakeRecord::getId, id).eq(SnakeRecord::getUserId, userId);
        return mapper.selectOne(w);
    }

    /** App 端：保存（自动填 userId + 时间） */
    public SnakeRecord saveMine(SnakeRecord r, Long userId) {
        r.setUserId(userId);
        if (r.getId() == null) {
            r.setCreateTime(LocalDateTime.now());
            mapper.insert(r);
        } else {
            r.setUpdateTime(LocalDateTime.now());
            mapper.updateById(r);
        }
        return r;
    }

    /** App 端：删除自己的（校验归属） */
    public boolean deleteMine(Long id, Long userId) {
        LambdaQueryWrapper<SnakeRecord> w = new LambdaQueryWrapper<>();
        w.eq(SnakeRecord::getId, id).eq(SnakeRecord::getUserId, userId);
        return mapper.delete(w) > 0;
    }
}
