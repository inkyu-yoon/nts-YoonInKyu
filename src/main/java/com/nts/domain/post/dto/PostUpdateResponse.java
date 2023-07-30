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
public class PostUpdateResponse {
    private Long postId;
    private String title;

    public static PostUpdateResponse from(Post post) {
        return PostUpdateResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .build();
    }

}
