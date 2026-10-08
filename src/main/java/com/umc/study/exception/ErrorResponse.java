package com.umc.study.exception;

// 오류 응답 형식: { "status": 400, "message": "제목은 비어 있을 수 없습니다." }
public record ErrorResponse(
        int status,
        String message
) {}
