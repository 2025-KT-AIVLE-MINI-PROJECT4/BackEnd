package com.mini4.Book.controller;

import com.mini4.Book.dto.ApiResponse;
import com.mini4.Book.dto.LoginRequestDto;
import com.mini4.Book.dto.UserRegisterRequestDto;
import com.mini4.Book.dto.UserDto;
import com.mini4.Book.jwt.JwtAuthenticationFilter;
import com.mini4.Book.jwt.JwtTokenProvider;
import com.mini4.Book.security.CustomUserDetails;
import com.mini4.Book.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) { // 주입
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 이메일 회원가입
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody UserRegisterRequestDto request) {
        UserDto registeredUser = userService.registerUser(request);
        return new ResponseEntity<>(
                ApiResponse.success("회원가입 성공", registeredUser),
                HttpStatus.CREATED
        );
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto>> login(@RequestBody LoginRequestDto request) {
        UserDto loggedInUser = userService.loginUser(request);
        return new ResponseEntity<>(
                ApiResponse.success("로그인 성공", loggedInUser),
                HttpStatus.OK
        );
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            HttpServletRequest request
    ) {
        if (customUserDetails == null) {
            log.warn("UserController.logout: customUserDetails가 null입니다. 인증 정보 없음."); // 추가
            return new ResponseEntity<>(
                    ApiResponse.error("로그인 정보가 없습니다."),
                    HttpStatus.UNAUTHORIZED // 401 Unauthorized
            );
        }

        log.debug("UserController.logout: customUserDetails가 null이 아닙니다. User ID: {}", customUserDetails.getUserId()); // 추가
        log.debug("UserController.logout: customUserDetails User Name: {}", customUserDetails.getUsername()); // 추가

        Long userId = customUserDetails.getUserId();

        // 1. Request Header에서 Access Token 추출
        String accessToken = resolveToken(request);
        log.debug("UserController.logout: Extracted Access Token for logout: {}", StringUtils.hasText(accessToken) ? "Token Found" : "No Token"); // 추가

        // 2. 서비스에 사용자 ID와 Access Token 전달하여 로그아웃 처리
        userService.logoutUser(userId, accessToken);

        return new ResponseEntity<>(
                ApiResponse.success("로그아웃이 완료되었습니다."),
                HttpStatus.OK
        );
    }

    // HttpServletRequest에서 토큰 정보 추출하는 헬퍼 메서드 (JwtAuthenticationFilter와 유사)
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtAuthenticationFilter.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtAuthenticationFilter.BEARER_PREFIX)) {
            return bearerToken.substring(JwtAuthenticationFilter.BEARER_PREFIX.length());
        }
        return null;
    }
}
