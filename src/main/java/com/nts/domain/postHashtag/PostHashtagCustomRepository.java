package com.nts.domain.postHashtag;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostHashtagCustomRepository {

    List<String> getHashtagNamesByPostId(Long postId);
}
