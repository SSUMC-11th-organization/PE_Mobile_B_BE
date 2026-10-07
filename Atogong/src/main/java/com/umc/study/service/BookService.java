package com.umc.study.service;

import com.umc.study.domain.Book;
import com.umc.study.domain.Category;
import com.umc.study.dto.BookResponse;
import com.umc.study.dto.CreateBookRequest;
import com.umc.study.repository.BookJdbcRepository;
import com.umc.study.repository.BookRepository;
import com.umc.study.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;           // 4주차 JPA
    private final CategoryRepository categoryRepository;   // 4주차 JPA
    private final BookJdbcRepository bookJdbcRepository;   // 3주차 Raw SQL (미션용 유지)

    @Transactional(readOnly = true)
    public List<BookResponse> getBooks() {
        return bookRepository.findAllByOrderByBookIdDesc().stream()
                .map(BookResponse::from)
                .toList();
    }

    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        Book book = new Book(category, request.title(), request.description());
        return BookResponse.from(bookRepository.save(book));
    }

    // 3주차 미션 — 아직 Raw SQL
    public List<Map<String, Object>> getBooksByCategory(Long categoryId) {
        return bookJdbcRepository.findByCategoryId(categoryId);
    }
}