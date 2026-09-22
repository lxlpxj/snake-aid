package cn.snake.snakeaidbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / OpenAPI 配置
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        String bearer = "BearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("毒蛇咬伤自救科普 APP - 后端接口文档")
                        .description("紧急自救、蛇种识别、医院推荐、内容管理等接口")
                        .version("v1.0.0")
                        .contact(new Contact().name("snake-aid-dev")))
                .addSecurityItem(new SecurityRequirement().addList(bearer))
                .schemaRequirement(bearer, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization"));
    }
}
