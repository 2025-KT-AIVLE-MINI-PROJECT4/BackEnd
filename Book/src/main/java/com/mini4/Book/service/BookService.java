package com.mini4.Book.service;

import com.mini4.Book.dto.BookRegisterRequestDto;

public interface
BookService {
    Long register(BookRegisterRequestDto dto);
}