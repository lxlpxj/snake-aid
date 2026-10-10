package cn.snake.snakeaidbackend.controller.app;

import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.Complication;
import cn.snake.snakeaidbackend.service.ComplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/complication")
@Tag(name = "APP-并发症处理流程")
public class ComplicationAppController {

    @Resource
    private ComplicationService service;

    @Operation(summary = "列表（已发布）")
    @GetMapping("/list")
    public Result<List<Complication>> list(@RequestParam(required = false) String category) {
        return Result.success(service.listPublished(category));
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    public Result<Complication> get(@PathVariable Long id) {
        return Result.success(service.get(id));
    }
}
