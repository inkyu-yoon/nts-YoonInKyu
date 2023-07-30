package com.nts.domain.postHashtag;

import com.nts.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long>,PostHashtagCustomRepository {
    void deleteAllByPost(Post post);
}
