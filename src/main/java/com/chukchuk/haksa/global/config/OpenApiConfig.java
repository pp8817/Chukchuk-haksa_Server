package com.chukchuk.haksa.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "척척학사 API", version = "v0", description = "API 명세서"),
        security = @SecurityRequirement(name = "bearerAuth"), // 모든 API에 인증 기본 적용
        servers = {
                @Server(url = "http://localhost:8080", description = "로컬 서버")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT 인증 토큰을 입력하세요"
)
public class OpenApiConfig {

}
