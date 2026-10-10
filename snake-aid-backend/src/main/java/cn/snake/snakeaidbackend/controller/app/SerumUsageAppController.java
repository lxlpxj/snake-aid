package cn.snake.snakeaidbackend.controller.app;

import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.SerumUsage;
import cn.snake.snakeaidbackend.service.SerumUsageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/serum-usage")
@Tag(name = "APP-血清使用规范")
public class SerumUsageAppController {

    @Resource
    private SerumUsageService service;

    @Operation(summary = "血清规范列表（已发布）")
    @GetMapping("/list")
    public Result<List<SerumUsage>> list(
            @RequestParam(required = false) Long snakeId,
            @RequestParam(required = false) String venomType) {
        return Result.success(service.listPublished(snakeId, venomType));
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    public Result<SerumUsage> get(@PathVariable Long id) {
        return Result.success(service.get(id));
    }
}
