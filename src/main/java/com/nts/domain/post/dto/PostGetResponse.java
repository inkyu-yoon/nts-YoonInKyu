package com.nts.domain.post.dto;

import com.nts.domain.post.Post;
import com.nts.global.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostGetResponse {
    private Long postId;
    private String author;
    private String title;
    private String body;
    private Long viewCount;
    private Long likeCount;
    private String createdDate;
    private List<String> hashtagNames;

    public PostGetResponse(Long postId,String author, String title, String body, Long viewCount,Long likeCount, LocalDateTime createdDate, List<String> hashtagNames) {
        this.postId = postId;
        this.author = author;
        this.title = title;
        this.body = body;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.createdDate = DateUtil.convertLocalDateTimeToString(createdDate);
        this.hashtagNames = hashtagNames;
    }

    public static PostGetResponse from(Post foundPost, List<String> hashtagNames) {
        return PostGetResponse.builder()
                .author(foundPost.getUser().getName())
                .postId(foundPost.getId())
                .title(foundPost.getTitle())
                .body(foundPost.getBody())
                .viewCount(foundPost.getViewCount() + 1)
                .likeCount(foundPost.getLikeCount())
                .createdDate(DateUtil.convertLocalDateTimeToString(foundPost.getCreatedDate()))
                .hashtagNames(hashtagNames)
                .build();
    }
}
