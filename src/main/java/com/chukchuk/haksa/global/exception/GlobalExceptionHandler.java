package com.chukchuk.haksa.global.exception;

import com.chukchuk.haksa.global.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * BaseException (우리 커스텀 예외 최상위 클래스) 처리
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        log.warn("[BaseException] {} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        return ResponseEntity
                .status(ex.getStatus())
                .body(ErrorResponse.of(ex.getCode(), ex.getMessage(), null));
    }

    /**
     * IllegalArgumentException 처리 (잘못된 요청 파라미터)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("[IllegalArgumentException] {}", ex.getMessage());
        ErrorCode errorCode = ErrorCode.INVALID_ARGUMENT;
        return ResponseEntity
                .status(errorCode.status())
                .body(ErrorResponse.of(errorCode.code(), ex.getMessage(), null));
    }

    /**
     * RuntimeException 처리 (예상 못한 서버 오류)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("[RuntimeException] {}", ex.getMessage(), ex);
        return ResponseEntity
                .internalServerError()
                .body(ErrorResponse.of("INTERNAL_ERROR", "서버 오류가 발생했습니다.", null));
    }
}