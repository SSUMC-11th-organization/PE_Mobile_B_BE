package com.umc.springstudy.controller;

import com.umc.springstudy.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public List<Map<String, Object>> getBooks(){
        return bookService.getAllBooks();
    }

    @GetMapping("/category/{categoryId}")
    public List<Map<String, Object>> getBookByCategory(@PathVariable("categoryId") Long id){
        return bookService.getBookByCategory(id);
    }

    @PostMapping
    public String createBook(@RequestBody Map<String, Object> body){
        bookService.createBook(body);
        return "도서 등록이 완료되었습니다!";
    }
}
