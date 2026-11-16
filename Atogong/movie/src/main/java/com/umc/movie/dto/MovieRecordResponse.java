package com.umc.movie.dto;

// addToFavorites가 false였다면 favorite은 null
public record MovieRecordResponse(
        RatingResponse rating,
        FavoriteResponse favorite
) {
}
