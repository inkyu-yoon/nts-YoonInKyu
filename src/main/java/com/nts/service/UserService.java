package com.nts.service;

import com.nts.domain.user.User;
import com.nts.domain.user.UserRepository;
import com.nts.domain.user.dto.UserCreateRequest;
import com.nts.domain.user.dto.UserCreateResponse;
import com.nts.global.exception.AppException;
import com.nts.global.exception.ErrorCode;
import com.nts.global.encrypt.PasswordEncryption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncryption encryption;

    /**
     * 사용자 등록
     */
    public UserCreateResponse createUser(UserCreateRequest requestDto) {
        // 사용자 명 중복 확인
        checkDuplicateName(requestDto);

        // 비밀번호 암호화
        String encryptedPassword = encryption.encrypt(requestDto.getPassword());

        User savedUser = userRepository.save(requestDto.toEntity(encryptedPassword));

        return UserCreateResponse.from(savedUser);
    }

    /**
     * 등록을 요청한 사용자 명이 중복되는지 확인, 중복되는 경우 에러 처리
     */
    private void checkDuplicateName(UserCreateRequest requestDto) {
        if (userRepository.existsByName(requestDto.getName())) {
            throw new AppException(ErrorCode.DUPLICATE_USERNAME);
        }
    }

    /**
     * 사용자명이 등록된 사용자인지 확인합니다.
     * 비밀번호가 일치하는지 확인합니다.
     */
    public Long validateUser(String name, String password) {
        // 등록된 사용자인지 확인
        User foundUser = userRepository.findUserByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 일치하는지 확인
        validatePassword(password, foundUser);

        return foundUser.getId();
    }

    /**
     * 비밀번호가 일치하는지 확인
     */
    private void validatePassword(String password, User foundUser) {
        String encryptedPassword = encryption.encrypt(password);

        // 비밀번호가 일치하는지 확인
        if (!foundUser.validatePassword(encryptedPassword)) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }
    }

}
