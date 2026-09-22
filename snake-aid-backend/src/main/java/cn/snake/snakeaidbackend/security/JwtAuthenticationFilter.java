package cn.snake.snakeaidbackend.security;

import cn.snake.snakeaidbackend.common.Result;
import cn.snake.snakeaidbackend.common.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 认证过滤器
 *
 * 解析 JWT 后把 role 字段（如 CONTENT_ADMIN）加上 "ROLE_" 前缀放入 authorities，
 * Spring Security 的 hasRole("CONTENT_ADMIN") 会自动映射到 "ROLE_CONTENT_ADMIN"。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (StringUtils.hasText(token)) {
            try {
                Claims claims = jwtUtil.parse(token);
                Long userId = jwtUtil.getUserId(claims);
                String username = jwtUtil.getUsername(claims);
                String role = jwtUtil.getRole(claims);

                // 检查 Token 是否已拉黑（登出场景）—— Redis 挂了也别崩
                try {
                    String blackKey = "jwt:blacklist:" + userId;
                    if (Boolean.TRUE.equals(redisTemplate.hasKey(blackKey))) {
                        writeError(response, ResultCode.TOKEN_INVALID);
                        return;
                    }
                } catch (Exception redisEx) {
                    log.warn("Redis 不可用，跳过黑名单检查: {}", redisEx.getMessage());
                }

                // 构造认证对象：role 加 ROLE_ 前缀给 Spring Security
                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + role)
                );
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 把 username 存到 request，供操作日志切面使用
                request.setAttribute("__username__", username);

            } catch (Exception e) {
                log.warn("JWT 校验失败（非公开路径才拦）: {}", e.getMessage());
                // 只有非公开路径才返回 401，登录等 permitAll 路径放行
                String uri = request.getRequestURI();
                if (isProtectedPath(uri)) {
                    writeError(response, ResultCode.TOKEN_INVALID);
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /** 非公开路径（需要 token 才能访问的） */
    private boolean isProtectedPath(String uri) {
        // 去掉 context-path 前缀
        String path = uri.replaceFirst("^/api", "");
        return path.startsWith("/admin/") && !path.startsWith("/admin/auth/");
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    private void writeError(HttpServletResponse response, ResultCode code) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(code)));
    }
}
