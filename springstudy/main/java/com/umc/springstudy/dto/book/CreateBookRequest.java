package com.umc.springstudy.dto.book;

import com.umc.springstudy.entity.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBookRequest (
        @NotNull Long categoryId,
        @NotBlank @Size(max=100) String title,
        String description
) { }


