package com.umc.movie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.movie.dto.CreateMovieRecordRequest;
import com.umc.movie.entity.Favorite;
import com.umc.movie.entity.Member;
import com.umc.movie.repository.FavoriteRepository;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.repository.RatingRepository;
import com.umc.movie.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 서비스 트랜잭션 롤백을 증명하려고 일부러 @Transactional을 붙이지 않는다.
 * 대신 @AfterEach에서 이 테스트가 만든 데이터를 직접 지운다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class MovieRecordApiTest {

    private static final long MOVIE_ID = 550L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @MockitoSpyBean
    private FavoriteRepository favoriteRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private Long memberId;
    private String authorizationHeader;

    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(Member.builder()
                .email("record-challenger@umc.com")
                .password("encoded-password")
                .nickname("기록왕")
                .build());
        memberId = member.getMemberId();
        authorizationHeader = "Bearer " + jwtUtil.createAccessToken(memberId, member.getEmail());
    }

    @AfterEach
    void tearDown() {
        // FK 순서: rating(member_id 참조) -> member
        ratingRepository.deleteAll(ratingRepository.findByMember_MemberIdOrderByRatingIdDesc(memberId));
        memberRepository.deleteById(memberId);
    }

    @Test
    @DisplayName("평점 저장 뒤 즐겨찾기 저장에서 실패하면 409를 돌려주고 평점 저장도 롤백된다")
    void createMovieRecord_favoriteSaveFails_rollsBackRating() throws Exception {
        // given: 사전 중복 검사는 실제 DB 조회(false)로 통과하고, 즐겨찾기 저장만 실패하게 한다
        doThrow(new DataIntegrityViolationException("테스트용 즐겨찾기 저장 실패"))
                .when(favoriteRepository).save(any(Favorite.class));
        String body = objectMapper.writeValueAsString(
                new CreateMovieRecordRequest(MOVIE_ID, 5, "다시 봐도 재미있는 영화", true));
        long ratingCountBefore = ratingRepository.count();

        // when & then: GlobalExceptionHandler가 DataIntegrityViolationException을 409 COMMON4091로 바꾼다
        mockMvc.perform(post("/movie-records")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("COMMON4091"))
                .andExpect(jsonPath("$.message").value("이미 존재하는 데이터입니다."));

        // then: 평점 저장 다음 단계인 즐겨찾기 저장까지 실제로 갔고, 평점은 롤백되어 남지 않았다
        verify(favoriteRepository).save(any(Favorite.class));
        assertThat(ratingRepository.count()).isEqualTo(ratingCountBefore);
        assertThat(ratingRepository.existsByMember_MemberIdAndMovieId(memberId, MOVIE_ID)).isFalse();
    }
}
