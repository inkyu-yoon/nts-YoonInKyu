package com.nts.domain.user.dto;

import com.nts.domain.user.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserCreateRequest {
    private String name;
    private String password;

    public User toEntity(String encryptedPassword) {
        return User.builder()
                .name(this.name)
                .password(encryptedPassword)
                .build();
    }
}
