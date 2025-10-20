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
    private int streak_count;

    @Column(name = "longestStreak")
    private int longest_streak;


    @CreatedDate
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime created_at;

    @LastModifiedDate
    @Column(name = "lastLogin", nullable = false)
    private LocalDateTime last_login;

    @Enumerated(EnumType.STRING)
    private UserRoleEnums role ;


    @Column(name = "lastWorkout")
    private LocalDate last_workout;


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
}
