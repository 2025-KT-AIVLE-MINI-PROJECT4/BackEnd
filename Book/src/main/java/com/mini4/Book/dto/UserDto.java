package com.mini4.Book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
    // 로그인 시 토큰 반환을 위한 필드
    private String accessToken;
    private String refreshToken;
}
