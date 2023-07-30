package com.nts.domain.post;

import com.nts.domain.post.dto.PostDataGetResponse;
import com.nts.domain.post.dto.PostGetPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCustomRepository {

    Page<PostGetPageResponse> getPostsBySearch(String searchCondition, String keyword , Pageable pageable);

    PostDataGetResponse getPostAndCommentTotalCount();

}
