package com.app.mindbody.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password_hash;

    @Column(name = "streakCount", nullable = false)
    private int streak_count;

    @CreatedDate
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime created_at;

    @LastModifiedDate
    @Column(name = "lastLogin", nullable = false)
    private LocalDateTime last_login;



}
