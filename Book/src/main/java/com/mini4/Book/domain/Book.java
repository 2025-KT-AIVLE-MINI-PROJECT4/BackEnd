package com.mini4.Book.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_table")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 활성화
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 100)
    private String author; // 작자 미상 = null

    @Column(length = 100)
    private String publisher;

    @Column
    private String publishedDate; // YYYY-MM-DD 형식으로 저장 (String)

    @Column(columnDefinition = "TEXT") // 긴 텍스트 저장을 위해 TEXT 타입으로
    private String content;

    @Column
    private Integer price; // int 대신 Integer를 사용하여 null 허용

    @Column
    private String category;

    @Column(name = "image_url", length = 1024)
    private String imageUrl;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 책을 등록한 사용자 (외래키)

    private LocalDateTime deletedAt; // Soft Delete용

    @Builder
    public Book(String title, String author, String publisher, String publishedDate, String content, Integer price, String category, String imageUrl, User user) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.content = content;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.user = user;
    }

    public void update(String title, String author, String publisher, String publishedDate, String content, Integer price, String category, String imageUrl) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.content = content;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    // Soft Delete를 위한 메서드
    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }
}
