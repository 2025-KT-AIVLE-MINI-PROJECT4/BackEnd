package com.mini4.Book.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookRegisterRequestDto {
    private Long userId;
    private String title;
    private String author;
    private String publisher;
    private LocalDateTime publishedDate;
    private String content;
    private int price;
    private String category;
    private String imageUrl;
}
