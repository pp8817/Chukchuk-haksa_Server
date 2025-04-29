package com.chukchuk.haksa.global.exception;

import com.chukchuk.haksa.global.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 우리 프로젝트의 모든 커스텀 예외는 BaseException을 상속받는다.
     * 따라서 BaseException 하나로 모든 CustomException을 처리할 수 있다.
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<?>> handleBaseException(BaseException ex) {
        log.warn("[BaseException] {} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    /**
     * 잘못된 요청 파라미터 등 기본적인 IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("[IllegalArgumentException] {}", ex.getMessage());
        ErrorCode errorCode = ErrorCode.INVALID_ARGUMENT;
        return ResponseEntity
                .status(errorCode.status())
                .body(ApiResponse.error(errorCode.code(), ex.getMessage()));
    }

    /**
     * 예상하지 못한 모든 RuntimeException 처리 (서버 오류)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntime(RuntimeException ex) {
        log.error("[RuntimeException] {}", ex.getMessage(), ex);
        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
    }
}