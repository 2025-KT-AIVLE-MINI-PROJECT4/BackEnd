package com.mini4.Book.dto;

import java.time.LocalDate;

public class BookRegisterRequestDto {
    private Long userId;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publishedDate;
    private String content;
    private int price;
    private String category;
    private String imageUrl;
}
