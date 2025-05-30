package com.mini4.Book.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookRequestDto {
    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;

    private String author;

    @NotBlank(message = "출판사는 필수 입력 값입니다.")
    private String publisher;

    @NotBlank(message = "출판일은 필수 입력 값입니다.")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "출판일은 YYYY-MM-DD 형식이어야 합니다.")
    private String publishedDate;

    private String content;

    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    private String category;
    private String imageUrl;
}
