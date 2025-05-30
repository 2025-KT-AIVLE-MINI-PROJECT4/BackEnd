package com.mini4.Book.dto;

import com.mini4.Book.domain.Book;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BookResponseDto {
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String publishedDate;
    private String content;
    private Integer price;
    private String category;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String authorName; // 책을 등록한 사용자 이름 (User 엔티티에서 가져옴)
    private LocalDateTime deletedAt; // Soft Delete용 필드

    public BookResponseDto(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.publisher = book.getPublisher();
        this.publishedDate = book.getPublishedDate();
        this.content = book.getContent();
        this.price = book.getPrice();
        this.category = book.getCategory();
        this.imageUrl = book.getImageUrl();
        this.createdAt = book.getCreatedAt();
        this.updatedAt = book.getUpdatedAt();
        this.authorName = book.getUser() != null ? book.getUser().getName() : "Unknown";
        this.deletedAt = book.getDeletedAt();
    }
}