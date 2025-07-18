package com.chukchuk.haksa.global.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT 인증 토큰을 입력하세요"
)
public class OpenApiConfig {

        @Value("${swagger.server-url}")
        private String serverUrl;

        @Value("${spring.profiles.active:default}")
        private String activeProfile;

        @Bean
        public OpenAPI customOpenAPI() {
                Server server = new Server()
                        .url(serverUrl)
                        .description(activeProfile.substring(0, 1).toUpperCase() + activeProfile.substring(1) + " Server");

                return new OpenAPI()
                        .info(new Info().title("척척학사 API")
                                .version("v1")
                                .description("API 명세서"))
                        .servers(List.of(server));
        }
}