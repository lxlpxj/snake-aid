package cn.snake.snakeaidbackend.controller.app;

import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.FirstAidStep;
import cn.snake.snakeaidbackend.mapper.FirstAidStepMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/first-aid")
@Tag(name = "App端-急救闯关关卡")
public class FirstAidAppController {

    @Resource
    private FirstAidStepMapper firstAidStepMapper;

    @GetMapping("/steps")
    @Operation(summary = "获取9步急救闯关关卡")
    public Result<List<FirstAidStep>> list() {
        List<FirstAidStep> list = firstAidStepMapper.selectList(
            new QueryWrapper<FirstAidStep>()
                .eq("enabled", 1)
                .orderByAsc("step_order")
        );
        return Result.success(list);
    }
}
