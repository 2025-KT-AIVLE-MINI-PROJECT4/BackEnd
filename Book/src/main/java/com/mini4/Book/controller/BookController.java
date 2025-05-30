package com.mini4.Book.controller;

import com.mini4.Book.dto.BookDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/books")

public class BookController {

    // 예시로 해둔 샘플 데이터
    private List<BookDto> getSampleBooks() {
        return Arrays.asList(
                new BookDto(1L, "자바의 정석", "남궁성"),
                new BookDto(2L, "Effective Java", "Joshua Bloch"),
                new BookDto(3L, "스프링 인 액션", "Craig Walls")
        );
    }

    // 도서 목록 조회를 get 이랑 books
    @GetMapping
    public List<BookDto> getBooks() {
        return getSampleBooks();
    }

    // 도서 상세 조회 api get 이랑 /books/{id}
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        return getSampleBooks().stream()
                .filter(book -> book.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
