package com.mini4.Book.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
