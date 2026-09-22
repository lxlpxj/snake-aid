package cn.snake.snakeaidbackend.service.impl;

import cn.snake.snakeaidbackend.common.AuditStatus;
import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.ResultCode;
import cn.snake.snakeaidbackend.entity.Article;
import cn.snake.snakeaidbackend.exception.BusinessException;
import cn.snake.snakeaidbackend.mapper.ArticleMapper;
import cn.snake.snakeaidbackend.service.ArticleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 科普文章实现（字段对齐：status=上下架, auditStatus=审核）
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;

    @Override
    public PageResult<Article> pageForApp(long current, long size, String keyword) {
        Page<Article> page = new Page<>(current, size);
        LambdaQueryWrapper<Article> w = new LambdaQueryWrapper<>();
        // APP 只看：已发布 + 上架
        w.eq(Article::getAuditStatus, AuditStatus.PUBLISHED.getCode());
        w.eq(Article::getStatus, 1);
        if (StringUtils.hasText(keyword)) w.like(Article::getTitle, keyword);
        w.orderByDesc(Article::getCreateTime);
        return PageResult.of(articleMapper.selectPage(page, w));
    }

    @Override
    public PageResult<Article> pageForAdmin(long current, long size, Integer auditStatus, String keyword) {
        Page<Article> page = new Page<>(current, size);
        LambdaQueryWrapper<Article> w = new LambdaQueryWrapper<>();
        if (auditStatus != null) w.eq(Article::getAuditStatus, auditStatus);
        if (StringUtils.hasText(keyword)) w.like(Article::getTitle, keyword);
        w.orderByDesc(Article::getCreateTime);
        return PageResult.of(articleMapper.selectPage(page, w));
    }

    @Override
    public Article getDetail(Long id) {
        Article e = articleMapper.selectById(id);
        if (e == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        return e;
    }

    @Override
    public Long create(Article article) {
        article.setAuditStatus(AuditStatus.DRAFT.getCode());
        if (article.getStatus() == null) article.setStatus(1);
        article.setCreateTime(LocalDateTime.now());
        articleMapper.insert(article);
        return article.getId();
    }

    @Override
    public void update(Article article) {
        Article old = articleMapper.selectById(article.getId());
        if (old == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        // 保留审核态不变
        article.setAuditStatus(old.getAuditStatus());
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.updateById(article);
    }

    @Override
    public void delete(Long id) {
        Article old = articleMapper.selectById(id);
        if (old == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        // 已发布 → 走下线而非物理删除，保证历史可追溯
        if (AuditStatus.PUBLISHED.getCode().equals(old.getAuditStatus())) {
            old.setAuditStatus(AuditStatus.OFFLINE.getCode());
            old.setStatus(0);
            old.setUpdateTime(LocalDateTime.now());
            articleMapper.updateById(old);
            return;
        }
        articleMapper.deleteById(id); // 逻辑删除
    }

    @Override
    public void submitAudit(Long id) {
        Article old = articleMapper.selectById(id);
        if (old == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        Integer cur = old.getAuditStatus();
        if (cur == null || (cur != AuditStatus.DRAFT.getCode() && cur != AuditStatus.OFFLINE.getCode())) {
            throw new BusinessException(ResultCode.AUDIT_STATUS_ERROR);
        }
        old.setAuditStatus(AuditStatus.PENDING.getCode());
        articleMapper.updateById(old);
    }

    @Override
    public void audit(Long id, boolean pass, Long auditBy) {
        Article old = articleMapper.selectById(id);
        if (old == null) throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        if (old.getAuditStatus() == null || !old.getAuditStatus().equals(AuditStatus.PENDING.getCode())) {
            throw new BusinessException(ResultCode.AUDIT_STATUS_ERROR);
        }
        old.setAuditStatus(pass ? AuditStatus.PUBLISHED.getCode() : AuditStatus.OFFLINE.getCode());
        old.setAuditBy(auditBy);
        old.setAuditTime(LocalDateTime.now());
        articleMapper.updateById(old);
    }
}
