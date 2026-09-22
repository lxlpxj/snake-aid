package cn.snake.snakeaidbackend.controller.admin;

import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.entity.OperationLog;
import cn.snake.snakeaidbackend.mapper.OperationLogMapper;
import cn.snake.snakeaidbackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/auth")
@Tag(name = "后台-登录鉴权")
public class AuthController {

    @Resource
    private AuthService authService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private OperationLogMapper logMapper;

    @Operation(summary = "后台账号登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String username = body.get("username");
        Result<Map<String, Object>> result = Result.success(authService.login(username, body.get("password")));

        // 登录成功 → 写 LOGIN 分类日志（手动，因为切面自动归类为 SYSTEM）
        try {
            OperationLog op = new OperationLog();
            op.setCategory("LOGIN");
            op.setOperation("登录: " + username);
            op.setMethod("AuthController#login");
            op.setCreateTime(LocalDateTime.now());
            op.setUserId(((Number) result.getData().get("userId")).longValue());
            op.setUsername(username);  // 登录时 username 已知，直接存
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isBlank()) ip = request.getHeader("X-Real-IP");
            if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
            op.setIp(ip == null ? "-" : ip.split(",")[0].trim());
            logMapper.insert(op);
        } catch (Exception ignored) {}

        return result;
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    /**
     * 开发环境临时用：生成 BCrypt 密码哈希
     * 使用方式：curl "http://localhost:8080/api/admin/auth/encode?password=admin123"
     */
    @GetMapping("/encode")
    public Result<Map<String, String>> encode(@RequestParam(defaultValue = "admin123") String password) {
        String encoded = passwordEncoder.encode(password);
        log.warn("[DEV] BCrypt encoded '{}' -> {}", password, encoded);
        return Result.success(Map.of("original", password, "encoded", encoded));
    }
}
