// src/main/java/.../repository/BookRepository.java
package com.umc.study.repository;

import com.umc.study.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// JpaRepository를 상속하면 스프링이 구현체를 자동으로 만들어 빈으로 등록합니다.
public interface BookRepository extends JpaRepository<Book, Long> {

    // 메서드 이름으로 쿼리 생성: ORDER BY book_id DESC
    List<Book> findAllByOrderByBookIdDesc();

    // Book.category.categoryId 기준 조회: WHERE category_id = ?
    List<Book> findByCategory_CategoryId(Long categoryId);
}
