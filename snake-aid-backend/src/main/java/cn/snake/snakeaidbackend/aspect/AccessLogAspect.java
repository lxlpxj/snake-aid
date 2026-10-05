package cn.snake.snakeaidbackend.aspect;

import cn.snake.snakeaidbackend.service.AccessStatisticsService;
import cn.snake.snakeaidbackend.service.PointsService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class AccessLogAspect {

    @Resource
    private AccessStatisticsService accessStatService;
    @Resource
    private PointsService pointsService;

    @Around("execution(* cn.snake.snakeaidbackend.controller.app..*.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return result;
            HttpServletRequest request = attrs.getRequest();
            String uri = request.getRequestURI();
            String method = request.getMethod();
            Long userId = extractUserId();

            // ==== 1. 访问统计（所有访问都记）====
            if (uri.contains("/app/snake/page") && "GET".equals(method)) {
                String province = request.getParameter("province");
                String city = request.getParameter("city");
                accessStatService.increment("snake_filter", province, city);
            } else if (uri.matches(".*/app/snake/\\d+$") && "GET".equals(method)) {
                accessStatService.increment("snake_view", null, null);
            } else if (uri.contains("/app/hospital/nearby") && "GET".equals(method)) {
                String province = request.getParameter("province");
                accessStatService.increment("hospital_view", province, null);
            } else if (uri.contains("/app/quiz/submit") && "POST".equals(method)) {
                accessStatService.increment("quiz_submit", null, null);
            }

            // ==== 2. 积分发放（登录用户才加）====
            if (userId == null) return result;

            // 蛇种详情 → +2 分（refId=snakeId 防重复）
            if (uri.matches(".*/app/snake/\\d+$") && "GET".equals(method)) {
                Long snakeId = parseLastSegment(uri);
                pointsService.award(userId, "snake_view", snakeId, "学习蛇种知识");
            }
            // 医院查询 → +1 分
            else if (uri.contains("/app/hospital/nearby") && "GET".equals(method)) {
                pointsService.award(userId, "hospital_view", null, "查看附近医院");
            }
            // 文章详情 → +3 分
            else if (uri.matches(".*/app/article/\\d+$") && "GET".equals(method)) {
                Long artId = parseLastSegment(uri);
                pointsService.award(userId, "article_read", artId, "阅读科普文章");
            }
            // 急救指南详情 → +5 分
            else if (uri.matches(".*/app/guide/\\d+$") && "GET".equals(method)) {
                Long guideId = parseLastSegment(uri);
                pointsService.award(userId, "guide_read", guideId, "阅读急救指南");
            }

        } catch (Exception e) {
            log.warn("access stat aspect error: {}", e.getMessage());
        }
        return result;
    }

    private Long extractUserId() {
        try {
            Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return p instanceof Long ? (Long) p : null;
        } catch (Exception e) {
            return null;
        }
    }

    private Long parseLastSegment(String uri) {
        try {
            String[] parts = uri.split("/");
            return Long.parseLong(parts[parts.length - 1]);
        } catch (Exception e) {
            return null;
        }
    }
}
