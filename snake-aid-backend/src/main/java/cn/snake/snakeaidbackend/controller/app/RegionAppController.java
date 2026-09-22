package cn.snake.snakeaidbackend.controller.app;

import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.Region;
import cn.snake.snakeaidbackend.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/app/region")
@Tag(name = "APP-地区字典")
public class RegionAppController {

    @Resource
    private RegionService regionService;

    @Operation(summary = "省市区列表")
    @GetMapping("/list")
    public Result<List<Region>> list() {
        return Result.success(regionService.list());
    }
}
