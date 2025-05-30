package com.mini4.Book.controller;

import com.mini4.Book.dto.ApiResponse;
import com.mini4.Book.dto.BookListResponseDto;
import com.mini4.Book.dto.BookRequestDto;
import com.mini4.Book.dto.BookResponseDto;
import com.mini4.Book.security.CustomUserDetails;
import com.mini4.Book.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    // 도서 등록 API
    @PostMapping
    public ResponseEntity<ApiResponse<BookResponseDto>> createBook(
            @Valid @RequestBody BookRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            if (customUserDetails == null) {
                return new ResponseEntity<>(
                        ApiResponse.error("로그인이 필요합니다."),
                        HttpStatus.UNAUTHORIZED // 401 Unauthorized
                );
            }
            log.debug("User {} creating book: {}", customUserDetails.getUsername(), requestDto.getTitle());
            BookResponseDto responseDto = bookService.createBook(requestDto, customUserDetails.getUserId());
            return new ResponseEntity<>(
                    ApiResponse.success("도서가 성공적으로 등록되었습니다.", responseDto),
                    HttpStatus.CREATED // 201 Created
            );
        } catch (NoSuchElementException e) {
            log.error("Book creation failed: User not found. Error: {}", e.getMessage());
            return new ResponseEntity<>(
                    ApiResponse.error("사용자를 찾을 수 없습니다."),
                    HttpStatus.NOT_FOUND // 404 Not Found
            );
        } catch (Exception e) {
            log.error("도서 등록 중 오류가 발생했습니다.", e);
            return new ResponseEntity<>(
                    ApiResponse.error("도서 등록 중 오류가 발생했습니다."),
                    HttpStatus.INTERNAL_SERVER_ERROR // 500 Internal Server Error
            );
        }
    }

    // 단일 도서 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponseDto>> getBookById(@PathVariable Long id) {
        try {
            BookResponseDto responseDto = bookService.getBookById(id);
            return new ResponseEntity<>(
                    ApiResponse.success("도서가 성공적으로 조회되었습니다.", responseDto),
                    HttpStatus.OK // 200 OK
            );
        } catch (NoSuchElementException e) {
            log.warn("Book not found for ID: {}. Error: {}", id, e.getMessage());
            return new ResponseEntity<>(
                    ApiResponse.error("해당하는 도서를 찾을 수 없습니다."),
                    HttpStatus.NOT_FOUND // 404 Not Found
            );
        } catch (Exception e) {
            log.error("도서 조회 중 오류가 발생했습니다.", e);
            return new ResponseEntity<>(
                    ApiResponse.error("도서 조회 중 오류가 발생했습니다."),
                    HttpStatus.INTERNAL_SERVER_ERROR // 500 Internal Server Error
            );
        }
    }

    // 모든 도서 목록 조회 API (페이징 제거)
    @GetMapping
    public ResponseEntity<ApiResponse<BookListResponseDto>> getAllBooks() { // 파라미터 제거
        try {
            List<BookResponseDto> books = bookService.getAllBooks();
            // BookListResponseDto 생성자가 List<BookResponseDto>를 받도록 수정되었으므로, 직접 전달
            return new ResponseEntity<>(
                    ApiResponse.success("도서 목록이 성공적으로 조회되었습니다.", new BookListResponseDto(books)),
                    HttpStatus.OK // 200 OK
            );
        } catch (Exception e) {
            log.error("도서 목록 조회 중 오류가 발생했습니다.", e);
            return new ResponseEntity<>(
                    ApiResponse.error("도서 목록 조회 중 오류가 발생했습니다."),
                    HttpStatus.INTERNAL_SERVER_ERROR // 500 Internal Server Error
            );
        }
    }

    // 내가 등록한 도서 목록 조회 API (페이징 제거)
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<BookListResponseDto>> getMyBooks(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) { // 파라미터 제거
        try {
            if (customUserDetails == null) {
                return new ResponseEntity<>(
                        ApiResponse.error("로그인이 필요합니다."),
                        HttpStatus.UNAUTHORIZED
                );
            }
            List<BookResponseDto> books = bookService.getBooksByUserId(customUserDetails.getUserId());
            // BookListResponseDto 생성자가 List<BookResponseDto>를 받도록 수정되었으므로, 직접 전달
            return new ResponseEntity<>(
                    ApiResponse.success("내 도서 목록이 성공적으로 조회되었습니다.", new BookListResponseDto(books)),
                    HttpStatus.OK
            );
        } catch (NoSuchElementException e) {
            log.warn("User not found for my books. Error: {}", e.getMessage());
            return new ResponseEntity<>(
                    ApiResponse.error("사용자를 찾을 수 없습니다."),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            log.error("내 도서 목록 조회 중 오류가 발생했습니다.", e);
            return new ResponseEntity<>(
                    ApiResponse.error("내 도서 목록 조회 중 오류가 발생했습니다."),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // 도서 정보 수정 API
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponseDto>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            if (customUserDetails == null) {
                return new ResponseEntity<>(
                        ApiResponse.error("로그인이 필요합니다."),
                        HttpStatus.UNAUTHORIZED
                );
            }
            log.debug("User {} updating book ID {}: {}", customUserDetails.getUsername(), id, requestDto.getTitle());
            BookResponseDto responseDto = bookService.updateBook(id, requestDto, customUserDetails.getUserId());
            return new ResponseEntity<>(
                    ApiResponse.success("도서가 성공적으로 수정되었습니다.", responseDto),
                    HttpStatus.OK
            );
        } catch (NoSuchElementException e) {
            log.warn("Book not found for update, ID: {}. Error: {}", id, e.getMessage());
            return new ResponseEntity<>(
                    ApiResponse.error("해당하는 도서를 찾을 수 없습니다."),
                    HttpStatus.NOT_FOUND
            );
        } catch (AccessDeniedException e) {
            log.warn("User {} denied access to update book ID {}. Error: {}", customUserDetails.getUsername(), id, e.getMessage());
            return new ResponseEntity<>(
                    ApiResponse.error("도서를 수정할 권한이 없습니다."),
                    HttpStatus.FORBIDDEN // 403 Forbidden
            );
        } catch (Exception e) {
            log.error("도서 수정 중 오류가 발생했습니다.", e);
            return new ResponseEntity<>(
                    ApiResponse.error("도서 수정 중 오류가 발생했습니다."),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // 도서 삭제 API (Soft Delete 방식으로 변경)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            if (customUserDetails == null) {
                return new ResponseEntity<>(
                        ApiResponse.error("로그인이 필요합니다."),
                        HttpStatus.UNAUTHORIZED
                );
            }
            log.debug("User {} deleting book ID: {}", customUserDetails.getUsername(), id);
            bookService.deleteBook(id, customUserDetails.getUserId());
            return new ResponseEntity<>(
                    ApiResponse.success("도서가 성공적으로 삭제되었습니다."),
                    HttpStatus.NO_CONTENT // 204 No Content
            );
        } catch (NoSuchElementException e) {
            log.warn("Book not found for delete, ID: {}. Error: {}", id, e.getMessage());
            return new ResponseEntity<>(
                    ApiResponse.error("해당하는 도서를 찾을 수 없습니다."),
                    HttpStatus.NOT_FOUND
            );
        } catch (AccessDeniedException e) {
            log.warn("User {} denied access to delete book ID {}. Error: {}", customUserDetails.getUsername(), id, e.getMessage());
            return new ResponseEntity<>(
                    ApiResponse.error("도서를 삭제할 권한이 없습니다."),
                    HttpStatus.FORBIDDEN // 403 Forbidden
            );
        } catch (Exception e) {
            log.error("도서 삭제 중 오류가 발생했습니다.", e);
            return new ResponseEntity<>(
                    ApiResponse.error("도서 삭제 중 오류가 발생했습니다."),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
