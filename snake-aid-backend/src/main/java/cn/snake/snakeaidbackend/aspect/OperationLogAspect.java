package cn.snake.snakeaidbackend.aspect;

import cn.snake.snakeaidbackend.entity.OperationLog;
import cn.snake.snakeaidbackend.mapper.OperationLogMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * Auto operation log aspect
 *
 * Intercept all admin Controllers and categorize by URL:
 *   /admin/auth/login                       -> LOGIN (written explicitly in AuthController)
 *   /admin/user/**                          -> SYSTEM
 *   /admin/{id}/audit, /submit-audit        -> AUDIT
 *   /admin/snake|article|hospital|guide/**  -> CONTENT
 *   others                                  -> SYSTEM
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Resource
    private OperationLogMapper logMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 防止同一次 HTTP 请求写多条日志的标记 key */
    private static final String LOGGED_ATTR = "__op_logged__";

    @Around("execution(* cn.snake.snakeaidbackend.controller..*.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result;
        String category = "CONTENT";
        String operation = "";
        String methodName = point.getSignature().getName();
        String methodCN = translateMethod(methodName);

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return point.proceed();
        HttpServletRequest request = attrs.getRequest();

        // 同一次 HTTP 请求已经写过日志 → 直接放行，不再写
        if (Boolean.TRUE.equals(request.getAttribute(LOGGED_ATTR))) {
            return point.proceed();
        }

        try {
            String uri = request.getRequestURI();
            if (!uri.contains("/admin/")) {
                return point.proceed();
            }
            category = resolveCategory(uri);
            operation = buildOpDesc(uri, request.getMethod());
            result = point.proceed();

        } catch (Throwable t) {
            writeLog("SYSTEM", "异常: " + t.getClass().getSimpleName() + " - " + operation, methodCN, request, point);
            request.setAttribute(LOGGED_ATTR, Boolean.TRUE);
            throw t;
        }

        writeLog(category, operation, methodCN, request, point);
        request.setAttribute(LOGGED_ATTR, Boolean.TRUE);
        return result;
    }

    /** Controller 方法名 → 中文 */
    private String translateMethod(String name) {
        return switch (name) {
            case "page", "list"           -> "分页列表";
            case "getById", "detail", "get" -> "详情";
            case "save", "add", "create"  -> "新增保存";
            case "update", "edit", "modify" -> "修改保存";
            case "delete", "remove"       -> "删除";
            case "audit"                  -> "审核";
            case "submitAudit", "submit-audit" -> "提交审核";
            case "offline", "disable"    -> "下线/禁用";
            case "enable"                 -> "启用";
            case "resetPassword", "reset" -> "重置密码";
            case "login"                  -> "登录";
            case "logout"                 -> "登出";
            case "categories"             -> "查询分类";
            case "pageByRole", "overview" -> "统计概览";
            default                       -> name;
        };
    }

    private String resolveCategory(String uri) {
        if (uri.contains("/audit") || uri.contains("/submit-audit")) return "AUDIT";
        if (uri.contains("/admin/user/") || uri.contains("/admin/log/")
                || uri.contains("/admin/statistics/") || uri.contains("/admin/auth/")) return "SYSTEM";
        if (uri.contains("/admin/snake/") || uri.contains("/admin/article/")
                || uri.contains("/admin/hospital/") || uri.contains("/admin/guide/")) return "CONTENT";
        return "SYSTEM";
    }

    private String buildOpDesc(String uri, String httpMethod) {
        String action = switch (httpMethod) {
            case "GET"    -> "查询";
            case "POST"   -> "新增";
            case "PUT"    -> "修改";
            case "DELETE" -> "删除";
            default       -> httpMethod;
        };
        if (uri.contains("/audit"))               action = "审核通过";
        else if (uri.contains("/submit-audit"))   action = "提交审核";
        else if (uri.contains("/offline") || uri.contains("/disable")) action = "下线/禁用";
        else if (uri.contains("/enable"))         action = "启用";
        else if (uri.contains("/reset"))          action = "重置密码";

        String target = uri.replaceAll(".*/admin/", "").split("/")[0];
        String targetCN = switch (target) {
            case "user"        -> "账号";
            case "snake"       -> "蛇种";
            case "article"     -> "科普文章";
            case "hospital"    -> "医院";
            case "guide"       -> "急救指南";
            case "log"         -> "操作日志";
            case "statistics"  -> "数据统计";
            case "auth"        -> "登录认证";
            default            -> target;
        };
        return action + targetCN;
    }

    private void writeLog(String category, String operation, String methodName,
                          HttpServletRequest request, ProceedingJoinPoint point) {
        try {
            OperationLog op = new OperationLog();
            op.setCategory(category);
            op.setOperation(operation);
            op.setMethod(methodName);
            op.setCreateTime(LocalDateTime.now());
            op.setIp(getClientIp(request));

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Long uid) {
                op.setUserId(uid);
            }
            // 从 request 里拿 JWT 解析出来的用户名（JwtAuthenticationFilter 设置的）
            Object username = request.getAttribute("__username__");
            if (username != null) {
                op.setUsername(username.toString());
            }

            Object[] args = point.getArgs();
            if (args.length > 0) {
                try {
                    String json = objectMapper.writeValueAsString(args);
                    op.setParams(json.length() > 2000 ? json.substring(0, 2000) + "..." : json);
                } catch (Exception ignored) {}
            }

            logMapper.insert(op);
        } catch (Exception e) {
            log.warn("write op log failed: {}", e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) ip = req.getHeader("X-Real-IP");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) ip = req.getRemoteAddr();
        return ip == null ? "-" : ip.split(",")[0].trim();
    }
}
