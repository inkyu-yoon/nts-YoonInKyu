package com.nts.domain.postLike.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LikeOrUnlikeRequest {
    private String name;
    private String password;

}
