package com.nts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nts.domain.user.dto.UserCreateRequest;
import com.nts.domain.user.dto.UserCreateResponse;
import com.nts.global.exception.AppException;
import com.nts.global.exception.ErrorCode;
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
import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(value = UserApiController.class)
class UserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    String name = "testName";
    String password = "testPassword";
    Long userId = 1L;
    UserCreateRequest userCreateRequest;
    UserCreateResponse userCreateResponse;

    @BeforeEach
    void setUp() {
        userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .password(password)
                .build();

        userCreateResponse = UserCreateResponse.builder()
                .userId(userId)
                .name(name)
                .build();
    }

    @Nested
    @DisplayName("사용자 등록 테스트")
    class CreateUser {

        @Test
        @DisplayName("성공")
        void createUser_success() throws Exception {
            given(userService.createUser(userCreateRequest))
                    .willReturn(userCreateResponse);

            mockMvc.perform(post("/api/v1/users")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.userId").value(userId))
                    .andExpect(jsonPath("$.result.name").value(name));
        }

        private static Stream<Arguments> provideUserCreateFailScenarios() {
            return Stream.of(
                    Arguments.of(DUPLICATE_USERNAME,409,"이미 존재하는 사용자명 입니다."),
                    Arguments.of(FAIL_ENCRYPT_PASSWORD,500,"비밀번호 암호화에 실패하였습니다. 다음에 시도해주세요.")
            );
        }

        @DisplayName("실패")
        @ParameterizedTest
        @MethodSource("provideUserCreateFailScenarios")
        void createUser_fail(ErrorCode errorCode, int responseStatus, String errorMessage) throws Exception {
            when(userService.createUser(any()))
                    .thenThrow(new AppException(errorCode));

            mockMvc.perform(post("/api/v1/users")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userCreateRequest)))
                    .andDo(print())
                    .andExpect(status().is(responseStatus))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(errorMessage));

        }

    }
}