package cn.snake.snakeaidbackend.controller.app;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.FirstAidGuide;
import cn.snake.snakeaidbackend.service.AccessStatisticsService;
import cn.snake.snakeaidbackend.service.FirstAidGuideService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/app/emergency")
@Tag(name = "APP-紧急自救")
public class EmergencyAppController {

    @Resource
    private FirstAidGuideService guideService;

    @Resource
    private AccessStatisticsService statisticsService;

    @Operation(summary = "获取通用急救指南")
    @GetMapping("/guide")
    public Result<FirstAidGuide> guide() {
        PageResult<FirstAidGuide> page = guideService.pageForAdmin(1, 50, 1);
        FirstAidGuide generic = null;
        for (FirstAidGuide g : page.getRecords()) {
            if (g.getIsGeneric() != null && g.getIsGeneric() == 1) { generic = g; break; }
        }
        if (generic == null && !page.getRecords().isEmpty()) generic = page.getRecords().get(0);
        return Result.success(generic);
    }

    @Operation(summary = "按蛇种取急救指南")
    @GetMapping("/guide/{snakeId}")
    public Result<FirstAidGuide> guideBySnake(@PathVariable Long snakeId) {
        PageResult<FirstAidGuide> page = guideService.pageForAdmin(1, 50, 1);
        FirstAidGuide match = null;
        for (FirstAidGuide g : page.getRecords()) {
            if (g.getSnakeId() != null && g.getSnakeId().equals(snakeId)) { match = g; break; }
        }
        if (match == null) return Result.success(null);
        return Result.success(match);
    }

    @Operation(summary = "触发紧急模式")
    @PostMapping("/trigger")
    public Result<Void> trigger(@RequestBody(required = false) Map<String, String> body) {
        String province = body == null ? null : body.get("province");
        String city = body == null ? null : body.get("city");
        statisticsService.increment("TRIGGER_EMERGENCY", province, city);
        return Result.success();
    }
}
