package com.nts.domain.post.dto;

import com.nts.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateResponse {
    private Long postId;
    private String title;

    public static PostCreateResponse from(Post post) {
        return PostCreateResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .build();
    }

}
