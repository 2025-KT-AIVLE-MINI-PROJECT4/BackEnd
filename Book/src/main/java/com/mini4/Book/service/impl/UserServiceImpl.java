package com.mini4.Book.service.impl;

import com.mini4.Book.domain.User;
import com.mini4.Book.dto.LoginRequestDto;
import com.mini4.Book.dto.UserRegisterRequestDto;
import com.mini4.Book.dto.UserDto;
import com.mini4.Book.exception.InvalidCredentialsException;
import com.mini4.Book.exception.ResourceNotFoundException;
import com.mini4.Book.exception.UserExistsException;
import com.mini4.Book.jwt.JwtTokenProvider;
import com.mini4.Book.repository.UserRepository;
import com.mini4.Book.securtiy.CustomUserDetails;
import com.mini4.Book.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화 주입
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder,
                           RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public UserDto registerUser(UserRegisterRequestDto request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserExistsException("이미 존재하는 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = User.builder()
                .name(request.getName())
                .password(encodedPassword)
                .email(request.getEmail())
                .build();

        User savedUser = userRepository.save(newUser);

        return UserDto.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .build();
    }

    @Override
    @Transactional
    public UserDto loginUser(LoginRequestDto request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        String refreshTokenKey = "RT:" + userId;
        redisTemplate.opsForValue().set(refreshTokenKey, refreshToken, 7 * 24 * 60 * 60 * 1000L, TimeUnit.MILLISECONDS);

        return UserDto.builder()
                .id(userId)
                .name(userDetails.getUserName())
                .email(userDetails.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public void logoutUser(Long userId, String accessToken) { // accessToken 파라미터 추가
        // 1. Redis에서 Refresh Token 삭제
        String refreshTokenKey = "RT:" + userId;
        if (redisTemplate.hasKey(refreshTokenKey)) {
            redisTemplate.delete(refreshTokenKey);
        } else {
            // Refresh Token이 없거나 이미 삭제된 경우 (예: 이미 로그아웃)
            // throw new ResourceNotFoundException("로그아웃할 Refresh Token을 찾을 수 없습니다."); // 필요에 따라 예외 처리
            // 보통은 이미 로그아웃된 상태로 간주하고 경고 로그만 남김
            System.out.println("Warning: Refresh Token not found for userId: " + userId + ". Possibly already logged out.");
        }

        // 2. Access Token 블랙리스트 처리
        // Access Token이 유효한 경우에만 블랙리스트에 추가
        if (StringUtils.hasText(accessToken) && jwtTokenProvider.validateToken(accessToken)) {
            Long expiration = jwtTokenProvider.getExpiration(accessToken); // AccessToken의 남은 만료 시간 (ms)
            if (expiration > 0) { // 만료되지 않은 토큰만 블랙리스트에 추가
                redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
                System.out.println("Access Token (ID: " + jwtTokenProvider.parseClaims(accessToken).getSubject() + ") blacklisted for " + expiration + "ms.");
            }
        } else {
            System.out.println("Warning: No valid Access Token provided for logout or token already expired/invalid.");
        }

        // 3. SecurityContext 클리어
        SecurityContextHolder.clearContext();
    }
}

