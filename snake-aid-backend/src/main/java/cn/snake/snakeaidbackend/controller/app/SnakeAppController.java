package cn.snake.snakeaidbackend.controller.app;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.SnakeSpecies;
import cn.snake.snakeaidbackend.service.SnakeSpeciesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/app/snake")
@Tag(name = "APP-蛇种图库")
public class SnakeAppController {

    @Resource
    private SnakeSpeciesService snakeService;

    @Operation(summary = "蛇种列表（已发布）")
    @GetMapping("/page")
    public Result<PageResult<SnakeSpecies>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer isPoisonous) {
        return Result.success(snakeService.pageForApp(current, size, province, keyword, isPoisonous));
    }

    @Operation(summary = "蛇种详情")
    @GetMapping("/{id}")
    public Result<SnakeSpecies> detail(@PathVariable Long id) {
        return Result.success(snakeService.getDetailForApp(id));
    }

    @Operation(summary = "特征检索（头型/花纹/颜色/尾部）")
    @GetMapping("/features")
    public Result<List<SnakeSpecies>> features(
            @RequestParam(required = false) String headShape,
            @RequestParam(required = false) String pattern,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String tailShape) {
        return Result.success(snakeService.findByFeatures(headShape, pattern, color, tailShape));
    }

    @Operation(summary = "蛇种识别（带兜底：无法识别→按最危险蛇种处理）")
    @GetMapping("/recognize")
    public Result<Map<String, Object>> recognize(
            @RequestParam(required = false) String headShape,
            @RequestParam(required = false) String pattern,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String tailShape) {
        return Result.success(snakeService.recognizeByFeatures(headShape, pattern, color, tailShape));
    }
}
