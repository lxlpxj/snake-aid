package cn.snake.snakeaidbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源映射 —— 让上传到 uploads/ 目录的图片能通过 HTTP 访问
 *
 * 例：上传 uploads/silver_krait.jpg
 * 访问 URL：http://localhost:8080/api/uploads/silver_krait.jpg
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 把 /uploads/** 请求映射到本地 uploads/ 目录（用绝对路径更稳）
        String absPath = new java.io.File(uploadDir).getAbsolutePath();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absPath + java.io.File.separator);
    }
}
