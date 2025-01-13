package com.github.aivle6th.ai23.springboot-backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @Builder
    public User(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String password, String username) {
        this.password = password;
        this.username = username;
        this.modifiedAt = LocalDateTime.now();
    }
}