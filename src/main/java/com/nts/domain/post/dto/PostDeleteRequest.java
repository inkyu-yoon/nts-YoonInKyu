package com.nts.domain.post.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PostDeleteRequest {
    private String password;

}
