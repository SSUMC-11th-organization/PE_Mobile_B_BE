package com.umc.movie.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// 평점 + (선택) 즐겨찾기를 한 번에 저장하는 요청. addToFavorites가 false면 평점만 저장
public record CreateMovieRecordRequest(
        @NotNull(message = "영화 ID는 필수입니다.")
        @Positive(message = "영화 ID는 양수여야 합니다.")
        Long movieId,

        @NotNull(message = "별점은 필수입니다.")
        @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
        @Max(value = 5, message = "별점은 5점 이하여야 합니다.")
        Integer score,

        String comment,

        @NotNull(message = "즐겨찾기 추가 여부는 필수입니다.")
        Boolean addToFavorites
) {
}
