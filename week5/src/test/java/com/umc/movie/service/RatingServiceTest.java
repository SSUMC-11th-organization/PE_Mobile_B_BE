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
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingService ratingService;

    private Member fakeMember(Long memberId, String email, String nickname) {
        Member member = Member.builder()
                .email(email)
                .password("encoded-password")
                .nickname(nickname)
                .build();
        ReflectionTestUtils.setField(member, "memberId", memberId);
        return member;
    }

    @Test
    @DisplayName("같은 회원이 같은 영화에 평점을 또 등록하면 RATING409_1 예외를 던지고 저장하지 않는다")
    void create_throwsConflict_whenAlreadyRated() {
        // given
        Long memberId = 1L;
        CreateRatingRequest request = new CreateRatingRequest(550L, 5, "다시 봐도 재미있는 영화");

        given(memberRepository.findById(memberId))
                .willReturn(Optional.of(fakeMember(memberId, "challenger@umc.com", "영화조아")));
        given(ratingRepository.existsByMember_MemberIdAndMovieId(memberId, 550L)).willReturn(true);

        // when
        ApiException exception = catchThrowableOfType(
                ApiException.class,
                () -> ratingService.create(memberId, request)
        );

        // then
        assertThat(exception).isNotNull();
        assertThat(exception.getCode()).isEqualTo("RATING409_1");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("다른 회원의 평점을 수정하려 하면 RATING403_1 예외를 던진다")
    void update_throwsForbidden_whenNotOwner() {
        // given
        Long ownerId = 2L;
        Long otherMemberId = 1L;
        Long ratingId = 10L;
        Rating rating = Rating.builder()
                .member(fakeMember(ownerId, "owner@umc.com", "평점주인"))
                .movieId(550L)
                .score(5)
                .comment("다시 봐도 재미있는 영화")
                .build();
        UpdateRatingRequest request = new UpdateRatingRequest(1, "남의 평점을 바꾸려는 요청");

        given(ratingRepository.findById(ratingId)).willReturn(Optional.of(rating));

        // when
        ApiException exception = catchThrowableOfType(
                ApiException.class,
                () -> ratingService.update(otherMemberId, ratingId, request)
        );

        // then
        assertThat(exception).isNotNull();
        assertThat(exception.getCode()).isEqualTo("RATING403_1");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(rating.getScore()).isEqualTo(5);
        verify(ratingRepository, never()).saveAndFlush(any(Rating.class));
    }
}
