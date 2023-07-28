package com.nts.domain.post.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PostUpdateRequest {
    private String password;
    private String title;
    private String body;
    private List<String> hashtags;
}
