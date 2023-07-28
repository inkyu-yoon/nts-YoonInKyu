package com.nts.service;

import com.nts.domain.post.Post;
import com.nts.domain.post.PostRepository;
import com.nts.domain.post.dto.*;
import com.nts.domain.user.User;
import com.nts.domain.user.UserRepository;
import com.nts.global.encrypt.PasswordEncryption;
import com.nts.global.exception.AppException;
import com.nts.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PasswordEncryption encryption;

    /**
     * 게시글 작성
     */
    public PostCreateResponse createPost(PostCreateRequest requestDto) {

        // 게시글 작성 요청 사용자 검증
        User foundUser = findAndValidateUser(requestDto);

        //게시글 저장
        Post savedPost = postRepository.save(requestDto.toEntity(foundUser));

        return PostCreateResponse.from(savedPost);
    }

    /**
     * 사용자명이 등록된 사용자인지 확인합니다.
     * 비밀번호가 일치하는지 확인합니다.
     */
    private User findAndValidateUser(PostCreateRequest requestDto) {

        // 등록된 사용자인지 확인
        User foundUser = userRepository.findUserByName(requestDto.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 일치하는지 확인
        validatePassword(requestDto.getPassword(), foundUser);

        return foundUser;
    }

    /**
     * 게시글 단건 상세 조회
     * 단건 조회 요청 시 조회수 증가
     */
    public PostGetResponse getPost(Long postId) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        // @Modifying 이용해서 동시성 처리
        postRepository.increaseViewCount(postId);

        return PostGetResponse.from(foundPost);
    }

    /**
     * 게시글 수정
     */
    public PostUpdateResponse updatePost(PostUpdateRequest requestDto, Long postId) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        // 수정 요청자가 게시글 작성자의 비밀번호인지 확인
        validatePassword(requestDto.getPassword(), foundPost.getUser());

        foundPost.update(requestDto.getTitle(), requestDto.getBody());

        return PostUpdateResponse.from(foundPost);
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
}
