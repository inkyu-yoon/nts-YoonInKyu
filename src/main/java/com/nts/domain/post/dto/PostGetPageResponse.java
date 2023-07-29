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
public class PostGetPageResponse {
    private Long postId;
    private String title;
    private String author;
    private Long viewCount;
    private Long commentCount;
    private Long likeCount;
    private String createdDate;
    private String hashtagNames;
    private boolean isNew;

    public static PostGetPageResponse from(Post foundPost) {
        return PostGetPageResponse.builder()
                .postId(foundPost.getId())
                .title(foundPost.getTitle())
                .author(foundPost.getUser().getName())
                .viewCount(foundPost.getViewCount())
                .commentCount(foundPost.getCommentCount())
                .likeCount(foundPost.getLikeCount())
                .createdDate(DateUtil.convertLocalDateTimeToString(foundPost.getCreatedDate()))
                .isNew(DateUtil.checkRecentDate(foundPost.getCreatedDate()))
                .hashtagNames(foundPost.getHashtags())
                .build();
    }

    public boolean getIsNew() {
        return isNew;
    }
}
