package com.umc.movie.repository;

import com.umc.movie.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // 내 즐겨찾기 목록. 최근에 등록한 것이 위로 오도록 favoriteId 내림차순
    List<Favorite> findByMember_MemberIdOrderByFavoriteIdDesc(Long memberId);

    // 해제할 때 "내 것"만 찾도록 회원과 영화를 함께 조건으로 사용
    Optional<Favorite> findByMember_MemberIdAndMovieId(Long memberId, Long movieId);

    boolean existsByMember_MemberIdAndMovieId(Long memberId, Long movieId);
}
