package com.nts.domain.user.dto;

import com.nts.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateResponse {
    private Long userId;
    private String name;

    public static UserCreateResponse from(User user) {
        return UserCreateResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .build();
    }
}
