package com.nts.controller;

import com.nts.domain.post.dto.*;
import com.nts.global.Response;
import com.nts.service.PostService;
import com.nts.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostApiController {

    private final PostService postService;
    private final UserService userService;

    /**
     * 게시글 작성 API
     * 전달 받은 사용자명과 비밀번호로 사용자 검증 후 게시글 저장 로직 진행
     */
    @PostMapping
    public ResponseEntity<Response<PostCreateResponse>> create(@RequestBody PostCreateRequest requestDto) {
        // 게시글 등록 요청한 사용자 검증
        Long userId = userService.validateUser(requestDto.getName(), requestDto.getPassword());

        PostCreateResponse postResponse = postService.createPost(requestDto, userId);

        return ResponseEntity.status(CREATED).body(Response.success(postResponse));
    }

    /**
     * 게시글 단건 조회 API
     */
    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostGetResponse>> get(@PathVariable(name = "postId") Long postId) {

        PostGetResponse response = postService.getPost(postId);

        return ResponseEntity.ok(Response.success(response));
    }

    /**
     * 게시글 수정 API
     */
    @PutMapping("/{postId}")
    public ResponseEntity<Response<PostUpdateResponse>> update(@RequestBody PostUpdateRequest requestDto, @PathVariable(name = "postId") Long postId) {

        PostUpdateResponse response = postService.updatePost(requestDto, postId);

        return ResponseEntity.ok(Response.success(response));
    }

    /**
     * 게시글 삭제 API
     */

    @DeleteMapping("/{postId}")
    public ResponseEntity<Response<PostDeleteResponse>> delete(@RequestBody PostDeleteRequest requestDto, @PathVariable(name = "postId") Long postId) {

        PostDeleteResponse response = postService.deletePost(requestDto, postId);

        return ResponseEntity.ok(Response.success(response));
    }

    /**
     * 게시글 페이지 단위 조회 API
     * 날짜를 기준으로 최신순으로 정렬하여 20개씩 페이징됩니다.
     */
    @GetMapping
    public ResponseEntity<Response<Page<PostGetPageResponse>>> getPage(@PageableDefault(size = 20, sort ="createdDate" , direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostGetPageResponse> response = postService.getPostPage(pageable);

        return ResponseEntity.ok(Response.success(response));
    }

    /**
     * 등록된 게시글 갯수와 전체 댓글 갯수 조회 API
     */
    @GetMapping("/count")
    public ResponseEntity<Response<PostDataGetResponse>> getTotal() {

        PostDataGetResponse response = postService.getTotalPostAndCommentCount();

        return ResponseEntity.ok(Response.success(response));
    }
}
