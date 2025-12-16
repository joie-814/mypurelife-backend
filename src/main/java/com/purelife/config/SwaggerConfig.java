package com.purelife.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
}
