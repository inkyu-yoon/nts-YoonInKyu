package com.nts.domain.comment.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CommentDeleteRequest {
    private String password;
}
