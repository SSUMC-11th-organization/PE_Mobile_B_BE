package com.umc.movie.service;

import com.umc.movie.dto.CreateFavoriteRequest;
import com.umc.movie.dto.FavoriteResponse;
import com.umc.movie.entity.Favorite;
import com.umc.movie.entity.Member;
import com.umc.movie.exception.ApiException;
import com.umc.movie.repository.FavoriteRepository;
import com.umc.movie.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    private Member fakeMember() {
        return Member.builder()
                .email("challenger@umc.com")
                .password("encoded-password")
                .nickname("영화조아")
                .build();
    }

    @Test
    @DisplayName("아직 즐겨찾기하지 않은 영화면 즐겨찾기를 저장한다")
    void create_savesFavorite_whenNotFavoritedYet() {
        // given
        Long memberId = 1L;
        CreateFavoriteRequest request = new CreateFavoriteRequest(550L);
        Member member = fakeMember();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(favoriteRepository.existsByMember_MemberIdAndMovieId(memberId, 550L)).willReturn(false);
        given(favoriteRepository.save(any(Favorite.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        FavoriteResponse response = favoriteService.create(memberId, request);

        // then
        assertThat(response.movieId()).isEqualTo(550L);

        ArgumentCaptor<Favorite> captor = ArgumentCaptor.forClass(Favorite.class);
        verify(favoriteRepository).save(captor.capture());
        assertThat(captor.getValue().getMember()).isSameAs(member);
        assertThat(captor.getValue().getMovieId()).isEqualTo(550L);
    }

    @Test
    @DisplayName("이미 즐겨찾기한 영화면 FAVORITE409_1 예외를 던지고 저장하지 않는다")
    void create_throwsConflict_whenAlreadyFavorited() {
        // given
        Long memberId = 1L;
        CreateFavoriteRequest request = new CreateFavoriteRequest(550L);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(fakeMember()));
        given(favoriteRepository.existsByMember_MemberIdAndMovieId(memberId, 550L)).willReturn(true);

        // when
        ApiException exception = catchThrowableOfType(
                ApiException.class,
                () -> favoriteService.create(memberId, request)
        );

        // then
        assertThat(exception).isNotNull();
        assertThat(exception.getCode()).isEqualTo("FAVORITE409_1");
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }
}
