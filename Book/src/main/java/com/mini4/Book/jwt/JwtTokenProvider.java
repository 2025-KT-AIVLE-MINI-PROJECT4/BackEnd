package com.mini4.Book.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j // Lombok을 이용한 로깅
@Component // 스프링 빈으로 등록
public class JwtTokenProvider {

    private final Key key; // JWT 서명에 사용할 키
    private final UserDetailsService userDetailsService;

    // application.yml에서 secret 값 주입 (base64 인코딩된 값)
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            UserDetailsService userDetailsService) { // 주입
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userDetailsService = userDetailsService; // 필드 초기화
    }

    // JWT 토큰 생성
    public String generateToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        // Access Token 만료 시간 설정 (예: 30분)
        Date accessTokenExpiresIn = new Date(now + 1000 * 60 * 30); // 30분

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName()) // 토큰 제목 (여기서는 사용자 ID 또는 이메일)
                .claim("auth", authorities) // 권한 정보 (예: "ROLE_USER")
                .setExpiration(accessTokenExpiresIn) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 서명 (비밀키 사용)
                .compact();

        return accessToken;
    }

    // Refresh Token 생성 (별도의 메서드로 분리)
    public String generateRefreshToken(Authentication authentication) {
        long now = (new Date()).getTime();
        // Refresh Token 만료 시간 설정 (예: 7일)
        Date refreshTokenExpiresIn = new Date(now + 1000 * 60 * 60 * 24 * 7); // 7일

        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return refreshToken;
    }


    // JWT 토큰을 복호화하여 인증 객체(Authentication) 반환
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // claims.getSubject() (사용자 이메일)을 통해 CustomUserDetailsService에서 UserDetails를 로드
        UserDetails principal = userDetailsService.loadUserByUsername(claims.getSubject());

        // principal이 CustomUserDetails 타입이므로, 올바르게 설정됩니다.
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 정보 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.", e);
        }
        return false;
    }

    // Access Token에서 Claims 추출 (만료된 토큰의 경우에도 클레임을 가져오기 위해)
    public Claims parseClaims(String accessToken) { // private -> public으로 변경
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // Access Token의 남은 만료 시간 (ms)
    public Long getExpiration(String accessToken) {
        // Access Token 만료 시간
        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        // 남은 시간 계산
        return (expiration.getTime() - now);
    }
}
