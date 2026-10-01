package com.umc.movielogserver.repository;

import com.umc.movielogserver.entity.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Override
    @EntityGraph(attributePaths = "category")
    List<Book> findAll();

    @EntityGraph(attributePaths = "category")
    List<Book> findByCategoryId(Long categoryId);
}
