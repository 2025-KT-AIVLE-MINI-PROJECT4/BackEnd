package com.mini4.Book.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI(); // 현재 요청 URI 로깅

        log.debug("===== JwtAuthenticationFilter : Request URI [{}] =====", requestURI);

        // 1. Request Header에서 토큰 추출
        String token = resolveToken(request);
        log.debug("Extracted token from header: {}", StringUtils.hasText(token) ? "Token Found" : "No Token");
        if (StringUtils.hasText(token)) {
            log.debug("Token Start: {}, Token End: {}", token.substring(0, Math.min(token.length(), 20)), token.substring(Math.max(0, token.length() - 20))); // 토큰 앞/뒤 일부 로깅
        }


        // 2. validateToken으로 토큰 유효성 검사
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가져와 SecurityContext에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, URI: {}", authentication.getName(), requestURI);
        } else {
            if (StringUtils.hasText(token)) { // 토큰은 있지만 유효하지 않은 경우
                log.debug("유효하지 않은 JWT 토큰입니다. 토큰: {}", token);
            } else { // 토큰이 아예 없는 경우
                log.debug("요청에 JWT 토큰이 없습니다. URI: {}", requestURI);
            }
        }

        filterChain.doFilter(request, response); // 다음 필터로 요청 전달
    }

    // Request Header에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}