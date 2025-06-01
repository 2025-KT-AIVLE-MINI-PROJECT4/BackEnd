package com.mini4.Book.exception;

import com.mini4.Book.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException; // 이 부분을 임포트
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

    // 400 Bad Request (도서 등록/수정 시 필수값 누락 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    // 401 Unauthorized (로그인 실패, 유효하지 않은 토큰 등)
    // BadCredentialsException 추가하여 Spring Security의 인증 실패도 여기서 처리
    @ExceptionHandler({InvalidCredentialsException.class, UnauthorizedException.class, BadCredentialsException.class})
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(RuntimeException ex, WebRequest request) {
        // 보안을 위해 특정 메시지 대신 일반적인 인증 실패 메시지를 반환합니다.
        // Spring Security의 BadCredentialsException은 사용자 없음 또는 비밀번호 불일치 모두 포함합니다.
        return new ResponseEntity<>(
                ApiResponse.error("아이디 또는 비밀번호가 일치하지 않습니다."),
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
        logger.error("내부 서버 오류가 발생했습니다: {}", ex.getMessage(), ex); // 상세 스택 트레이스 로그

        return new ResponseEntity<>(
                ApiResponse.error("내부 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요. (" + ex.getMessage() + ")"), // 개발 시
                // ApiResponse.error("내부 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."), // 운영 시 (주석 처리된 라인 사용 권장)
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}