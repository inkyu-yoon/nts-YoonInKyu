package com.nts.service;

import com.nts.domain.post.Post;
import com.nts.domain.post.PostRepository;
import com.nts.domain.post.dto.PostCreateRequest;
import com.nts.domain.post.dto.PostCreateResponse;
import com.nts.domain.user.User;
import com.nts.domain.user.UserRepository;
import com.nts.global.encrypt.PasswordEncryption;
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

import static com.nts.global.exception.ErrorCode.INVALID_PASSWORD;
import static com.nts.global.exception.ErrorCode.USER_NOT_FOUND;
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
    private User mockUser;

    @Mock
    private Post mockPost;

    @Mock
    private PasswordEncryption encryption;

    @InjectMocks
    private PostService postService;

    String name = "name";
    String password = "password";
    String encryptedPassword = "encryptedPassword";
    String title = "title";
    String body = "body";

    PostCreateRequest postCreateRequest;

    @BeforeEach
    void setUp() {
        postCreateRequest = PostCreateRequest.builder()
                .name(name)
                .password(password)
                .title(title)
                .body(body)
                .build();
    }

    @Nested
    @DisplayName("게시글 작성 테스트")
    class CreatePost{

        @Test
        @DisplayName("성공")
        void createPost_success() {
            //given
            given(userRepository.findUserByName(name))
                    .willReturn(Optional.of(mockUser));
            given(encryption.encrypt(password))
                    .willReturn(encryptedPassword);
            given(mockUser.validatePassword(encryptedPassword))
                    .willReturn(true);
            given(postRepository.save(any(Post.class)))
                    .willReturn(mockPost);
            given(mockPost.getTitle())
                    .willReturn(title);

            //when
            PostCreateResponse response = postService.createPost(postCreateRequest);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getTitle()).isEqualTo(title);
            verify(userRepository, atLeastOnce()).findUserByName(name);
            verify(encryption, atLeastOnce()).encrypt(password);
            verify(mockUser, atLeastOnce()).validatePassword(encryptedPassword);
            verify(postRepository,atLeastOnce()).save(any(Post.class));
        }

        @Test
        @DisplayName("등록되지 않은 사용자가 요청 시 예외가 발생")
        void createPost_fail_userNotFound(){
            //given
            given(userRepository.findUserByName(name))
                    .willReturn(Optional.empty());

            //when
            AppException appException = assertThrows(AppException.class, () -> postService.createPost(postCreateRequest));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);
            verify(userRepository, atLeastOnce()).findUserByName(name);
        }

        @Test
        @DisplayName("요청한 사용자의 비밀번호가 일치하지 않는 경우 예외가 발생")
        void createPost_fail_invalidPassword(){
            //given
            given(userRepository.findUserByName(name))
                    .willReturn(Optional.of(mockUser));
            given(encryption.encrypt(password))
                    .willReturn(encryptedPassword);
            given(mockUser.validatePassword(encryptedPassword))
                    .willReturn(false);

            //when
            AppException appException = assertThrows(AppException.class, () -> postService.createPost(postCreateRequest));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(INVALID_PASSWORD);
            verify(userRepository, atLeastOnce()).findUserByName(name);
            verify(encryption, atLeastOnce()).encrypt(password);
            verify(mockUser, atLeastOnce()).validatePassword(encryptedPassword);
        }
    }

}