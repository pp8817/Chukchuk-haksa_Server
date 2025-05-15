package com.chukchuk.haksa.global.security.handler;

import com.chukchuk.haksa.global.common.response.ErrorResponse;
import com.chukchuk.haksa.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 인증 실패 (401 Unauthorized)
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        String exceptionType = (String) request.getAttribute("exception");
        ErrorCode errorCode = "expired".equals(exceptionType)
                ? ErrorCode.TOKEN_EXPIRED
                : ErrorCode.TOKEN_INVALID;

        log.warn("Authentication failed: {}, path: {}", errorCode.name(), request.getRequestURI());

        response.setStatus(errorCode.status().value());
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse error = ErrorResponse.of(errorCode.code(), errorCode.message());
        response.getWriter().write(objectMapper.writeValueAsString(error));
        response.getWriter().flush();
    }
}
