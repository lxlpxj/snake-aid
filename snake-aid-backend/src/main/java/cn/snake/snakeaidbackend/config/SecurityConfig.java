package cn.snake.snakeaidbackend.config;

import cn.snake.snakeaidbackend.security.JwtAuthenticationFilter;
import cn.snake.snakeaidbackend.security.SecurityExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 配置 —— 按需求文档角色边界
 *
 * ═══════════════════════════════════════════════════════════
 * 角色                        核心职责                          URL 权限
 * ──────────────────────────────────────────────────────────
 * SYS_ADMIN 系统管理员        用户/权限/系统维护 + 操作日志     /admin/user/** + /admin/log/** + /admin/statistics/**
 * CONTENT_ADMIN 内容管理员    维护科普文章、蛇种库、医院血清库存  /admin/snake/** + /admin/hospital/** + /admin/article/**
 * MEDICAL_ADVISOR 医疗顾问    审核内容、更新急救指南              /admin/guide/** + 审核接口（见下）
 * ═══════════════════════════════════════════════════════════
 *
 * 审核接口（audit）仅 MEDICAL_ADVISOR（医疗顾问）可访问，
 * SYS_ADMIN 不参与审核，避免越权干预业务内容。
 *
 * APP 普通用户 → 统一 role = APP_USER，不走这里，APP 端接口在 SecurityConfig 全 permitAll
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final SecurityExceptionHandler securityExceptionHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(securityExceptionHandler.authenticationEntryPoint())
                        .accessDeniedHandler(securityExceptionHandler.accessDeniedHandler())
                )
                .authorizeHttpRequests(auth -> auth

                        // ============ 公开接口 ============
                        .requestMatchers(
                                "/admin/auth/**",
                                "/admin/upload/**",
                                "/app/auth/**",
                                "/app/**",
                                "/uploads/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/doc.html",
                                "/webjars/**",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()

                        // ============ SYS_ADMIN 专属：用户管理 ============
                        .requestMatchers("/admin/user/**").hasRole("SYS_ADMIN")

                        // ============ 三角色都能看日志 + 统计（Controller 内再按角色过滤范围） ============
                        .requestMatchers("/admin/log/**").hasAnyRole("SYS_ADMIN", "CONTENT_ADMIN", "MEDICAL_ADVISOR")
                        .requestMatchers("/admin/statistics/**").hasAnyRole("SYS_ADMIN", "CONTENT_ADMIN", "MEDICAL_ADVISOR")

                        // ============ 审核接口（仅 MEDICAL_ADVISOR）—— 必须在 /** 之前声明 ============
                        .requestMatchers("/admin/guide/*/audit", "/admin/snake/*/audit", "/admin/article/*/audit")
                                .hasRole("MEDICAL_ADVISOR")

                        // ============ CONTENT_ADMIN：蛇种库 + 医院血清 + 科普文章 ============
                        .requestMatchers("/admin/snake/**").hasAnyRole("CONTENT_ADMIN", "MEDICAL_ADVISOR")
                        .requestMatchers("/admin/hospital/**").hasRole("CONTENT_ADMIN")
                        .requestMatchers("/admin/article/**").hasAnyRole("CONTENT_ADMIN", "MEDICAL_ADVISOR")

                        // ============ CONTENT_ADMIN + MEDICAL_ADVISOR：急救指南（内容管理员提交，医疗顾问审核/编辑） ============
                        .requestMatchers("/admin/guide/**").hasAnyRole("CONTENT_ADMIN", "MEDICAL_ADVISOR")

                        // ============ CONTENT_ADMIN + MEDICAL_ADVISOR：急救闯关关卡 ============
                        .requestMatchers("/admin/first-aid-step/**").hasAnyRole("CONTENT_ADMIN", "MEDICAL_ADVISOR")

                        // 兜底：其余 /admin/** 至少需要后台任一角色
                        .requestMatchers("/admin/**").hasAnyRole("SYS_ADMIN", "CONTENT_ADMIN", "MEDICAL_ADVISOR")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
