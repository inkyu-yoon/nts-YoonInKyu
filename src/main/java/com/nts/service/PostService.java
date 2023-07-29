package com.nts.service;

import com.nts.domain.hashtag.Hashtag;
import com.nts.domain.hashtag.HashtagRepository;
import com.nts.domain.post.Post;
import com.nts.domain.post.PostRepository;
import com.nts.domain.post.dto.*;
import com.nts.domain.postHashtag.PostHashtag;
import com.nts.domain.postHashtag.PostHashtagRepository;
import com.nts.domain.user.User;
import com.nts.domain.user.UserRepository;
import com.nts.global.encrypt.PasswordEncryption;
import com.nts.global.exception.AppException;
import com.nts.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final PasswordEncryption encryption;

    /**
     * 게시글 작성
     */
    public PostCreateResponse createPost(PostCreateRequest requestDto, Long userId) {

        User foundUser = userRepository.getReferenceById(userId);

        //게시글 저장
        Post savedPost = postRepository.save(requestDto.toEntity(foundUser));

        List<String> hashtags = requestDto.getHashtags();

        // 해시태그의 수는 최대 5개
        validateHashtagsSize(hashtags);

        saveNewHashtagsAndLinkToPost(savedPost, hashtags);

        return PostCreateResponse.from(savedPost);
    }

    /**
     * 새로운 해시태그 등록 및 게시글과 연결
     */
    private void saveNewHashtagsAndLinkToPost(Post savedPost, List<String> hashtags) {
        // 이미 존재하는 해시태그 추출
        List<Hashtag> existingHashtags = hashtagRepository.findHashtagByNameIn(hashtags);

        // 새롭게 등록(저장)해야하는 해시태그 추출
        List<Hashtag> newHashtags = hashtags.stream()
                .filter(name -> existingHashtags.stream().noneMatch(hashtag -> hashtag.getName().equals(name)))
                .map(name -> Hashtag.of(name))
                .collect(Collectors.toList());

        // 새로운 해시태그 등록
        hashtagRepository.saveAll(newHashtags);

        // 해시태그와 게시글 연결
        List<PostHashtag> postHashtags = Stream.concat(existingHashtags.stream(), newHashtags.stream())
                .map(hashtag -> PostHashtag.of(hashtag, savedPost))
                .collect(Collectors.toList());

        postHashtagRepository.saveAll(postHashtags);
    }

    /**
     * 해시태그 개수가 5개를 초과하는 경우 예외 처리
     */
    private void validateHashtagsSize(List<String> hashtags) {
        if (hashtags.size() > 5) {
            throw new AppException(ErrorCode.EXCEED_HASHTAG_SIZE);
        }
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

        List<String> hashtagNames = postHashtagRepository.getHashtagNamesByPostId(postId);

        return PostGetResponse.from(foundPost, hashtagNames);
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

        List<String> hashtags = requestDto.getHashtags();

        // 해시태그의 수는 최대 5개
        validateHashtagsSize(hashtags);

        // 게시글 수정 시, 해시태그도 변경이 있을 수 있으므로 재등록하기위해 초기화
        postHashtagRepository.deleteAllByPost(foundPost);

        saveNewHashtagsAndLinkToPost(foundPost, hashtags);

        return PostUpdateResponse.from(foundPost);
    }

    /**
     * 게시글 삭제
     */
    public PostDeleteResponse deletePost(PostDeleteRequest requestDto, Long postId) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        // 삭제 요청자가 게시글 작성자의 비밀번호인지 확인
        validatePassword(requestDto.getPassword(), foundPost.getUser());

        postRepository.delete(foundPost);

        return PostDeleteResponse.from(foundPost);
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
