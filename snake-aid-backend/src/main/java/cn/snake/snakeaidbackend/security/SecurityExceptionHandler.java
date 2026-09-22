package cn.snake.snakeaidbackend.security;

import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.common.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Security 异常处理器（返回 JSON 而非默认重定向）
 */
@Component
@RequiredArgsConstructor
public class SecurityExceptionHandler {

    private final ObjectMapper objectMapper;

    /** 未登录/Token 无效 → HTTP 401 */
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (req, resp, e) -> write(resp, 401, ResultCode.UNAUTHORIZED);
    }

    /** 无权限 → HTTP 403 */
    public AccessDeniedHandler accessDeniedHandler() {
        return (req, resp, e) -> write(resp, 403, ResultCode.FORBIDDEN);
    }

    /**
     * 业务 code（如 2002）和 HTTP status code（如 403）必须分离，
     * Spring 6 observability 要求 HTTP status 必须是合法的三位数（100-599）。
     */
    private void write(HttpServletResponse response, int httpStatus, ResultCode code) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(httpStatus);
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(code)));
    }
}
