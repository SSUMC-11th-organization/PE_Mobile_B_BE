package com.umc.movie.service;

import com.umc.movie.dto.CreateRatingRequest;
import com.umc.movie.dto.UpdateRatingRequest;
import com.umc.movie.entity.Member;
import com.umc.movie.entity.Rating;
import com.umc.movie.exception.ApiException;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.repository.RatingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

// 9주차 미션 단위 테스트: DB 없이 RatingService의 판단(중복 등록, 남의 평점 수정)만 검증
@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    @DisplayName("같은 영화에 평점을 다시 등록하면 RATING409_1 예외를 던지고 저장하지 않는다")
    void createRatingWhenDuplicated() {
        // given: 회원은 존재하고, 이 영화에 이미 평점을 남겨 둔 상황
        Member member = memberWithId(1L);
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(ratingRepository.existsByMember_MemberIdAndMovieId(1L, 550L)).willReturn(true);

        // when & then: RATING409_1 예외가 나고, 저장은 시도조차 하지 않는다
        assertThatThrownBy(() -> ratingService.create(1L, new CreateRatingRequest(550L, 5, "다시 봐도 좋아요")))
                .isInstanceOfSatisfying(ApiException.class,
                        exception -> assertThat(exception.getCode()).isEqualTo("RATING409_1"));

        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("남이 작성한 평점을 수정하려 하면 RATING403_1 예외를 던지고 평점은 그대로다")
    void updateRatingOfAnotherMember() {
        // given: 10번 평점의 주인은 2번 회원
        Member owner = memberWithId(2L);
        Rating rating = Rating.builder().member(owner).movieId(550L).score(5).comment("원래 후기").build();
        given(ratingRepository.findById(10L)).willReturn(Optional.of(rating));

        // when & then: 1번 회원이 수정하려 하면 RATING403_1 예외가 나고
        assertThatThrownBy(() -> ratingService.update(1L, 10L, new UpdateRatingRequest(1, "남이 바꾼 후기")))
                .isInstanceOfSatisfying(ApiException.class,
                        exception -> assertThat(exception.getCode()).isEqualTo("RATING403_1"));

        // 별점과 후기는 바뀌지 않고, 저장도 호출되지 않는다
        assertThat(rating.getScore()).isEqualTo(5);
        assertThat(rating.getComment()).isEqualTo("원래 후기");
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    // memberId는 DB가 채워 주는 값이라 Builder로는 넣을 수 없어서, 테스트에서만 직접 넣어 줌
    private Member memberWithId(Long memberId) {
        Member member = Member.builder()
                .email("member" + memberId + "@umc.com")
                .password("암호화된비밀번호")
                .nickname("회원" + memberId)
                .build();
        ReflectionTestUtils.setField(member, "memberId", memberId);
        return member;
    }
}
