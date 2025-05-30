package com.mini4.Book.service.impl;

import com.mini4.Book.domain.Book;
import com.mini4.Book.domain.User;
import com.mini4.Book.dto.BookRegisterRequestDto;
import com.mini4.Book.exception.ResourceNotFoundException;
import com.mini4.Book.repository.BookRepository;
import com.mini4.Book.repository.UserRepository;
import com.mini4.Book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DialectOverride;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    public Long register(BookRegisterRequestDto dto) {
        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("No User Error"));

        Book book = Book.builder()
                .userId(user.getId())
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
