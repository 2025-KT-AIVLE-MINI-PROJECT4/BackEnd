package com.mini4.Book.service;

import com.mini4.Book.dto.LoginRequestDto;
import com.mini4.Book.dto.RegisterRequestDto;
import com.mini4.Book.dto.UserDto;

public interface UserService {
    UserDto registerUser(RegisterRequestDto request);
    UserDto loginUser(LoginRequestDto request);
    void logoutUser(Long userId); // 토큰 무효화 로직 필요
}
