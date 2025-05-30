package com.mini4.Book.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // user FK
    private Long userId;

    @Column(nullable = false)
    private String title;

    private String author; // 작자 미상 = null

    private String publisher;

    private LocalDateTime publishedDate;

    @Column
    private String content; // GPT 이미지 생성시 사용

    private int price;

    private String category;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt; // Soft Delete용

    // 생성된 시각
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 업데이트 시각
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
