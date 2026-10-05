package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.UserQuiz;
import cn.snake.snakeaidbackend.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** 后台 - 考试记录查看 */
@RestController
@RequestMapping("/admin/quiz")
@Tag(name = "后台-考试记录")
public class QuizAdminController {

    @Resource
    private QuizService quizService;

    @Operation(summary = "分页查询考试记录")
    @GetMapping("/page")
    public Result<PageResult<UserQuiz>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String category) {
        return Result.success(quizService.pageQuizzes(current, size, category));
    }

    @Operation(summary = "查看某次考试的答题详情")
    @GetMapping("/{id}/detail")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        return Result.success(quizService.getQuizDetail(id));
    }
}
