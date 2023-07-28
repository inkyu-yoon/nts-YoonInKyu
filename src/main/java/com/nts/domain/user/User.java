package com.nts.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;

    private String password;

    @Builder
    public User(String name, String password) {
        Assert.hasText(name, "name must not be empty");
        Assert.hasText(password, "password must not be empty");

        this.name = name;
        this.password = password;
    }


    public boolean validatePassword(String encryptedPassword) {
        return this.password.equals(encryptedPassword);
    }
}
