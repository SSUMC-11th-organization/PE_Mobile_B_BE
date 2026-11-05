package com.umc.movie.repository;

import com.umc.movie.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    // member.memberId 경로를 따라가는 Query Method. 최신 평점이 위로 오도록 ratingId 내림차순
    List<Rating> findByMember_MemberIdOrderByRatingIdDesc(Long memberId);
    // 언더스코어는 연관 엔티티(member)의 memberId를 조건으로 쓴다는 뜻
    boolean existsByMember_MemberIdAndMovieId(Long memberId, Long movieId);
}