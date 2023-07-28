package com.nts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nts.domain.post.dto.*;
import com.nts.global.exception.AppException;
import com.nts.global.exception.ErrorCode;
import com.nts.service.PostService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PostApiController.class)
class PostApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    String name = "name";
    String password = "password";
    String title = "title";
    String body = "body";
    Long postId = 1L;
    Long viewCount = 1L;
    String createdDate = "createdDate";

    PostCreateRequest postCreateRequest;
    PostCreateResponse postCreateResponse;
    PostGetResponse postGetResponse;
    PostUpdateRequest postUpdateRequest;
    PostUpdateResponse postUpdateResponse;
    PostDeleteRequest postDeleteRequest;
    PostDeleteResponse postDeleteResponse;

    @BeforeEach
    void setUp() {
        postCreateRequest = PostCreateRequest.builder()
                .name(name)
                .password(password)
                .title(title)
                .body(body)
                .build();

        postCreateResponse = PostCreateResponse.builder()
                .postId(postId)
                .title(title)
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
                .build();

        postUpdateResponse = PostUpdateResponse.builder()
                .postId(postId)
                .title(title)
                .build();

        postDeleteRequest = PostDeleteRequest.builder()
                .password(password)
                .build();

        postDeleteResponse = PostDeleteResponse.builder()
                .postId(postId)
                .title(title)
                .build();
    }

    @Nested
    @DisplayName("게시글 등록 테스트")
    class CreatePost {

        @Test
        @DisplayName("성공")
        void createPost_success() throws Exception {
            given(postService.createPost(postCreateRequest))
                    .willReturn(postCreateResponse);

            mockMvc.perform(post("/api/v1/posts")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.postId").value(postId))
                    .andExpect(jsonPath("$.result.title").value(title));
        }

        private static Stream<Arguments> providePostCreateFailScenarios() {
            return Stream.of(
                    Arguments.of(USER_NOT_FOUND, 404, "등록된 사용자가 아닙니다."),
                    Arguments.of(INVALID_PASSWORD, 401, "비밀번호가 일치하지 않습니다.")
            );
        }

        @DisplayName("실패")
        @ParameterizedTest
        @MethodSource("providePostCreateFailScenarios")
        void createPost_fail(ErrorCode errorCode, int responseStatus, String errorMessage) throws Exception {
            when(postService.createPost(any()))
                    .thenThrow(new AppException(errorCode));

            mockMvc.perform(post("/api/v1/posts")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postCreateResponse)))
                    .andDo(print())
                    .andExpect(status().is(responseStatus))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(errorMessage));

        }

    }

    @Nested
    @DisplayName("게시글 조회 테스트")
    class GetPost {

        @Test
        @DisplayName("성공")
        void GetPost_success() throws Exception {
            given(postService.getPost(postId))
                    .willReturn(postGetResponse);

            mockMvc.perform(get("/api/v1/posts/" + postId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.postId").value(postId))
                    .andExpect(jsonPath("$.result.title").value(title))
                    .andExpect(jsonPath("$.result.body").value(body))
                    .andExpect(jsonPath("$.result.viewCount").value(viewCount))
                    .andExpect(jsonPath("$.result.createdDate").value(createdDate));
        }


        @DisplayName("존재하지 않는 postId 조회 요청 시 실패")
        @Test
        void createPost_fail() throws Exception {
            when(postService.getPost(postId))
                    .thenThrow(new AppException(POST_NOT_FOUND));

            mockMvc.perform(get("/api/v1/posts/" + postId))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value("등록된 게시글을 찾을 수 없습니다."));

        }

    }

    @Nested
    @DisplayName("게시글 수정 테스트")
    class UpdatePost {

        @Test
        @DisplayName("성공")
        void updatePost_success() throws Exception {
            given(postService.updatePost(postUpdateRequest, postId))
                    .willReturn(postUpdateResponse);

            mockMvc.perform(put("/api/v1/posts/" + postId)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postUpdateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.postId").value(postId))
                    .andExpect(jsonPath("$.result.title").value(title));
        }

        private static Stream<Arguments> providePostUpdateFailScenarios() {
            return Stream.of(
                    Arguments.of(POST_NOT_FOUND, 404, "등록된 게시글을 찾을 수 없습니다."),
                    Arguments.of(INVALID_PASSWORD, 401, "비밀번호가 일치하지 않습니다.")
            );
        }

        @DisplayName("실패")
        @ParameterizedTest
        @MethodSource("providePostUpdateFailScenarios")
        void updatePost_fail(ErrorCode errorCode, int responseStatus, String errorMessage) throws Exception {
            when(postService.updatePost(any(), anyLong()))
                    .thenThrow(new AppException(errorCode));

            mockMvc.perform(put("/api/v1/posts/" + postId)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postUpdateRequest)))
                    .andDo(print())
                    .andExpect(status().is(responseStatus))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(errorMessage));

        }

    }

    @Nested
    @DisplayName("게시글 삭제 테스트")
    class DeletePost {

        @Test
        @DisplayName("성공")
        void deletePost_success() throws Exception {
            given(postService.deletePost(postDeleteRequest, postId))
                    .willReturn(postDeleteResponse);

            mockMvc.perform(delete("/api/v1/posts/" + postId)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postDeleteRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.postId").value(postId))
                    .andExpect(jsonPath("$.result.title").value(title));
        }

        private static Stream<Arguments> providePostDeleteFailScenarios() {
            return Stream.of(
                    Arguments.of(POST_NOT_FOUND, 404, "등록된 게시글을 찾을 수 없습니다."),
                    Arguments.of(INVALID_PASSWORD, 401, "비밀번호가 일치하지 않습니다.")
            );
        }

        @DisplayName("실패")
        @ParameterizedTest
        @MethodSource("providePostDeleteFailScenarios")
        void deletePost_fail(ErrorCode errorCode, int responseStatus, String errorMessage) throws Exception {
            when(postService.deletePost(any(), anyLong()))
                    .thenThrow(new AppException(errorCode));

            mockMvc.perform(delete("/api/v1/posts/" + postId)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postDeleteRequest)))
                    .andDo(print())
                    .andExpect(status().is(responseStatus))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(errorMessage));

        }

    }

}