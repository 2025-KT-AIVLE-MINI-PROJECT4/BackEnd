package com.mini4.Book.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 활성화
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (H2 콘솔 사용을 위해)
                .authorizeHttpRequests(authorize -> authorize
                        // H2 콘솔에 대한 접근 허용 (프레임 옵션을 위해 permitAll() 추가)
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll() // '/h2-console/**' 경로에 대한 모든 요청 허용
                        // API 정의서에 명시된 로그인, 회원가입 API도 인증 없이 접근 허용
                        .requestMatchers("/api/v1/users/register", "/api/v1/users/login").permitAll() // 회원가입, 로그인 허용
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                .formLogin(formLogin -> formLogin.disable()) // 기본 폼 로그인 비활성화 (REST API이므로)
                .httpBasic(httpBasic -> httpBasic.disable()) // 기본 HTTP Basic 인증 비활성화 (REST API이므로)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())); // H2 콘솔이 iframe으로 동작하기 위해 필요

        return http.build();
    }
}