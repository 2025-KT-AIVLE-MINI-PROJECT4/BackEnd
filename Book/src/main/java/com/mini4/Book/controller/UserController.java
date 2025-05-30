package com.mini4.Book.controller;

import com.mini4.Book.dto.ApiResponse;
import com.mini4.Book.dto.LoginRequestDto;
import com.mini4.Book.dto.RegisterRequestDto;
import com.mini4.Book.dto.UserDto;
import com.mini4.Book.service.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 이메일 회원가입
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody RegisterRequestDto request) {
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
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody UserDto request) {
        userService.logoutUser(request.getId());
        return new ResponseEntity<>(
                ApiResponse.success("로그아웃이 완료되었습니다."),
                HttpStatus.OK
        );
    }
}
