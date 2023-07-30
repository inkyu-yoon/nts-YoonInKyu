package com.nts.domain.postHashtag;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.nts.domain.hashtag.QHashtag.hashtag;
import static com.nts.domain.postHashtag.QPostHashtag.postHashtag;

@RequiredArgsConstructor
public class PostHashtagCustomRepositoryImpl implements PostHashtagCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<String> getHashtagNamesByPostId(Long postId) {
        return jpaQueryFactory.select(hashtag.name)
                .from(postHashtag)
                .join(postHashtag.hashtag, hashtag)
                .where(postHashtag.post.id.eq(postId))
                .fetch();
    }
}
