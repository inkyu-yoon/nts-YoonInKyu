package com.nts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nts.domain.postLike.dto.LikeOrUnlikeRequest;
import com.nts.domain.postLike.dto.LikeOrUnlikeResponse;
import com.nts.global.exception.AppException;
import com.nts.global.exception.ErrorCode;
import com.nts.service.PostLikeService;
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

@WebMvcTest(value = PostLikeApiController.class)
class PostLikeApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
    @MockBean
    private PostLikeService postLikeService;

    String name = "name";
    String password = "password";
    Long userId = 1L;
    Long postId = 1L;
    LikeOrUnlikeRequest likeOrUnlikeRequest;
    LikeOrUnlikeResponse likeOrUnlikeResponse;

    @BeforeEach
    void setUp() {
        likeOrUnlikeRequest = LikeOrUnlikeRequest.builder()
                .name(name)
                .password(password)
                .build();

        likeOrUnlikeResponse = LikeOrUnlikeResponse.builder()
                .postId(postId)
                .build();

    }

    @Nested
    @DisplayName("Like 테스트")
    class LikeOrUnlikePost {

        @Test
        @DisplayName("성공")
        void likeOrUnlikePost_success() throws Exception {
            given(userService.validateUser(name, password))
                    .willReturn(userId);
            given(postLikeService.likeOrUnlikePost(userId, postId))
                    .willReturn(likeOrUnlikeResponse);

            mockMvc.perform(post("/api/v1/posts/" + postId + "/likes")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(likeOrUnlikeRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.postId").value(postId));
        }

        private static Stream<Arguments> provideLikeOrUnlikeFailScenarios() {
            return Stream.of(
                    Arguments.of(USER_NOT_FOUND, 404, "등록된 사용자가 아닙니다."),
                    Arguments.of(INVALID_PASSWORD, 401, "비밀번호가 일치하지 않습니다.")
            );
        }

        @DisplayName("사용자 관련 실패 케이스")
        @ParameterizedTest
        @MethodSource("provideLikeOrUnlikeFailScenarios")
        void likeOrUnlikePost_fail(ErrorCode errorCode, int responseStatus, String errorMessage) throws Exception {
            when(userService.validateUser(any(), any()))
                    .thenThrow(new AppException(errorCode));

            mockMvc.perform(post("/api/v1/posts/" + postId + "/likes")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(likeOrUnlikeRequest)))
                    .andDo(print())
                    .andExpect(status().is(responseStatus))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(errorMessage));

        }

        @Test
        @DisplayName("게시글이 존재하지 않는 경우 예외 발생")
        void likeOrUnlikePost_fail_postNotFound() throws Exception {
            given(userService.validateUser(name, password))
                    .willReturn(userId);

            when(postLikeService.likeOrUnlikePost(userId, postId))
                    .thenThrow(new AppException(POST_NOT_FOUND));

            mockMvc.perform(post("/api/v1/posts/" + postId + "/likes")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(likeOrUnlikeRequest)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value("등록된 게시글을 찾을 수 없습니다."));
        }
    }


}