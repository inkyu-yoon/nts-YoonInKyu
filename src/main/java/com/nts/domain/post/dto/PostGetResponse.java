package com.nts.domain.post.dto;

import com.nts.domain.post.Post;
import com.nts.global.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostGetResponse {
    private Long postId;
    private String title;
    private String body;
    private Long viewCount;
    private String createdDate;

    public static PostGetResponse from(Post foundPost) {
        return PostGetResponse.builder()
                .postId(foundPost.getId())
                .title(foundPost.getTitle())
                .body(foundPost.getBody())
                .viewCount(foundPost.getViewCount()+1)
                .createdDate(DateUtil.convertLocalDateTimeToString(foundPost.getCreatedDate()))
                .build();
    }
}
