package com.nts.domain.postLike.dto;

import com.nts.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeOrUnlikeResponse {
    private Long postId;

    public static LikeOrUnlikeResponse from(Post post) {
        return LikeOrUnlikeResponse.builder()
                .postId(post.getId())
                .build();
    }

}
