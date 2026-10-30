package com.umc.movie.repository;

import com.umc.movie.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    // member.memberId 경로를 따라가는 Query Method. 최신 평점이 위로 오도록 ratingId 내림차순
    List<Rating> findByMember_MemberIdOrderByRatingIdDesc(Long memberId);
}