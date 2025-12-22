package com.purelife.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("pureLife API 文件")
                        .version("1.0.0")
                        .description("pureLife 保健食品電商平台的 API 文件")
                        .contact(new Contact()
                                .name("Joie")
                                .email("joie@example.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("本地開發環境")
                ));
    }

    // 在 Swagger（springdoc-openapi）設定 security scheme
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
            );
    }
}
