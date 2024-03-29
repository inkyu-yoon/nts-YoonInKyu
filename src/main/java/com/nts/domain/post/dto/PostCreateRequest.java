package com.nts.domain.post.dto;

import com.nts.domain.post.Post;
import com.nts.domain.user.User;
import com.nts.global.util.StringUtil;
import lombok.*;

import java.util.List;

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

    private List<String> hashtags;

    public Post toEntity(User user) {
        return Post.builder()
                .user(user)
                .title(this.title)
                .body(this.body)
                .hashtags(StringUtil.convertListToString(hashtags))
                .build();
    }

}
