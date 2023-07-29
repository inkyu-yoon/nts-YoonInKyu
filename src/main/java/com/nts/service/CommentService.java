package com.nts.service;

import com.nts.domain.comment.Comment;
import com.nts.domain.comment.CommentRepository;
import com.nts.domain.comment.dto.CommentCreateRequest;
import com.nts.domain.comment.dto.CommentCreateResponse;
import com.nts.domain.post.Post;
import com.nts.domain.post.PostRepository;
import com.nts.domain.user.User;
import com.nts.domain.user.UserRepository;
import com.nts.global.exception.AppException;
import com.nts.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    private final PostRepository postRepository;


    public CommentCreateResponse createComment(CommentCreateRequest requestDto, Long userId, Long postId) {

        User foundUser = userRepository.getReferenceById(userId);

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        Comment savedComment = commentRepository.save(requestDto.toEntity(foundUser, foundPost));

        return CommentCreateResponse.from(savedComment);
    }


}
