package com.nts.domain.hashtag;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long id;

    private String name;

    @Builder
    public Hashtag(String name) {
        this.name = name;
    }

    public static Hashtag of(String name) {
        return Hashtag.builder()
                .name(name)
                .build();
    }
}
