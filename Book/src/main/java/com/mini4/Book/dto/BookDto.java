package com.mini4.Book.dto; //데이터 보내는 클래스

public class BookDto {

    private Long id;
    private String title;
    private String author;

    public BookDto() {} // 생성자

    public BookDto(Long id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}

