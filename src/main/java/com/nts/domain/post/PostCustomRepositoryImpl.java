package com.nts.domain.post;

import com.nts.domain.post.dto.PostGetPageResponse;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.nts.domain.post.QPost.post;
import static com.nts.domain.user.QUser.user;
import static com.querydsl.core.group.GroupBy.groupBy;

@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PostGetPageResponse> getPostsBySearch(String searchCondition, String keyword, Pageable pageable) {
        List<PostGetPageResponse> posts = jpaQueryFactory.from(post)
                .where(authorContains(searchCondition, keyword), titleContains(searchCondition, keyword), bodyContains(searchCondition, keyword), hashtagContains(searchCondition, keyword))
                .join(user).on(user.id.eq(post.user.id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdDate.desc())
                .transform(groupBy(post.id)
                        .list(Projections.constructor(PostGetPageResponse.class, post)
                        ));

        long total = jpaQueryFactory.selectFrom(post)
                .where(authorContains(searchCondition, keyword), titleContains(searchCondition, keyword), bodyContains(searchCondition, keyword))
                .join(user).on(user.eq(post.user))
                .fetch()
                .size();


        return new PageImpl<>(posts, pageable, total);
    }

    private Predicate authorContains(String searchCondition, String keyword) {
        if (searchCondition.equals("author")) {
            return keyword == null ? null : post.user.name.contains(keyword);
        } else {
            return null;
        }
    }

    private Predicate titleContains(String searchCondition, String keyword) {
        if (searchCondition.equals("title")) {
            return keyword == null ? null : post.title.contains(keyword);
        } else {
            return null;
        }
    }

    private Predicate bodyContains(String searchCondition, String keyword) {
        if (searchCondition.equals("content")) {
            return keyword == null ? null : post.body.contains(keyword);
        } else {
            return null;
        }
    }

    private Predicate hashtagContains(String searchCondition, String keyword) {
        if (searchCondition.equals("hashtag")) {
            return keyword == null ? null : post.postHashtags.any().hashtag.name.eq(keyword);
        } else {
            return null;
        }
    }

}
