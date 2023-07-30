package com.nts.service;

import com.nts.domain.comment.dto.CommentCreateResponse;
import com.nts.domain.post.Post;
import com.nts.domain.post.PostRepository;
import com.nts.domain.postLike.PostLike;
import com.nts.domain.postLike.PostLikeRepository;
import com.nts.domain.postLike.dto.LikeOrUnlikeRequest;
import com.nts.domain.postLike.dto.LikeOrUnlikeResponse;
import com.nts.domain.user.User;
import com.nts.domain.user.UserRepository;
import com.nts.global.exception.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.nts.global.exception.ErrorCode.POST_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private User mockUser;

    @Mock
    private Post mockPost;
    @Mock
    private PostLike mockPostLike;

    @InjectMocks
    private PostLikeService postLikeService;

    String name = "name";
    String password = "password";
    Long userId = 1L;
    Long postId = 1L;
    LikeOrUnlikeRequest likeOrUnlikeRequest;

    @BeforeEach
    void setUp() {
        likeOrUnlikeRequest = LikeOrUnlikeRequest.builder()
                .name(name)
                .password(password)
                .build();
    }


    @Nested
    @DisplayName("Like, Unlike 테스트")
    class LikeOrUnlikePost {

        @Test
        @DisplayName("Like 성공")
        void likePost_success() {
            //given
            given(userRepository.getReferenceById(userId))
                    .willReturn(mockUser);
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(postLikeRepository.findByUserAndPost(mockUser, mockPost))
                    .willReturn(Optional.empty());
            given(mockPost.getId())
                    .willReturn(postId);

            //when
            LikeOrUnlikeResponse response = postLikeService.likeOrUnlikePost(userId, postId);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getPostId()).isEqualTo(postId);
            verify(userRepository, atLeastOnce()).getReferenceById(userId);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(postLikeRepository, atLeastOnce()).findByUserAndPost(mockUser, mockPost);
            verify(postLikeRepository, atLeastOnce()).save(any(PostLike.class));
            verify(postRepository, atLeastOnce()).increaseLikeCount(postId);
        }

        @Test
        @DisplayName("Unlike 성공")
        void unlikePost_success() {
            //given
            given(userRepository.getReferenceById(userId))
                    .willReturn(mockUser);
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(postLikeRepository.findByUserAndPost(mockUser, mockPost))
                    .willReturn(Optional.of(mockPostLike));
            given(mockPost.getId())
                    .willReturn(postId);

            //when
            LikeOrUnlikeResponse response = postLikeService.likeOrUnlikePost(userId, postId);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getPostId()).isEqualTo(postId);
            verify(userRepository, atLeastOnce()).getReferenceById(userId);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(postLikeRepository, atLeastOnce()).findByUserAndPost(mockUser, mockPost);
            verify(postLikeRepository, atLeastOnce()).delete(mockPostLike);
            verify(postRepository, atLeastOnce()).decreaseLikeCount(postId);
        }

        @Test
        @DisplayName("게시글이 존재하지 않는 경우 예외 발생")
        void likeOrUnlikePost_fail_notFound() {
            //given
            given(userRepository.getReferenceById(userId))
                    .willReturn(mockUser);
            given(postRepository.findById(postId))
                    .willReturn(Optional.empty());

            //when
            AppException appException = assertThrows(AppException.class, () -> postLikeService.likeOrUnlikePost(userId, postId));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);
            verify(userRepository, atLeastOnce()).getReferenceById(userId);
            verify(postRepository, atLeastOnce()).findById(postId);
        }

    }
}