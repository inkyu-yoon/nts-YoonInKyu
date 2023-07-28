package com.nts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nts.domain.post.dto.PostCreateRequest;
import com.nts.domain.post.dto.PostCreateResponse;
import com.nts.global.exception.AppException;
import com.nts.global.exception.ErrorCode;
import com.nts.service.PostService;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    PostCreateRequest postCreateRequest;
    PostCreateResponse postCreateResponse;

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
                    Arguments.of(USER_NOT_FOUND,404,"등록된 사용자가 아닙니다."),
                    Arguments.of(INVALID_PASSWORD,401,"비밀번호가 일치하지 않습니다.")
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

}