package com.umc.movie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.movie.dto.CreateFavoriteRequest;
import com.umc.movie.entity.Favorite;
import com.umc.movie.entity.Member;
import com.umc.movie.repository.FavoriteRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FavoriteApiTest {

    private static final long MOVIE_ID = 550L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private Member member;
    private String authorizationHeader;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.builder()
                .email("challenger@umc.com")
                .password("encoded-password")
                .nickname("영화조아")
                .build());
        authorizationHeader = "Bearer " + jwtUtil.createAccessToken(member.getMemberId(), member.getEmail());
    }

    private String favoriteRequestBody() throws Exception {
        return objectMapper.writeValueAsString(new CreateFavoriteRequest(MOVIE_ID));
    }

    @Test
    @DisplayName("토큰 없이 즐겨찾기를 등록하면 401과 AUTH401_2를 돌려준다")
    void createFavorite_withoutToken_returns401() throws Exception {
        // given
        String body = favoriteRequestBody();

        // when & then
        mockMvc.perform(post("/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("AUTH401_2"));
    }

    @Test
    @DisplayName("토큰을 담아 즐겨찾기를 등록하면 201과 등록한 영화 ID를 돌려준다")
    void createFavorite_withToken_returns201() throws Exception {
        // given
        String body = favoriteRequestBody();

        // when & then
        mockMvc.perform(post("/favorites")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("COMMON201"))
                .andExpect(jsonPath("$.result.movieId").value(MOVIE_ID));
    }

    @Test
    @DisplayName("이미 즐겨찾기한 영화를 다시 등록하면 409와 FAVORITE409_1을 돌려준다")
    void createFavorite_duplicate_returns409() throws Exception {
        // given
        favoriteRepository.save(Favorite.builder()
                .member(member)
                .movieId(MOVIE_ID)
                .build());
        String body = favoriteRequestBody();

        // when & then
        mockMvc.perform(post("/favorites")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("FAVORITE409_1"));
    }

    @Test
    @DisplayName("즐겨찾기하지 않은 영화의 즐겨찾기 여부를 조회하면 false를 돌려준다")
    void getFavoriteStatus_notFavorited_returnsFalse() throws Exception {
        // given: 즐겨찾기를 저장하지 않은 상태

        // when & then
        mockMvc.perform(get("/favorites/{movieId}", MOVIE_ID)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.isFavorite").value(false));
    }

    @Test
    @DisplayName("토큰 없이 OpenAPI 문서를 조회할 수 있다")
    void apiDocs_withoutToken_returns200() throws Exception {
        // given: 토큰 없음

        // when & then
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }
}
