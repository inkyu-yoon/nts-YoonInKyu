package com.nts.service;

import com.nts.domain.user.User;
import com.nts.domain.user.UserRepository;
import com.nts.domain.user.dto.UserCreateRequest;
import com.nts.domain.user.dto.UserCreateResponse;
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

import static com.nts.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private User mockUser;

    @Mock
    private PasswordEncryption encryption;

    @InjectMocks
    private UserService userService;

    String name = "testName";
    String password = "testPassword";
    String encryptedPassword = "encryptedPassword";
    Long userId = 1L;
    UserCreateRequest userCreateRequest;

    @BeforeEach
    void setUp() {
        userCreateRequest = UserCreateRequest.builder()
                .name(name)
                .password(password)
                .build();
    }

    @Nested
    @DisplayName("사용자 등록 테스트")
    class CreateUser{

        @Test
        @DisplayName("성공")
        void createUser_success(){
            //given
            given(userRepository.existsByName(name))
                    .willReturn(false);
            given(encryption.encrypt(password))
                    .willReturn(encryptedPassword);
            given(userRepository.save(any(User.class)))
                    .willReturn(mockUser);
            given(mockUser.getName())
                    .willReturn(name);

            //when
            UserCreateResponse response = userService.createUser(userCreateRequest);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo(name);
            verify(userRepository, atLeastOnce()).existsByName(name);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("사용자 명이 이미 존재하는 경우, 사용자 등록 시 중복 예외가 발생")
        void createUser_fail(){
            //given
            given(userRepository.existsByName(name))
                    .willReturn(true);

            //when
            AppException appException = assertThrows(AppException.class, () -> userService.createUser(userCreateRequest));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(DUPLICATE_USERNAME);
            verify(userRepository, atLeastOnce()).existsByName(name);
        }
    }

    @Nested
    @DisplayName("사용자 검증 기능 테스트")
    class ValidateUser{

        @Test
        @DisplayName("성공")
        void validateUser_success(){
            //given
            given(userRepository.findUserByName(name))
                    .willReturn(Optional.of(mockUser));
            given(encryption.encrypt(password))
                    .willReturn(encryptedPassword);
            given(mockUser.validatePassword(encryptedPassword))
                    .willReturn(true);
            given(mockUser.getId())
                    .willReturn(userId);

            //when
            Long response = userService.validateUser(name,password);

            //then
            assertThat(response).isNotNull();
            assertThat(response).isEqualTo(userId);
            verify(userRepository, atLeastOnce()).findUserByName(name);
            verify(encryption, atLeastOnce()).encrypt(password);
            verify(mockUser, atLeastOnce()).validatePassword(encryptedPassword);
        }

        @Test
        @DisplayName("등록된 사용자가 없는 경우 예외 발생")
        void validateUser_fail_notFound(){
            //given
            given(userRepository.findUserByName(name))
                    .willReturn(Optional.empty());

            //when
            AppException appException = assertThrows(AppException.class, () -> userService.validateUser(name,password));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);
            verify(userRepository, atLeastOnce()).findUserByName(name);
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않는 경우 예외 발생")
        void createUser_fail_invalidPassword(){
            //given
            given(userRepository.findUserByName(name))
                    .willReturn(Optional.of(mockUser));
            given(encryption.encrypt(password))
                    .willReturn(encryptedPassword);
            given(mockUser.validatePassword(encryptedPassword))
                    .willReturn(false);

            //when
            AppException appException = assertThrows(AppException.class, () -> userService.validateUser(name,password));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(INVALID_PASSWORD);
            verify(userRepository, atLeastOnce()).findUserByName(name);
        }
    }

}