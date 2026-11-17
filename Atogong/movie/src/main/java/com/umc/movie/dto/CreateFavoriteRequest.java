package com.umc.movie.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// memberId는 받지 않음. 누가 요청했는지는 토큰에서 꺼냄
public record CreateFavoriteRequest(
        @NotNull(message = "영화 ID는 필수입니다.")
        @Positive(message = "영화 ID는 양수여야 합니다.")
        Long movieId
) {
}
