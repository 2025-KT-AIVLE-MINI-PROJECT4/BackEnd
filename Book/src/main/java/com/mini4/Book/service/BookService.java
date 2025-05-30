package com.mini4.Book.service;

import com.mini4.Book.domain.*;
import com.mini4.Book.dto.RegisterRequestDto;
import com.mini4.Book.exception.ResourceNotFoundException;
import com.mini4.Book.repository.BookRepository;
import com.mini4.Book.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long register(RegisterRequestDto dto) {
        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new ResourceNotFoundException());

        Book book = Book.builder()
                .user(user)
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .publisher(dto.getPublisher())
                .publishedDate(dto.getPublishedDate())
                .content(dto.getContent())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .imageUrl(dto.getImageUrl())
                .build();

        return bookRepository.save(book).getId();
    }
}
