package com.mini4.Book.service.impl;

import com.mini4.Book.domain.User;
import com.mini4.Book.dto.LoginRequestDto;
import com.mini4.Book.dto.RegisterRequestDto;
import com.mini4.Book.dto.UserDto;
import com.mini4.Book.exception.InvalidCredentialsException;
import com.mini4.Book.exception.ResourceNotFoundException;
import com.mini4.Book.exception.UserExistsException;
import com.mini4.Book.repository.UserRepository;
import com.mini4.Book.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화 주입

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDto registerUser(RegisterRequestDto request) {
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
    @Transactional(readOnly = true)
    public UserDto loginUser(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("잘못된 사용자 이름 또는 비밀번호입니다."));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("잘못된 사용자 이름 또는 비밀번호입니다.");
        }

        // TODO: 실제 JWT 토큰 발급 로직 필요 (여기서는 더미 토큰)
        String accessToken = "jwt_token_string"; // 실제 JWT 생성 로직 필요
        String refreshToken = "jwt_refresh_token_string"; // 실제 JWT 생성 로직 필요

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void logoutUser(Long userId) {
        // TODO: 실제 JWT 토큰 무효화 또는 세션 관리 로직이 필요
        // ex) Redis에 블랙리스트 토큰 저장 또는 세션 만료 등
        // 여기서는 단순히 성공 메시지를 반환하는 것으로 가정
        // 사용자 ID가 존재하는지 확인하는 로직은 선택 사항
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("로그아웃할 사용자를 찾을 수 없습니다."));
    }
}
