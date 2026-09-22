package cn.snake.snakeaidbackend.controller.app;

import cn.snake.snakeaidbackend.common.PageResult;
import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.Article;
import cn.snake.snakeaidbackend.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/app/article")
@Tag(name = "APP-科普文章")
public class ArticleAppController {

    @Resource
    private ArticleService articleService;

    @Operation(summary = "文章列表（已发布）")
    @GetMapping("/page")
    public Result<PageResult<Article>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String keyword) {
        return Result.success(articleService.pageForApp(current, size, keyword));
    }

    @Operation(summary = "文章详情")
    @GetMapping("/{id}")
    public Result<Article> detail(@PathVariable Long id) {
        return Result.success(articleService.getDetail(id));
    }
}
