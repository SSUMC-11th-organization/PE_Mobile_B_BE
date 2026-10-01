package com.umc.movielogserver.dto;

import com.umc.movielogserver.entity.Book;

public record BookResponse(
        Long bookId,
        Long categoryId,
        String categoryName,
        String title,
        String description,
        boolean available
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getCategory().getId(),
                book.getCategory().getName(),
                book.getTitle(),
                book.getDescription(),
                book.isAvailable()
        );
    }
}
