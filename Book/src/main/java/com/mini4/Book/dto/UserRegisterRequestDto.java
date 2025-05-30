package com.mini4.Book.dto;

import lombok.Data;

@Data
public class UserRegisterRequestDto {
    private String name;
    private String password;
    private String email;
}
