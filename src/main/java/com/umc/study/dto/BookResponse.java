package com.umc.study.dto;

import com.umc.study.entity.Book;

// 엔티티 대신 API로 내보낼 도서 정보
public record BookResponse(
        Long bookId,
        String title,
        String description,
        String categoryName,
        Boolean isAvailable
) {
    // LAZY 로딩된 category에 접근하므로 Service의 트랜잭션 안에서 호출해야 합니다.
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getBookId(),
                book.getTitle(),
                book.getDescription(),
                book.getCategory().getName(),
                book.getIsAvailable()
        );
    }
}
