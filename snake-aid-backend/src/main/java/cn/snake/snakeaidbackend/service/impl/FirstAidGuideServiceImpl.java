package cn.snake.snakeaidbackend.service.impl;

import cn.snake.snakeaidbackend.common.AuditStatus;
import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.ResultCode;
import cn.snake.snakeaidbackend.entity.FirstAidGuide;
import cn.snake.snakeaidbackend.exception.BusinessException;
import cn.snake.snakeaidbackend.mapper.FirstAidGuideMapper;
import cn.snake.snakeaidbackend.service.FirstAidGuideService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FirstAidGuideServiceImpl implements FirstAidGuideService {

    private final FirstAidGuideMapper guideMapper;

    @Override
    public PageResult<FirstAidGuide> pageForAdmin(long current, long size, Integer status) {
        Page<FirstAidGuide> page = new Page<>(current, size);
        LambdaQueryWrapper<FirstAidGuide> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(FirstAidGuide::getStatus, status);
        wrapper.orderByDesc(FirstAidGuide::getCreateTime);
        IPage<FirstAidGuide> result = guideMapper.selectPage(page, wrapper);
        return PageResult.of(result);
    }

    @Override
    public FirstAidGuide createSimple(FirstAidGuide guide) {
        if (guide.getAuditStatus() == null) guide.setAuditStatus(AuditStatus.DRAFT.getCode());
        if (guide.getStatus() == null) guide.setStatus(1);
        if (guide.getSortOrder() == null) guide.setSortOrder(0);
        if (guide.getIsGeneric() == null) guide.setIsGeneric(0);
        guide.setId(null);
        guide.setCreateTime(LocalDateTime.now());
        guideMapper.insert(guide);
        return guide;
    }

    @Override
    public FirstAidGuide updateSimple(FirstAidGuide guide) {
        if (guide.getId() == null) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "缺少 id");
        FirstAidGuide old = guideMapper.selectById(guide.getId());
        if (old == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        guide.setUpdateTime(LocalDateTime.now());
        guideMapper.updateById(guide);
        return guideMapper.selectById(guide.getId());
    }

    @Override
    public void delete(Long id) {
        FirstAidGuide g = guideMapper.selectById(id);
        if (g == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        // 已发布 → 走下线而非物理删除，保证历史可追溯
        if (AuditStatus.PUBLISHED.getCode().equals(g.getAuditStatus())) {
            g.setAuditStatus(AuditStatus.OFFLINE.getCode());
            g.setStatus(0);
            g.setUpdateTime(LocalDateTime.now());
            guideMapper.updateById(g);
            return;
        }
        guideMapper.deleteById(id);
    }

    @Override
    public void submitAudit(Long id) {
        FirstAidGuide g = guideMapper.selectById(id);
        if (g == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        g.setAuditStatus(AuditStatus.PENDING.getCode());
        guideMapper.updateById(g);
    }

    @Override
    public void audit(Long id, boolean pass, Long auditBy) {
        FirstAidGuide g = guideMapper.selectById(id);
        if (g == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        g.setAuditStatus(pass ? AuditStatus.PUBLISHED.getCode() : 3);
        g.setAuditBy(auditBy);
        g.setAuditTime(LocalDateTime.now());
        guideMapper.updateById(g);
    }
}
