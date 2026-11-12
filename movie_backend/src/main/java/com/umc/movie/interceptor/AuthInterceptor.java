package com.umc.movie.interceptor;

import com.umc.movie.exception.ApiException;
import com.umc.movie.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    public static final String MEMBER_ID = "memberId";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        //TODO: CORS 사전 요청 통과 -> 근대 왜 조건을 저렇게 잡았을까?
        if (HttpMethod.OPTIONS.matches(request.getMethod())){
            return true;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // "Bearer {토큰}" 형식 필터링
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "AUTH401_2",
                    "로그인이 필요한 요청입니다.");
        }
        // 여기까지 온거면 유효한 토큰 -> 토큰으로 memberId 구하기
        Long memberId = jwtUtil.getMemberId(header.substring(BEARER_PREFIX.length()));
        //뒤이어 실행할 컨트롤러 쓸 수 있게 request에 memberId 보관
        request.setAttribute(MEMBER_ID,memberId);

        return true;
    }
}
