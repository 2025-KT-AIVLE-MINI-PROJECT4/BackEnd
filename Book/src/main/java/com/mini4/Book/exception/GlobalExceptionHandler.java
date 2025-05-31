package com.mini4.Book.exception;

import com.mini4.Book.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404 Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    // 400 Bad Request (회원가입 중복, 필수 값 누락 등)
    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserExistsException(UserExistsException ex, WebRequest request) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    // 400 Bad Request (도서 등록/수정 시 필수값 누락 등) [cite: 12, 26]
    @ExceptionHandler(IllegalArgumentException.class) // 예시로 IllegalArgumentException 사용. 더 구체적인 DTO 유효성 검증 예외를 사용할 수 있습니다.
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    // 401 Unauthorized (로그인 실패, 유효하지 않은 토큰 등)
    @ExceptionHandler({InvalidCredentialsException.class, UnauthorizedException.class})
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    // 403 Forbidden (권한 없음)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbiddenException(ForbiddenException ex, WebRequest request) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage()),
                HttpStatus.FORBIDDEN
        );
    }


    // 500 Internal Server Error (모든 예상치 못한 예외)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex, WebRequest request) {
        // 실제 운영 환경에서는 상세한 에러 메시지를 클라이언트에 직접 노출하지 않을 수 있습니다.
        // 하지만 개발/테스트 단계에서는 유용합니다.
        // 중요: 에러 로그는 항상 남겨야 합니다.
        logger.error("내부 서버 오류가 발생했습니다: {}", ex.getMessage(), ex); // 상세 스택 트레이스 로그

        // 클라이언트에게는 좀 더 일반적인 메시지를 주거나, ex.getMessage()를 포함할 수 있습니다.
        return new ResponseEntity<>(
                ApiResponse.error("내부 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요. (" + ex.getMessage() + ")"), // 개발 시
                // ApiResponse.error("내부 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."), // 운영 시
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
