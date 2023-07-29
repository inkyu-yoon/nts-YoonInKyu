package com.nts.domain.comment.dto;

import com.nts.domain.comment.Comment;
import com.nts.domain.post.Post;
import com.nts.domain.user.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CommentCreateRequest {
    private String name;
    private String password;
    private String body;

    public Comment toEntity(User user, Post post) {
        return Comment.builder()
                .user(user)
                .post(post)
                .body(this.body)
                .build();
    }
}
