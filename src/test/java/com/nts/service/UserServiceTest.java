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

import static com.nts.global.exception.ErrorCode.DUPLICATE_USERNAME;
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

}