package com.nts.service;

import com.nts.domain.hashtag.Hashtag;
import com.nts.domain.hashtag.HashtagRepository;
import com.nts.domain.post.Post;
import com.nts.domain.post.PostRepository;
import com.nts.domain.post.dto.*;
import com.nts.domain.postHashtag.PostHashtagRepository;
import com.nts.domain.user.User;
import com.nts.domain.user.UserRepository;
import com.nts.global.encrypt.PasswordEncryption;
import com.nts.global.exception.AppException;
import com.nts.global.util.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.nts.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private HashtagRepository hashtagRepository;

    @Mock
    private PostHashtagRepository postHashtagRepository;

    @Mock
    private User mockUser;

    @Mock
    private Post mockPost;

    @Mock
    private Hashtag mockHashtag;


    @Mock
    private PasswordEncryption encryption;

    @InjectMocks
    private PostService postService;

    String name = "name";
    String password = "password";
    String encryptedPassword = "encryptedPassword";
    String title = "title";
    String body = "body";
    Long userId = 1L;
    Long postId = 1L;
    Long viewCount = 1L;
    String createdDate = "2023/07/28 00:00";
    List<String> hashtags = List.of("tag", "new");
    List<Hashtag> existingHashtags = List.of(Hashtag.of("tag1"));

    String hashtagNames = StringUtil.convertListToString(hashtags);

    PostCreateRequest postCreateRequest;
    PostGetResponse postGetResponse;

    PostUpdateRequest postUpdateRequest;

    PostDeleteRequest postDeleteRequest;


    @BeforeEach
    void setUp() {
        postCreateRequest = PostCreateRequest.builder()
                .name(name)
                .password(password)
                .title(title)
                .body(body)
                .hashtags(hashtags)
                .build();

        postGetResponse = PostGetResponse.builder()
                .postId(postId)
                .title(title)
                .body(body)
                .viewCount(viewCount)
                .createdDate(createdDate)
                .build();

        postUpdateRequest = PostUpdateRequest.builder()
                .password(password)
                .title(title)
                .body(body)
                .hashtags(hashtags)
                .build();

        postDeleteRequest = PostDeleteRequest.builder()
                .password(password)
                .build();

    }

    @Nested
    @DisplayName("게시글 작성 테스트")
    class CreatePost {

        @Test
        @DisplayName("성공")
        void createPost_success() {
            //given
            given(userRepository.getReferenceById(userId))
                    .willReturn(mockUser);
            given(postRepository.save(any(Post.class)))
                    .willReturn(mockPost);
            given(mockPost.getTitle())
                    .willReturn(title);
            given(hashtagRepository.findHashtagByNameIn(hashtags))
                    .willReturn(existingHashtags);


            //when
            PostCreateResponse response = postService.createPost(postCreateRequest, userId);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getTitle()).isEqualTo(title);
            verify(userRepository, atLeastOnce()).getReferenceById(userId);
            verify(postRepository, atLeastOnce()).save(any(Post.class));
            verify(hashtagRepository, atLeastOnce()).findHashtagByNameIn(hashtags);
            verify(hashtagRepository, atLeastOnce()).saveAll(any());
            verify(postHashtagRepository, atLeastOnce()).saveAll(any());
        }

        @Test
        @DisplayName("해시태그 개수가 5개를 초과하는 경우 예외가 발생")
        void createPost_fail_exceedHashtagsNum() {
            //given
            postCreateRequest = PostCreateRequest.builder()
                    .name(name)
                    .password(password)
                    .title(title)
                    .body(body)
                    .hashtags(List.of("tag1", "tag2", "tag3", "tag4", "tag5", "tag6"))
                    .build();

            given(userRepository.getReferenceById(userId))
                    .willReturn(mockUser);

            //when
            AppException appException = assertThrows(AppException.class, () -> postService.createPost(postCreateRequest, userId));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(EXCEED_HASHTAG_SIZE);
            verify(userRepository, atLeastOnce()).getReferenceById(userId);
        }
    }

    @Nested
    @DisplayName("게시글 단건 조회 테스트")
    class GetPost {

        @Test
        @DisplayName("성공")
        void getPost_success() {
            //given
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            willDoNothing().given(postRepository)
                    .increaseViewCount(postId);
            given(postHashtagRepository.getHashtagNamesByPostId(postId))
                    .willReturn(List.of("tag"));
            given(mockPost.getUser())
                    .willReturn(mockUser);
            given(mockPost.getId())
                    .willReturn(postId);
            given(mockPost.getTitle())
                    .willReturn(title);
            given(mockPost.getBody())
                    .willReturn(body);
            given(mockPost.getViewCount())
                    .willReturn(viewCount);
            given(mockPost.getCreatedDate())
                    .willReturn(LocalDateTime.of(2023, 7, 28, 0, 0));


            //when
            PostGetResponse response = postService.getPost(postId);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getPostId()).isEqualTo(postId);
            assertThat(response.getTitle()).isEqualTo(title);
            assertThat(response.getBody()).isEqualTo(body);
            assertThat(response.getViewCount()).isEqualTo(viewCount + 1);
            assertThat(response.getCreatedDate()).isEqualTo(createdDate);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(postRepository, atLeastOnce()).increaseViewCount(postId);
        }

        @Test
        @DisplayName("존재하지 않은 postId 조회 요청 시 실패")
        void getPost_error() {
            //given
            given(postRepository.findById(postId))
                    .willReturn(Optional.empty());

            //when
            AppException appException = assertThrows(AppException.class, () -> postService.getPost(postId));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);
            verify(postRepository, atLeastOnce()).findById(postId);
        }

    }

    @Nested
    @DisplayName("게시글 수정 테스트")
    class UpdatePost {

        @Test
        @DisplayName("성공")
        void updatePost_success() {
            //given
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(mockPost.getUser())
                    .willReturn(mockUser);
            given(mockPost.getId())
                    .willReturn(postId);
            given(mockPost.getTitle())
                    .willReturn(title);
            given(encryption.encrypt(password))
                    .willReturn(encryptedPassword);
            given(mockUser.validatePassword(encryptedPassword))
                    .willReturn(true);
            willDoNothing().given(mockPost)
                    .update(title, body, hashtagNames);
            given(hashtagRepository.findHashtagByNameIn(hashtags))
                    .willReturn(existingHashtags);

            //when
            PostUpdateResponse response = postService.updatePost(postUpdateRequest, postId);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getPostId()).isEqualTo(postId);
            assertThat(response.getTitle()).isEqualTo(title);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(encryption, atLeastOnce()).encrypt(password);
            verify(mockUser, atLeastOnce()).validatePassword(encryptedPassword);
            verify(hashtagRepository, atLeastOnce()).findHashtagByNameIn(hashtags);
        }

        @Test
        @DisplayName("존재하지 않은 게시글에 수정 요청 시 예외가 발생")
        void updatePost_fail_postNotFound() {
            //given
            given(postRepository.findById(postId))
                    .willReturn(Optional.empty());

            //when
            AppException appException = assertThrows(AppException.class, () -> postService.updatePost(postUpdateRequest, postId));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);
            verify(postRepository, atLeastOnce()).findById(postId);
        }

        @Test
        @DisplayName("요청한 사용자의 비밀번호가 일치하지 않는 경우 예외가 발생")
        void updatePost_fail_invalidPassword() {
            //given
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(mockPost.getUser())
                    .willReturn(mockUser);
            given(encryption.encrypt(password))
                    .willReturn(encryptedPassword);
            given(mockUser.validatePassword(encryptedPassword))
                    .willReturn(false);

            //when
            AppException appException = assertThrows(AppException.class, () -> postService.updatePost(postUpdateRequest, postId));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(INVALID_PASSWORD);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(encryption, atLeastOnce()).encrypt(password);
            verify(mockUser, atLeastOnce()).validatePassword(encryptedPassword);
        }

        @Test
        @DisplayName("해시태그 개수가 5개를 초과하는 경우 예외가 발생")
        void updatePost_fail_exceedHashtagsNum() {
            //given
            postUpdateRequest = PostUpdateRequest.builder()
                    .password(password)
                    .title(title)
                    .body(body)
                    .hashtags(List.of("tag1", "tag2", "tag3", "tag4", "tag5", "tag6"))
                    .build();

            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(mockPost.getUser())
                    .willReturn(mockUser);
            given(encryption.encrypt(password))
                    .willReturn(encryptedPassword);
            given(mockUser.validatePassword(encryptedPassword))
                    .willReturn(true);

            //when
            AppException appException = assertThrows(AppException.class, () -> postService.updatePost(postUpdateRequest, postId));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(EXCEED_HASHTAG_SIZE);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(encryption, atLeastOnce()).encrypt(password);
            verify(mockUser, atLeastOnce()).validatePassword(encryptedPassword);
        }
    }

    @Nested
    @DisplayName("게시글 삭제 테스트")
    class DeletePost {

        @Test
        @DisplayName("성공")
        void deletePost_success() {
            //given
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(mockPost.getUser())
                    .willReturn(mockUser);
            given(mockPost.getId())
                    .willReturn(postId);
            given(mockPost.getTitle())
                    .willReturn(title);
            given(encryption.encrypt(password))
                    .willReturn(encryptedPassword);
            given(mockUser.validatePassword(encryptedPassword))
                    .willReturn(true);
            willDoNothing().given(postRepository)
                    .delete(mockPost);


            //when
            PostDeleteResponse response = postService.deletePost(postDeleteRequest, postId);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getPostId()).isEqualTo(postId);
            assertThat(response.getTitle()).isEqualTo(title);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(encryption, atLeastOnce()).encrypt(password);
            verify(mockUser, atLeastOnce()).validatePassword(encryptedPassword);
            verify(postRepository, atLeastOnce()).delete(mockPost);
        }

        @Test
        @DisplayName("존재하지 않은 게시글에 삭제 요청 시 예외가 발생")
        void deletePost_fail_postNotFound() {
            //given
            given(postRepository.findById(postId))
                    .willReturn(Optional.empty());

            //when
            AppException appException = assertThrows(AppException.class, () -> postService.deletePost(postDeleteRequest, postId));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);
            verify(postRepository, atLeastOnce()).findById(postId);
        }

        @Test
        @DisplayName("요청한 사용자의 비밀번호가 일치하지 않는 경우 예외가 발생")
        void deletePost_fail_invalidPassword() {
            //given
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(mockPost.getUser())
                    .willReturn(mockUser);
            given(encryption.encrypt(password))
                    .willReturn(encryptedPassword);
            given(mockUser.validatePassword(encryptedPassword))
                    .willReturn(false);

            //when
            AppException appException = assertThrows(AppException.class, () -> postService.deletePost(postDeleteRequest, postId));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(INVALID_PASSWORD);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(encryption, atLeastOnce()).encrypt(password);
            verify(mockUser, atLeastOnce()).validatePassword(encryptedPassword);
        }
    }
}