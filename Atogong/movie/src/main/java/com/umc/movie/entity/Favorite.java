package com.umc.movie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 8주차: 5주차에 SQL로 만들어 둔 favorite 테이블과 연결.
// (member_id, movie_id)를 한 쌍으로 UNIQUE → 같은 회원이 같은 영화를 두 번 즐겨찾기할 수 없음
@Entity
@Table(
        name = "favorite",
        uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "movie_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Favorite extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long favoriteId;

    // 즐겨찾기의 주인은 로그인한 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // TMDB의 영화 ID를 그대로 저장. 우리 DB에 movie 테이블이 없으므로 외래 키가 아님
    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Builder
    public Favorite(Member member, Long movieId) {
        this.member = member;
        this.movieId = movieId;
    }
}
