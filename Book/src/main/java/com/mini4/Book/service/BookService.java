package com.mini4.Book.service;

import com.mini4.Book.dto.BookRequestDto;
import com.mini4.Book.dto.BookResponseDto;

import java.util.List;

public interface BookService {
    BookResponseDto createBook(BookRequestDto requestDto, Long userId);
    BookResponseDto getBookById(Long bookId);
    List<BookResponseDto> getAllBooks();
    List<BookResponseDto> getBooksByUserId(Long userId);
    BookResponseDto updateBook(Long bookId, BookRequestDto requestDto, Long userId);
    void deleteBook(Long bookId, Long userId);
}
