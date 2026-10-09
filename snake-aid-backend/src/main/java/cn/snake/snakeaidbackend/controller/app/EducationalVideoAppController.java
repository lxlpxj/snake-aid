package cn.snake.snakeaidbackend.controller.app;

import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.EducationalVideo;
import cn.snake.snakeaidbackend.service.EducationalVideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** App端 - 科普视频 */
@RestController
@RequestMapping("/app/video")
@Tag(name = "App端-科普视频")
public class EducationalVideoAppController {

    @Resource
    private EducationalVideoService service;

    @Operation(summary = "视频列表（已发布+已上架）")
    @GetMapping("/list")
    public Result<List<EducationalVideo>> list(
            @RequestParam(required = false) String category) {
        return Result.success(service.listPublished(category));
    }

    @Operation(summary = "详情 + 播放次数+1")
    @GetMapping("/{id}")
    public Result<EducationalVideo> get(@PathVariable Long id) {
        service.incPlayCount(id);
        return Result.success(service.get(id));
    }
}
