package com.umc.movie.controller;

import com.umc.movie.dto.ApiResponse;
import com.umc.movie.dto.MemberResponse;
import com.umc.movie.dto.EmailCheckResponse;
import com.umc.movie.dto.NickNameCheckResponse;
import com.umc.movie.dto.SignUpRequest;
import com.umc.movie.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "회원 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "닉네임 중복 체크")
    @GetMapping("/nickname/{nickname}")
    public NickNameCheckResponse checkNickname(
            @Parameter(description = "확인할 닉네임", example = "noco")
            @PathVariable String nickname)
    {
        return memberService.checkNickname(nickname);
    }

    @Operation(summary = "이메일 중복 체크")
    @GetMapping("/email/{email}")
    public EmailCheckResponse checkEmail(
            @Parameter(description = "확인할 이메일", example = "test@example.com")
            @PathVariable String email)
    {
        return memberService.checkEmail(email);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        MemberResponse response = memberService.signUp(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.created("회원가입에 성공했습니다.", response));
    }
}
