package com.nts.domain.comment.dto;

import com.nts.domain.comment.Comment;
import com.nts.global.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.nts.domain.comment.Constants.CommentConstants.REPLACE_DELETED_COMMENT;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentGetResponse {
    private Long commentId;
    private String body;
    private String createdDate;
    private String author;

    public static CommentGetResponse from(Comment comment) {

        String body = comment.isDeleted() ? REPLACE_DELETED_COMMENT : comment.getBody();

        return CommentGetResponse.builder()
                .commentId(comment.getId())
                .body(body)
                .createdDate(DateUtil.convertLocalDateTimeToString(comment.getCreatedDate()))
                .author(comment.getUser().getName())
                .build();
    }
}
