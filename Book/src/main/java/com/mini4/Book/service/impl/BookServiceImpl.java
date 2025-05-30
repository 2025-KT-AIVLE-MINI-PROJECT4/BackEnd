package com.mini4.Book.service.impl;

import com.mini4.Book.domain.Book;
import com.mini4.Book.domain.User;
import com.mini4.Book.dto.BookRequestDto;
import com.mini4.Book.dto.BookResponseDto;
import com.mini4.Book.repository.BookRepository;
import com.mini4.Book.repository.UserRepository;
import com.mini4.Book.service.BookService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service // @Service 어노테이션을 구현체에 추가
@RequiredArgsConstructor
public class BookServiceImpl implements BookService { // BookService 인터페이스 구현

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookResponseDto createBook(BookRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. User ID: " + userId));

        Book book = Book.builder()
                .title(requestDto.getTitle())
                .author(requestDto.getAuthor())
                .publisher(requestDto.getPublisher())
                .publishedDate(requestDto.getPublishedDate())
                .content(requestDto.getContent())
                .price(requestDto.getPrice()) // price는 null이 될 수 있음
                .category(requestDto.getCategory())
                .imageUrl(requestDto.getImageUrl())
                .user(user)
                .build();

        Book savedBook = bookRepository.save(book);
        log.info("Book created: {}", savedBook.getTitle());
        return new BookResponseDto(savedBook);
    }

    @Override
    public BookResponseDto getBookById(Long bookId) {
        Book book = bookRepository.findByIdAndDeletedAtIsNull(bookId)
                .orElseThrow(() -> new NoSuchElementException("해당하는 도서를 찾을 수 없습니다. Book ID: " + bookId));
        return new BookResponseDto(book);
    }

    @Override
    public List<BookResponseDto> getAllBooks() {
        List<Book> books = bookRepository.findAllByDeletedAtIsNull();
        return books.stream()
                .map(BookResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDto> getBooksByUserId(Long userId) {
        List<Book> books = bookRepository.findByUser_IdAndDeletedAtIsNull(userId);
        return books.stream()
                .map(BookResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookResponseDto updateBook(Long bookId, BookRequestDto requestDto, Long userId) {
        Book book = bookRepository.findByIdAndDeletedAtIsNull(bookId)
                .orElseThrow(() -> new NoSuchElementException("해당하는 도서를 찾을 수 없습니다. Book ID: " + bookId));

        if (!book.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("도서를 수정할 권한이 없습니다.");
        }

        book.update(
                requestDto.getTitle(),
                requestDto.getAuthor(),
                requestDto.getPublisher(),
                requestDto.getPublishedDate(),
                requestDto.getContent(),
                requestDto.getPrice(), // price는 null이 될 수 있음
                requestDto.getCategory(),
                requestDto.getImageUrl()
        );

        log.info("Book updated: {}", book.getTitle());
        return new BookResponseDto(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long bookId, Long userId) {
        Book book = bookRepository.findByIdAndDeletedAtIsNull(bookId)
                .orElseThrow(() -> new NoSuchElementException("해당하는 도서를 찾을 수 없습니다. Book ID: " + bookId));

        if (!book.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("도서를 삭제할 권한이 없습니다.");
        }

        book.markAsDeleted();
        bookRepository.save(book);
        log.info("Book soft deleted: {}", bookId);
    }
}