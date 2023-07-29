package com.nts.domain.comment.dto;

import com.nts.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateResponse {
    private Long commentId;
    private String body;

    public static CommentCreateResponse from(Comment comment) {
        return CommentCreateResponse.builder()
                .commentId(comment.getId())
                .body(comment.getBody())
                .build();
    }

}
