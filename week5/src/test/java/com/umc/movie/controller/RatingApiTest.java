package com.umc.movie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.movie.dto.CreateRatingRequest;
import com.umc.movie.entity.Member;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RatingApiTest {

    private static final long MOVIE_ID = 550L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String authorizationHeader;

    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(Member.builder()
                .email("challenger@umc.com")
                .password("encoded-password")
                .nickname("영화조아")
                .build());
        authorizationHeader = "Bearer " + jwtUtil.createAccessToken(member.getMemberId(), member.getEmail());
    }

    private String ratingRequestBody() throws Exception {
        return objectMapper.writeValueAsString(new CreateRatingRequest(MOVIE_ID, 5, "다시 봐도 재미있는 영화"));
    }

    @Test
    @DisplayName("토큰 없이 평점을 등록하면 401과 AUTH401_2를 돌려준다")
    void createRating_withoutToken_returns401() throws Exception {
        // given
        String body = ratingRequestBody();

        // when & then
        mockMvc.perform(post("/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("AUTH401_2"));
    }

    @Test
    @DisplayName("유효한 토큰으로 평점을 등록하면 201과 등록한 평점을 돌려준다")
    void createRating_withToken_returns201() throws Exception {
        // given
        String body = ratingRequestBody();

        // when & then
        mockMvc.perform(post("/ratings")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("COMMON201"))
                .andExpect(jsonPath("$.result.movieId").value(MOVIE_ID))
                .andExpect(jsonPath("$.result.score").value(5));
    }
}
