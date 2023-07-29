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
public class CommentDeleteResponse {
    private Long commentId;
    private String body;

    public static CommentDeleteResponse from(Comment comment) {
        return CommentDeleteResponse.builder()
                .commentId(comment.getId())
                .body(comment.getBody())
                .build();
    }
}
