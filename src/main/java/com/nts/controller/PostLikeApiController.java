package com.nts.controller;

import com.nts.domain.postLike.dto.LikeOrUnlikeRequest;
import com.nts.domain.postLike.dto.LikeOrUnlikeResponse;
import com.nts.global.Response;
import com.nts.service.PostLikeService;
import com.nts.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostLikeApiController {

    private final UserService userService;
    private final PostLikeService postLikeService;

    /**
     * 게시글에 좋아요를 입력하거나 취소하는 API
     * 전달 받은 사용자명과 비밀번호로 사용자 검증 후 로직 진행
     */
    @PostMapping("/{postId}/likes")
    public ResponseEntity<Response<LikeOrUnlikeResponse>> likeOrUnlike(@PathVariable(name = "postId") Long postId, @RequestBody LikeOrUnlikeRequest requestDto) {
        Long userId = userService.validateUser(requestDto.getName(), requestDto.getPassword());

        LikeOrUnlikeResponse response = postLikeService.likeOrUnlikePost(userId, postId);

        return ResponseEntity.ok(Response.success(response));
    }
}
