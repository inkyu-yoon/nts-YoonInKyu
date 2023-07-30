package com.nts.domain.postHashtag;


import com.nts.domain.hashtag.Hashtag;
import com.nts.domain.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostHashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_hashtag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;


    @Builder
    public PostHashtag(Hashtag hashtag, Post post) {
        this.hashtag = hashtag;
        this.post = post;
    }

    public static PostHashtag of(Hashtag hashtag, Post post) {
        return PostHashtag.builder()
                .hashtag(hashtag)
                .post(post)
                .build();
    }

}
