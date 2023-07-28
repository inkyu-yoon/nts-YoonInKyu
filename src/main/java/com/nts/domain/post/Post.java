package com.nts.domain.post;

import com.nts.domain.BaseEntity;
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
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private String body;

    private Long viewCount;

    @Builder
    public Post(User user, String title, String body) {
        Assert.notNull(user, "user must not be empty");
        Assert.hasText(title, "title must not be empty");
        Assert.hasText(body, "body must not be empty");

        this.viewCount = 0L;
        this.user = user;
        this.title = title;
        this.body = body;
    }
}
