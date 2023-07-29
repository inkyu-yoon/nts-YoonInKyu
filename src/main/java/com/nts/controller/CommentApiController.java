package com.nts.controller;

import com.nts.domain.comment.dto.CommentCreateRequest;
import com.nts.domain.comment.dto.CommentCreateResponse;
import com.nts.global.Response;
import com.nts.service.CommentService;
import com.nts.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class CommentApiController {

    private final UserService userService;
    private final CommentService commentService;

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Response<CommentCreateResponse>> create(@RequestBody CommentCreateRequest requestDto, @PathVariable(name = "postId") Long postId) {

        Long userId = userService.validateUser(requestDto.getName(), requestDto.getPassword());

        CommentCreateResponse response = commentService.createComment(requestDto, userId, postId);

        return ResponseEntity.status(CREATED).body(Response.success(response));
    }


}
