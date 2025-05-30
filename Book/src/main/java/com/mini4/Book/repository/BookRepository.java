package com.mini4.Book.repository;

import com.mini4.Book.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // 특정 ID의 도서를 조회하되, deletedAt이 NULL인 경우에만 (Soft Delete 미포함)
    Optional<Book> findByIdAndDeletedAtIsNull(Long id);

    // 모든 도서 목록을 조회하되, deletedAt이 NULL인 경우에만 (Soft Delete 미포함)
    List<Book> findAllByDeletedAtIsNull();

    // 특정 사용자가 등록한 도서 목록을 조회하되, deletedAt이 NULL인 경우에만 (Soft Delete 미포함)
    List<Book> findByUser_IdAndDeletedAtIsNull(Long userId); // 페이징 제거

    // 제목 또는 저자를 기준으로 검색하되, deletedAt이 NULL인 경우에만 (Soft Delete 미포함)
    List<Book> findByTitleContainingOrAuthorContainingAndDeletedAtIsNull(String title, String author);
}