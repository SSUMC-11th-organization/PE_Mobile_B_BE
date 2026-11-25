package com.umc.movie.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 9주차: 문서의 표지(제목·설명)와 인증 방식을 등록. API 목록 자체는 springdoc이 컨트롤러·DTO를 읽어 자동으로 만듦
@Configuration
public class SwaggerConfig {

    // 각 API에 @SecurityRequirement(name = "bearerAuth")로 연결할 때 쓰는 이름
    public static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI movieOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("영화 평점 서비스 API")
                        .description("UMC 11기 백엔드 워크북에서 만든 회원/평점/즐겨찾기 API 문서입니다.")
                        .version("v1"))
                // 자물쇠 버튼(Authorize)에 쓸 인증 방식 등록.
                // 여기서는 "이런 인증 방식이 있다"고 선언만 하고, 어떤 API에 필요한지는 각 컨트롤러에서 지정
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP) // Authorization 헤더를 사용하는 방식
                                .scheme("bearer")               // "Bearer {토큰}" 형태
                                .bearerFormat("JWT")));         // 문서에 표시할 토큰 종류
    }
}
