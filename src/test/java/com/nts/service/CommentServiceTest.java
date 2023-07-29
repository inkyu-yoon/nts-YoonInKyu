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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.nts.domain.comment.Constants.CommentConstants.COMMENT_PAGE_SIZE;
import static com.nts.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private User mockUser;

    @Mock
    private Post mockPost;

    @Mock
    private Comment mockComment;

    @Mock
    private PasswordEncryption encryption;


    @InjectMocks
    private CommentService commentService;
    String name = "name";
    String password = "password";
    String encryptedPassword = "encryptedPassword";
    String body = "body";
    Long userId = 1L;
    Long postId = 1L;
    Long commentId = 1L;
    String createdDate = "createdDate";
    CommentCreateRequest commentCreateRequest;
    CommentDeleteRequest commentDeleteRequest;
    Pageable pageable = PageRequest.of(0, COMMENT_PAGE_SIZE);
    CommentGetResponse commentGetResponse;


    @BeforeEach
    void setUp() {
        commentCreateRequest = CommentCreateRequest.builder()
                .name(name)
                .password(password)
                .body(body)
                .build();


        commentDeleteRequest = CommentDeleteRequest.builder()
                .password(password)
                .build();

        commentGetResponse = CommentGetResponse.builder()
                .commentId(commentId)
                .body(body)
                .author(name)
                .createdDate(createdDate)
                .build();

    }

    @Nested
    @DisplayName("댓글 작성 테스트")
    class CreateComment {

        @Test
        @DisplayName("성공")
        void createComment_success() {
            //given
            given(userRepository.getReferenceById(userId))
                    .willReturn(mockUser);
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(commentRepository.save(any()))
                    .willReturn(mockComment);
            given(mockComment.getBody())
                    .willReturn(body);


            //when
            CommentCreateResponse response = commentService.createComment(commentCreateRequest, userId, postId);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getBody()).isEqualTo(body);
            verify(userRepository, atLeastOnce()).getReferenceById(userId);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(commentRepository, atLeastOnce()).save(any());
        }

        @Test
        @DisplayName("게시글이 존재하지 않는 경우 예외 발생")
        void createComment_fail_notFound() {
            //given
            given(userRepository.getReferenceById(userId))
                    .willReturn(mockUser);
            given(postRepository.findById(postId))
                    .willReturn(Optional.empty());

            //when
            AppException appException = assertThrows(AppException.class, () -> commentService.createComment(commentCreateRequest, userId, postId));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);
            verify(userRepository, atLeastOnce()).getReferenceById(userId);
            verify(postRepository, atLeastOnce()).findById(postId);
        }

    }

    @Nested
    @DisplayName("댓글 삭제 테스트")
    class DeleteComment {

        @Test
        @DisplayName("성공")
        void deleteComment_success() {
            //given
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(commentRepository.findById(commentId))
                    .willReturn(Optional.of(mockComment));
            given(encryption.encrypt(password))
                    .willReturn(encryptedPassword);
            given(mockComment.getUser())
                    .willReturn(mockUser);
            given(mockUser.validatePassword(encryptedPassword))
                    .willReturn(true);
            given(mockComment.getId())
                    .willReturn(commentId);
            given(mockComment.getBody())
                    .willReturn(body);


            //when
            CommentDeleteResponse response = commentService.deleteComment(commentDeleteRequest, postId, commentId);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getCommentId()).isEqualTo(commentId);
            assertThat(response.getBody()).isEqualTo(body);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(commentRepository, atLeastOnce()).findById(commentId);
            verify(encryption, atLeastOnce()).encrypt(password);
        }

        @Test
        @DisplayName("게시글이 존재하지 않는 경우 예외 발생")
        void deleteComment_fail_postNotFound() {
            //given
            given(postRepository.findById(postId))
                    .willReturn(Optional.empty());
            //when
            AppException appException = assertThrows(AppException.class, () -> commentService.deleteComment(commentDeleteRequest, postId, commentId));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);
            verify(postRepository, atLeastOnce()).findById(postId);
        }

        @Test
        @DisplayName("게시글이 존재하지 않는 경우 예외 발생")
        void deleteComment_fail_CommentNotFound() {
            //given
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(commentRepository.findById(commentId))
                    .willReturn(Optional.empty());

            //when
            AppException appException = assertThrows(AppException.class, () -> commentService.deleteComment(commentDeleteRequest, postId, commentId));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(COMMENT_NOT_FOUND);
            verify(postRepository, atLeastOnce()).findById(postId);
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않는 경우 예외 발생")
        void deleteComment_fail_InvalidPassword() {
            //given
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(commentRepository.findById(commentId))
                    .willReturn(Optional.of(mockComment));
            given(encryption.encrypt(password))
                    .willReturn(encryptedPassword);
            given(mockComment.getUser())
                    .willReturn(mockUser);
            given(mockUser.validatePassword(encryptedPassword))
                    .willReturn(false);


            //when
            AppException appException = assertThrows(AppException.class, () -> commentService.deleteComment(commentDeleteRequest, postId, commentId));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(INVALID_PASSWORD);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(commentRepository, atLeastOnce()).findById(commentId);
            verify(encryption, atLeastOnce()).encrypt(password);
        }

    }

    @Nested
    @DisplayName("댓글 페이지 단위로 읽기 테스트")
    class GetPageComments {

        @Test
        @DisplayName("성공 테스트")
        void getPageComments_success(){
            //given
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(commentRepository.findAllByPostOrderByCreatedDateDesc(mockPost, pageable))
                    .willReturn(new PageImpl<>(List.of(mockComment)));
            given(mockComment.getUser())
                    .willReturn(mockUser);
            given(mockComment.getCreatedDate())
                    .willReturn(LocalDateTime.of(2023, 7, 29, 0, 0));

            //when
            Page<CommentGetResponse> response = commentService.getPageComment(postId, pageable);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getSize()).isEqualTo(1);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(commentRepository, atLeastOnce()).findAllByPostOrderByCreatedDateDesc(mockPost,pageable);

        }

        @Test
        @DisplayName("게시글이 존재하지 않는 경우 예외 발생")
        void getPageComments_fail_PostNotFound() {
            //given
            given(postRepository.findById(postId))
                    .willReturn(Optional.empty());

            //when
            AppException appException = assertThrows(AppException.class, () -> commentService.getPageComment(postId, pageable));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);
            verify(postRepository, atLeastOnce()).findById(postId);
        }

    }
}