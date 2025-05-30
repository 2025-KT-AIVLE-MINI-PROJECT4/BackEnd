package com.mini4.Book.controller;

import com.mini4.Book.domain.Book;
import com.mini4.Book.dto.BookRegisterRequestDto;
import com.mini4.Book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public Book registerBook(@RequestBody BookRegisterRequestDto dto) {

        Long book = bookService.register(dto);

    }
}
