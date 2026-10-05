package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.Question;
import cn.snake.snakeaidbackend.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** 后台 - 题库管理 */
@RestController
@RequestMapping("/admin/question")
@Tag(name = "后台-题库管理")
public class QuestionAdminController {

    @Resource
    private QuizService quizService;

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public Result<PageResult<Question>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer questionType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(quizService.pageQuestions(current, size, category, questionType, keyword, status));
    }

    @Operation(summary = "题库统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        return Result.success(quizService.getQuestionStats());
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    public Result<Question> get(@PathVariable Long id) {
        return Result.success(quizService.getQuestion(id));
    }

    @Operation(summary = "新增/修改")
    @PostMapping
    public Result<Question> save(@RequestBody Question q) {
        return Result.success(quizService.saveQuestion(q));
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(quizService.deleteQuestion(id));
    }

    @Operation(summary = "启/禁用切换")
    @PostMapping("/{id}/toggle")
    public Result<Boolean> toggle(@PathVariable Long id) {
        return Result.success(quizService.toggleStatus(id));
    }
}
