package com.mini4.Book.config;

import com.mini4.Book.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // JwtAuthenticationFilter 주입

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS 필터 Bean 추가
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:5173"); // 프론트엔드 URL
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (JWT 사용 시 일반적으로 필요 없음)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 사용 시 세션 사용 안함
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 적용
                .authorizeHttpRequests(authorize -> authorize
                        // H2 콘솔 접근 허용
                        .requestMatchers("/h2-console/**").permitAll()
                        // 로그인, 회원가입 API 허용
                        .requestMatchers("/api/v1/users/register", "/api/v1/users/login").permitAll()
                        // 전체 도서 목록 조회 (GET /api/v1/books) 허용
                        .requestMatchers(HttpMethod.GET, "/api/v1/books").permitAll()
                        // 특정 도서 상세 조회 (GET /api/v1/books/{id}) 허용
                        .requestMatchers(HttpMethod.GET, "/api/v1/books/{id}").permitAll()
                        // 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // 폼 로그인 및 HTTP Basic 인증 비활성화
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())

                // H2 콘솔이 iframe으로 동작하기 위해 필요
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))

                // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}