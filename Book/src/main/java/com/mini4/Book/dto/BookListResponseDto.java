package com.mini4.Book.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BookListResponseDto {
    private List<BookResponseDto> books;

    public BookListResponseDto(List<BookResponseDto> books) {
        this.books = books;
    }
}