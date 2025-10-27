package com.app.mindbody.models;

import com.app.mindbody.enums.UserRoleEnums;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor

@EntityListeners(AuditingEntityListener.class) // otherwise @CreatedDate and @LastModifiedDate won't populate
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;


    @Column(name = "password", nullable = false)
    private String password_hash;

    @Column(name = "streakCount", nullable = false)
    private Integer streak_count = 0;

    @Column(name = "longestStreak", nullable = false)
    private Integer longest_streak = 0;

    @Column(name = "longestWorkout",nullable = false)
    private Integer longest_workout = 0;


    @CreatedDate
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime created_at;

    @LastModifiedDate
    @Column(name = "lastLogin", nullable = false)
    private LocalDateTime last_login;

    @Enumerated(EnumType.STRING)
    private UserRoleEnums role;


    @Column(name = "lastWorkout")
    private LocalDate last_workout;

    @Column(name = "totalMinutes",nullable = false)
    private Integer totalMinutes=0;

    @Column(name = "points", nullable = false)
    private Integer points = 0;

    @Column(name = "totalWorkouts",nullable = false)
    private Integer totalWorkouts=0;

    @Column(name = "totalCaloriesBurned")
    private Integer totalCaloriesBurned;

    @Column(name = "totalMornings")
    private Integer totalMornings;

    @Column(name = "totalEvenings")
    private Integer totalEvenings;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password_hash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @PrePersist
    public void prePersist() {
        if (streak_count == null) streak_count = 0;
        if (longest_streak == null) longest_streak = 0;
        if (points == null) points = 0;
        if (totalMinutes == null) totalMinutes = 0;
        if (totalWorkouts == null) totalWorkouts = 0;
        if (totalCaloriesBurned == null) totalCaloriesBurned = 0;
        if (totalMornings == null) totalMornings = 0;
        if (totalEvenings == null) totalEvenings = 0;
    }
}
