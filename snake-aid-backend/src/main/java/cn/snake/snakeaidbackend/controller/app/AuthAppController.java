package cn.snake.snakeaidbackend.controller.app;

import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/app/auth")
@Tag(name = "APP-登录注册")
public class AuthAppController {

    @Resource
    private AuthService authService;

    @Operation(summary = "发送验证码")
    @PostMapping("/send-sms")
    public Result<Void> sendSms(@RequestBody Map<String, String> body) {
        authService.sendSmsCode(body.get("phone"));
        return Result.success();
    }

    @Operation(summary = "手机号+验证码登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        return Result.success(authService.appLoginBySms(body.get("phone"), body.get("smsCode")));
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<Long> register(@RequestBody Map<String, String> body) {
        return Result.success(authService.registerAppUser(body.get("phone"), body.get("nickname")).getId());
    }
}
