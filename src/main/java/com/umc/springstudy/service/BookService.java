package com.umc.springstudy.service;

import com.umc.springstudy.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public List<Map<String, Object>> getAllBooks() {
        return bookRepository.findAll();
    }
    public List<Map<String, Object>> getBookByCategory(Long id) {
        return bookRepository.findByCategory(id);
    }
    public void createBook(Map<String, Object> body){
        bookRepository.save(body);
    }
}
