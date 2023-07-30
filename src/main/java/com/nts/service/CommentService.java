package com.nts.service;

import com.nts.domain.comment.Comment;
import com.nts.domain.comment.CommentRepository;
import com.nts.domain.comment.dto.*;
import com.nts.domain.post.Post;
import com.nts.domain.post.PostRepository;
import com.nts.domain.user.User;
import com.nts.domain.user.UserRepository;
import com.nts.global.encrypt.PasswordEncryption;
import com.nts.global.exception.AppException;
import com.nts.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final PasswordEncryption encryption;

    /**
     * 댓글 작성
     */
    public CommentCreateResponse createComment(CommentCreateRequest requestDto, Long userId, Long postId) {

        User foundUser = userRepository.getReferenceById(userId);

        Post foundPost = validatePostById(postId);

        Comment savedComment = commentRepository.save(requestDto.toEntity(foundUser, foundPost));

        // 게시글의 comment_Count 증가
        postRepository.increaseCommentCount(postId);

        return CommentCreateResponse.from(savedComment);
    }


    /**
     * 댓글 삭제
     */
    public CommentDeleteResponse deleteComment(CommentDeleteRequest requestDto, Long postId, Long commentId) {

        validatePostById(postId);

        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        // 삭제 요청자가 댓글 작성자의 비밀번호인지 확인
        validatePassword(requestDto.getPassword(), foundComment.getUser());

        foundComment.delete();

        return CommentDeleteResponse.from(foundComment);

    }

    /**
     * 비밀번호가 일치하는지 확인
     */
    private void validatePassword(String password, User foundUser) {
        String encryptedPassword = encryption.encrypt(password);

        // 비밀번호가 일치하는지 확인
        if (!foundUser.validatePassword(encryptedPassword)) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }
    }

    /**
     * postId에 해당하는 게시글의 댓글을 날짜 최신순으로 조회
     */
    @Transactional(readOnly = true)
    public Page<CommentGetResponse> getPageComment(Long postId, Pageable pageable) {
        Post foundPost = validatePostById(postId);

        return commentRepository.findAllByPostOrderByCreatedDateDesc(foundPost, pageable)
                .map(comment -> CommentGetResponse.from(comment));
    }

    /**
     * 게시글 유효성 검증
     */
    private Post validatePostById(Long postId) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        return foundPost;
    }
}
