package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台-数据概览（Dashboard 用）
 * 仅 SYS_ADMIN 可访问（SecurityConfig 中配置）
 */
@RestController
@RequestMapping("/admin/statistics")
@Tag(name = "后台-数据概览（仅 SYS_ADMIN）")
public class StatisticsAdminController {

    @Resource private SnakeSpeciesMapper snakeMapper;
    @Resource private HospitalMapper hospitalMapper;
    @Resource private ArticleMapper articleMapper;
    @Resource private SysUserMapper sysUserMapper;
    @Resource private AccessStatisticsMapper accessStatsMapper;

    @Operation(summary = "概览统计：蛇种/医院/文章/用户总数")
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("snakeCount", snakeMapper.selectCount(null));
        stats.put("hospitalCount", hospitalMapper.selectCount(null));
        stats.put("articleCount", articleMapper.selectCount(null));
        stats.put("userCount", sysUserMapper.selectCount(null));
        stats.put("accessToday", 0); // 后续实现，access_statistics 目前无数据
        return Result.success(stats);
    }
}
