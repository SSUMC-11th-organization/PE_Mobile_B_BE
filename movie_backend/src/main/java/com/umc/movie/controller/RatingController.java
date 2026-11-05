// src/main/java/com/umc/movie/controller/RatingController.java
package com.umc.movie.controller;

import com.umc.movie.dto.ApiResponse;
import com.umc.movie.dto.CreateRatingRequest;
import com.umc.movie.dto.RatingResponse;
import com.umc.movie.dto.UpdateRatingRequest;
import com.umc.movie.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Rating", description = "평점 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class RatingController {

    private final RatingService ratingService;

    @Operation(summary = "유저별 평점 조회")
    @GetMapping("/{memberId}/ratings")
    public List<RatingResponse> getMemberRatings(
            @Parameter(description = "확인할 유저 ID", example = "1")
            @PathVariable Long memberId
    ) {
        return ratingService.getRatingsByMember(memberId);
    }

    @Operation(summary = "평점 등록", description = "특정 영화에 대한 회원의 별점과 후기를 등록합니다.")
    @PostMapping("/{memberId}/ratings")
    public ResponseEntity<ApiResponse<Long>> create(
            @Parameter(description = "평점을 작성하는 회원 ID", example = "1")
            @PathVariable Long memberId,

            @Valid @RequestBody CreateRatingRequest request
    ) {
        Long ratingId = ratingService.create(memberId, request);
        return ResponseEntity.status(201).body(ApiResponse.created("평점 등록에 성공했습니다.", ratingId));
    }

    @Operation(summary = "평점 수정", description = "본인이 작성한 평점의 별점과 후기를 수정합니다.")
    @PatchMapping("/{memberId}/ratings/{ratingId}")
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "요청한 회원 ID", example = "1")
            @PathVariable Long memberId,

            @Parameter(description = "수정할 평점 ID", example = "1")
            @PathVariable Long ratingId,

            @Valid @RequestBody UpdateRatingRequest request
    ) {
        ratingService.update(ratingId, memberId, request);
        return ResponseEntity.ok(ApiResponse.ok("평점 수정에 성공했습니다.", null));
    }

    @Operation(summary = "평점 삭제", description = "본인이 작성한 평점을 삭제합니다.")
    @DeleteMapping("/{memberId}/ratings/{ratingId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "요청한 회원 ID", example = "1")
            @PathVariable Long memberId,

            @Parameter(description = "삭제할 평점 ID", example = "1")
            @PathVariable Long ratingId
    ){
        ratingService.delete(memberId, ratingId);
        return ResponseEntity.noContent().build();
    }
}
