package com.umc.movie.dto;

// 즐겨찾기하지 않은 것도 오류가 아닌 정상 상태라 200 + isFavorite: false로 응답
public record FavoriteStatusResponse(
        boolean isFavorite
) {
}
