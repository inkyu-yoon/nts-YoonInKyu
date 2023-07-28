package com.nts.domain.post.dto;

import com.nts.domain.post.Post;
import com.nts.domain.user.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PostUpdateRequest {
    private String password;
    private String title;
    private String body;

}
