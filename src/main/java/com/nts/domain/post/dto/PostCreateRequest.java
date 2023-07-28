package com.nts.domain.post.dto;

import com.nts.domain.post.Post;
import com.nts.domain.user.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PostCreateRequest {
    private String name;
    private String password;
    private String title;
    private String body;

    public Post toEntity(User user) {
        return Post.builder()
                .user(user)
                .title(this.title)
                .body(this.body)
                .build();
    }
}
