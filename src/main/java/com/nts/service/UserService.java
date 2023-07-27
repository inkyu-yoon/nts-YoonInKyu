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

}
