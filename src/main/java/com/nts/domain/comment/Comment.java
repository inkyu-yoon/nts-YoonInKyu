package com.nts.domain.comment;

import com.nts.domain.BaseEntity;
import com.nts.domain.post.Post;
import com.nts.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String body;

    private boolean isDeleted;

    @Builder
    public Comment(User user, Post post, String body) {
        Assert.notNull(user, "user must not be empty");
        Assert.notNull(post, "post must not be empty");
        Assert.hasText(body, "body must not be empty");

        this.user = user;
        this.post = post;
        this.body = body;
        this.isDeleted = false;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
