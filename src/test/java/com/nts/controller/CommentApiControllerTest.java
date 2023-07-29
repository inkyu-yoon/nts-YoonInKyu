package com.nts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nts.domain.comment.dto.CommentCreateRequest;
import com.nts.domain.comment.dto.CommentCreateResponse;
import com.nts.global.exception.AppException;
import com.nts.global.exception.ErrorCode;
import com.nts.service.CommentService;
import com.nts.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static com.nts.global.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CommentApiController.class)
class CommentApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
    @MockBean
    private CommentService commentService;

    String name = "name";
    String password = "password";
    String body = "body";
    Long userId = 1L;
    Long postId = 1L;
    Long commentId = 1L;
    CommentCreateRequest commentCreateRequest;
    CommentCreateResponse commentCreateResponse;

    @BeforeEach
    void setUp() {
        commentCreateRequest = CommentCreateRequest.builder()
                .name(name)
                .password(password)
                .body(body)
                .build();

        commentCreateResponse = CommentCreateResponse.builder()
                .commentId(commentId)
                .body(body)
                .build();
    }


    @Nested
    @DisplayName("댓글 등록 테스트")
    class CreateComment {

        @Test
        @DisplayName("성공")
        void createComment_success() throws Exception {
            given(userService.validateUser(name, password))
                    .willReturn(userId);
            given(commentService.createComment(commentCreateRequest, userId, postId))
                    .willReturn(commentCreateResponse);

            mockMvc.perform(post("/api/v1/posts/" + postId + "/comments")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(commentCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.commentId").value(commentId))
                    .andExpect(jsonPath("$.result.body").value(body));
        }

        private static Stream<Arguments> provideCommentCreateFailScenarios() {
            return Stream.of(
                    Arguments.of(USER_NOT_FOUND, 404, "등록된 사용자가 아닙니다."),
                    Arguments.of(INVALID_PASSWORD, 401, "비밀번호가 일치하지 않습니다.")
            );
        }

        @DisplayName("사용자 관련 실패 케이스")
        @ParameterizedTest
        @MethodSource("provideCommentCreateFailScenarios")
        void createComment_fail(ErrorCode errorCode, int responseStatus, String errorMessage) throws Exception {
            when(userService.validateUser(any(),any()))
                    .thenThrow(new AppException(errorCode));

            mockMvc.perform(post("/api/v1/posts/" + postId + "/comments")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(commentCreateRequest)))
                    .andDo(print())
                    .andExpect(status().is(responseStatus))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(errorMessage));

        }

        @Test
        @DisplayName("게시글이 존재하지 않는 경우 예외 발생")
        void createComment_fail_postNotFound() throws Exception {
            given(userService.validateUser(name, password))
                    .willReturn(userId);

            when(commentService.createComment(commentCreateRequest, userId, postId))
                    .thenThrow(new AppException(POST_NOT_FOUND));

            mockMvc.perform(post("/api/v1/posts/" + postId + "/comments")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(commentCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value("등록된 게시글을 찾을 수 없습니다."));
        }


    }
}