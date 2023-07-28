package com.nts.controller;

import com.nts.domain.post.dto.PostCreateRequest;
import com.nts.domain.post.dto.PostCreateResponse;
import com.nts.domain.post.dto.PostGetResponse;
import com.nts.global.Response;
import com.nts.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostApiController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Response<PostCreateResponse>> create(@RequestBody PostCreateRequest requestDto) {

        PostCreateResponse postResponse = postService.createPost(requestDto);

        return ResponseEntity.status(CREATED).body(Response.success(postResponse));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostGetResponse>> get(@PathVariable(name = "postId") Long postId) {

        PostGetResponse response = postService.getPost(postId);

        return ResponseEntity.ok(Response.success(response));
    }
}
