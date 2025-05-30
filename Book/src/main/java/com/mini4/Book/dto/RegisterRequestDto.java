package com.mini4.Book.dto;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String name;
    private String password;
    private String email;
}
