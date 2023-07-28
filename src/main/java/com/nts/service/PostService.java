package com.nts.service;

import com.nts.domain.post.Post;
import com.nts.domain.post.PostRepository;
import com.nts.domain.post.dto.PostCreateRequest;
import com.nts.domain.post.dto.PostCreateResponse;
import com.nts.domain.post.dto.PostGetResponse;
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

        String encryptedPassword = encryption.encrypt(requestDto.getPassword());

        // 비밀번호가 일치하는지 확인
        if (!foundUser.validatePassword(encryptedPassword)) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

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
}
