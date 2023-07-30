package com.nts.controller;

import com.nts.domain.comment.dto.*;
import com.nts.global.Response;
import com.nts.service.CommentService;
import com.nts.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.nts.domain.comment.Constants.CommentConstants.COMMENT_PAGE_SIZE;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class CommentApiController {

    private final UserService userService;
    private final CommentService commentService;

    /**
     * 댓글 등록 API
     * 전달 받은 사용자명과 비밀번호로 사용자 검증 후 댓글 저장 로직 진행
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Response<CommentCreateResponse>> create(@RequestBody CommentCreateRequest requestDto, @PathVariable(name = "postId") Long postId) {

        Long userId = userService.validateUser(requestDto.getName(), requestDto.getPassword());

        CommentCreateResponse response = commentService.createComment(requestDto, userId, postId);

        return ResponseEntity.status(CREATED).body(Response.success(response));
    }

    /**
     * 댓글 삭제 API
     */
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Response<CommentDeleteResponse>> delete(@RequestBody CommentDeleteRequest requestDto, @PathVariable(name = "postId") Long postId, @PathVariable(name = "commentId") Long commentId) {

        CommentDeleteResponse response = commentService.deleteComment(requestDto, postId, commentId);

        return ResponseEntity.ok(Response.success(response));
    }

    /**
     * pageNumber를 쿼리 파라미터로 전달받아 해당하는 페이지의 댓글 목록 반환
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<Response<Page<CommentGetResponse>>> getPage(@PathVariable(name = "postId") Long postId, @RequestParam(defaultValue = "0") int pageNumber) {

        Page<CommentGetResponse> response = commentService.getPageComment(postId, PageRequest.of(pageNumber, COMMENT_PAGE_SIZE));

        return ResponseEntity.ok(Response.success(response));
    }


}
