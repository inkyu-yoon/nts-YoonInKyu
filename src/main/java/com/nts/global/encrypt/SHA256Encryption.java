package com.nts.global.encrypt;

import com.nts.global.exception.AppException;
import com.nts.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
@Slf4j
public class SHA256Encryption implements PasswordEncryption {

    @Override
    public String encrypt(String password) {
        try {
            // MessageDigest 객체 생성 (SHA-256 알고리즘)
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 입력받은 문자열을 바이트 배열로 변환
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // 바이트 배열을 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {

                // 16 진수로 변환
                String hex = Integer.toHexString(0xff & b);

                // 문자열 길이를 2로 고정
                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            log.error("비밀번호 암호화 에러", e);
            throw new AppException(ErrorCode.FAIL_ENCRYPT_PASSWORD);
        }
    }
}
