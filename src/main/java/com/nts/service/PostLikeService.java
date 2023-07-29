package com.nts.service;

import com.nts.domain.post.Post;
import com.nts.domain.post.PostRepository;
import com.nts.domain.postLike.PostLike;
import com.nts.domain.postLike.PostLikeRepository;
import com.nts.domain.postLike.dto.LikeOrUnlikeResponse;
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
public class PostLikeService {

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final PostLikeRepository postLikeRepository;


    public LikeOrUnlikeResponse likeOrUnlikePost(Long userId, Long postId) {

        User foundUser = userRepository.getReferenceById(userId);

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        // 좋아요를 누른 이력이 있는 경우 UnLike, 이력이 없는 경우 Like
        postLikeRepository.findByUserAndPost(foundUser, foundPost)
                .ifPresentOrElse(
                        postLike -> {
                            postLikeRepository.delete(postLike);
                            postRepository.decreaseLikeCount(postId);
                        },

                        () -> {
                            postLikeRepository.save(PostLike.of(foundUser, foundPost));
                            postRepository.increaseLikeCount(postId);
                        }
                );

        return LikeOrUnlikeResponse.from(foundPost);
    }
}
