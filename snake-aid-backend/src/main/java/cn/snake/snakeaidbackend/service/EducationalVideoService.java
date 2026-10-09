package cn.snake.snakeaidbackend.service;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.entity.EducationalVideo;
import cn.snake.snakeaidbackend.mapper.EducationalVideoMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EducationalVideoService {

    @Resource
    private EducationalVideoMapper mapper;

    /** 后台分页查询 */
    public PageResult<EducationalVideo> page(long current, long size, String category, Integer status, String keyword) {
        Page<EducationalVideo> page = new Page<>(current, size);
        LambdaQueryWrapper<EducationalVideo> w = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) w.eq(EducationalVideo::getCategory, category);
        if (status != null) w.eq(EducationalVideo::getStatus, status);
        if (keyword != null && !keyword.isEmpty())
            w.like(EducationalVideo::getTitle, keyword).or().like(EducationalVideo::getSummary, keyword);
        // sort 升序：数字小的排前面（1 排第 1，2 排第 2...）；相同 sort 按 id 升序
        w.orderByAsc(EducationalVideo::getSort).orderByAsc(EducationalVideo::getId);
        return PageResult.of(mapper.selectPage(page, w));
    }

    /** 详情 */
    public EducationalVideo get(Long id) {
        return mapper.selectById(id);
    }

    /** 新增/修改 */
    public EducationalVideo save(EducationalVideo v) {
        if (v.getSort() == null) v.setSort(0);
        if (v.getPlayCount() == null) v.setPlayCount(0);
        if (v.getStatus() == null) v.setStatus(1);
        if (v.getAuditStatus() == null) v.setAuditStatus(2);
        if (v.getId() == null) {
            v.setCreateTime(LocalDateTime.now());
            mapper.insert(v);
        } else {
            mapper.updateById(v);
        }
        return v;
    }

    /** 删除（逻辑） */
    public boolean delete(Long id) {
        return mapper.deleteById(id) > 0;
    }

    /** 启/下架 */
    public boolean toggleStatus(Long id) {
        EducationalVideo v = mapper.selectById(id);
        if (v == null) return false;
        v.setStatus(v.getStatus() == 1 ? 0 : 1);
        return mapper.updateById(v) > 0;
    }

    /** App端：已发布+已上架列表 */
    public List<EducationalVideo> listPublished(String category) {
        LambdaQueryWrapper<EducationalVideo> w = new LambdaQueryWrapper<>();
        w.eq(EducationalVideo::getStatus, 1);
        w.eq(EducationalVideo::getAuditStatus, 2); // 已发布
        if (category != null && !category.isEmpty()) w.eq(EducationalVideo::getCategory, category);
        // sort 升序：数字小的排前面（1 排第 1，2 排第 2...）；相同 sort 按 id 升序
        w.orderByAsc(EducationalVideo::getSort).orderByAsc(EducationalVideo::getId);
        return mapper.selectList(w);
    }

    /** 播放次数+1 */
    public void incPlayCount(Long id) {
        EducationalVideo v = mapper.selectById(id);
        if (v != null) {
            v.setPlayCount((v.getPlayCount() == null ? 0 : v.getPlayCount()) + 1);
            mapper.updateById(v);
        }
    }
}
