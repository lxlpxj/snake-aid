package cn.snake.snakeaidbackend.service.impl;

import cn.snake.snakeaidbackend.common.AuditStatus;
import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.ResultCode;
import cn.snake.snakeaidbackend.entity.SnakeSpecies;
import cn.snake.snakeaidbackend.exception.BusinessException;
import cn.snake.snakeaidbackend.mapper.SnakeSpeciesMapper;
import cn.snake.snakeaidbackend.service.SnakeSpeciesService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 蛇种图库实现（Entity 已对齐前端 SnakePage.vue）
 *
 * status      → 0-下架 1-启用（上下架开关）
 * auditStatus → 0-草稿 1-待审 2-已发布 3-已驳回（审核工作流）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SnakeSpeciesServiceImpl implements SnakeSpeciesService {

    private final SnakeSpeciesMapper snakeSpeciesMapper;

    @Override
    public PageResult<SnakeSpecies> pageForApp(long current, long size, String province, String keyword, Integer isVenomous) {
        Page<SnakeSpecies> page = new Page<>(current, size);
        LambdaQueryWrapper<SnakeSpecies> wrapper = new LambdaQueryWrapper<>();
        // APP 端只看 已发布 + 上架
        wrapper.eq(SnakeSpecies::getAuditStatus, 2);
        wrapper.eq(SnakeSpecies::getStatus, 1);

        if (StringUtils.hasText(province)) {
            wrapper.like(SnakeSpecies::getDistribution, province);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SnakeSpecies::getName, keyword)
                    .or().like(SnakeSpecies::getScientificName, keyword));
        }
        if (isVenomous != null) {
            wrapper.eq(SnakeSpecies::getIsVenomous, isVenomous);
        }
        wrapper.orderByDesc(SnakeSpecies::getCreateTime);
        return PageResult.of(snakeSpeciesMapper.selectPage(page, wrapper));
    }

    @Override
    public SnakeSpecies getDetailForApp(Long id) {
        SnakeSpecies entity = snakeSpeciesMapper.selectById(id);
        if (entity == null || entity.getAuditStatus() == null || entity.getAuditStatus() != 2
                || entity.getStatus() == null || entity.getStatus() != 1) {
            throw new BusinessException(ResultCode.CONTENT_NOT_AUDITED);
        }
        return entity;
    }

    @Override
    public PageResult<SnakeSpecies> pageForAdmin(long current, long size, Integer auditStatus, Integer isVenomous, String keyword) {
        Page<SnakeSpecies> page = new Page<>(current, size);
        LambdaQueryWrapper<SnakeSpecies> wrapper = new LambdaQueryWrapper<>();
        if (auditStatus != null) {
            wrapper.eq(SnakeSpecies::getAuditStatus, auditStatus);
        }
        if (isVenomous != null) {
            wrapper.eq(SnakeSpecies::getIsVenomous, isVenomous);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SnakeSpecies::getName, keyword)
                    .or().like(SnakeSpecies::getScientificName, keyword));
        }
        wrapper.orderByDesc(SnakeSpecies::getCreateTime);
        return PageResult.of(snakeSpeciesMapper.selectPage(page, wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SnakeSpecies entity, List<String> imageUrls, List<Integer> isCovers) {
        // 新增默认：草稿态 + 启用
        if (entity.getAuditStatus() == null) entity.setAuditStatus(0);
        if (entity.getStatus() == null) entity.setStatus(1);
        entity.setCreateTime(LocalDateTime.now());
        snakeSpeciesMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SnakeSpecies entity, List<String> imageUrls, List<Integer> isCovers) {
        SnakeSpecies old = snakeSpeciesMapper.selectById(entity.getId());
        if (old == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        entity.setUpdateTime(LocalDateTime.now());
        snakeSpeciesMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        SnakeSpecies entity = snakeSpeciesMapper.selectById(id);
        if (entity == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        // 已发布 → 走下线而非物理删除，保证历史可追溯
        if (entity.getAuditStatus() != null && entity.getAuditStatus() == AuditStatus.PUBLISHED.getCode()) {
            entity.setAuditStatus(AuditStatus.OFFLINE.getCode());
            entity.setStatus(0);
            entity.setUpdateTime(LocalDateTime.now());
            snakeSpeciesMapper.updateById(entity);
            return;
        }
        snakeSpeciesMapper.deleteById(id); // 逻辑删除（@TableLogic）
    }

    @Override
    public void submitAudit(Long id) {
        SnakeSpecies entity = snakeSpeciesMapper.selectById(id);
        if (entity == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        if (entity.getAuditStatus() != 0 && entity.getAuditStatus() != 3) {
            throw new BusinessException(ResultCode.AUDIT_STATUS_ERROR);
        }
        entity.setAuditStatus(1); // 待审
        snakeSpeciesMapper.updateById(entity);
    }

    @Override
    public void audit(Long id, boolean pass, Long auditBy) {
        SnakeSpecies entity = snakeSpeciesMapper.selectById(id);
        if (entity == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        if (entity.getAuditStatus() != 1) {
            throw new BusinessException(ResultCode.AUDIT_STATUS_ERROR);
        }
        entity.setAuditStatus(pass ? 2 : 3); // 已发布 or 已驳回
        entity.setAuditBy(auditBy);
        entity.setAuditTime(LocalDateTime.now());
        snakeSpeciesMapper.updateById(entity);
    }

    @Override
    public List<SnakeSpecies> findByFeatures(String headShape, String pattern, String color, String tailShape) {
        LambdaQueryWrapper<SnakeSpecies> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SnakeSpecies::getAuditStatus, 2);
        wrapper.eq(SnakeSpecies::getStatus, 1);
        // 独立特征字段：每命中一个加分，先粗筛再按匹配度排序
        if (StringUtils.hasText(headShape)) wrapper.like(SnakeSpecies::getHeadShape, headShape);
        if (StringUtils.hasText(pattern))   wrapper.like(SnakeSpecies::getPattern, pattern);
        if (StringUtils.hasText(color))     wrapper.like(SnakeSpecies::getColor, color);
        if (StringUtils.hasText(tailShape)) wrapper.like(SnakeSpecies::getTailShape, tailShape);
        return snakeSpeciesMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> recognizeByFeatures(String headShape, String pattern, String color, String tailShape) {
        Map<String, Object> result = new HashMap<>();

        // 1. 先尝试特征匹配
        List<SnakeSpecies> matched = findByFeatures(headShape, pattern, color, tailShape);

        if (!matched.isEmpty()) {
            result.put("matched", true);
            result.put("message", "已根据特征匹配到疑似蛇种，请结合现场实际判断");
            result.put("snakes", matched);
            return result;
        }

        // 2. 匹配不到 → 兜底：按"最危险蛇种处理"
        LambdaQueryWrapper<SnakeSpecies> dangerWrapper = new LambdaQueryWrapper<>();
        dangerWrapper.eq(SnakeSpecies::getAuditStatus, 2);
        dangerWrapper.eq(SnakeSpecies::getStatus, 1);
        dangerWrapper.eq(SnakeSpecies::getIsVenomous, 1);
        // 危险等级倒序，取最高危的 3 种
        dangerWrapper.orderByDesc(SnakeSpecies::getHazardLevel);
        dangerWrapper.last("LIMIT 3");
        List<SnakeSpecies> topDanger = snakeSpeciesMapper.selectList(dangerWrapper);

        result.put("matched", false);
        result.put("message", "⚠ 无法准确识别蛇种，建议按最危险蛇种情况处理，立即拨打 120 并前往医院！");
        result.put("snakes", topDanger);
        result.put("fallback", true);
        return result;
    }
}
