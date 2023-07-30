package com.nts.domain.post;

import com.nts.domain.BaseEntity;
import com.nts.domain.comment.Comment;
import com.nts.domain.postHashtag.PostHashtag;
import com.nts.domain.postLike.PostLike;
import com.nts.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private String body;

    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private String hashtags;

    @OneToMany(mappedBy = "post",orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post",orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private Set<PostHashtag> postHashtags = new HashSet<>();

    @Builder
    public Post(User user, String title, String body, String hashtags) {
        Assert.notNull(user, "user must not be empty");
        Assert.hasText(title, "title must not be empty");
        Assert.hasText(body, "body must not be empty");

        this.viewCount = 0L;
        this.commentCount = 0L;
        this.likeCount = 0L;
        this.user = user;
        this.title = title;
        this.body = body;
        this.hashtags = hashtags;
    }

    public void update(String title, String body,String hashtags) {
        this.title = title;
        this.body = body;
        this.hashtags = hashtags;
    }
}
