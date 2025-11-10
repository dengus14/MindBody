package com.app.mindbody.dto;

import com.app.mindbody.models.UserAuth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    public static UserAuthDTO fromEntity(UserAuth auth) {
        return UserAuthDTO.builder()
                .id(auth.getId())
                .username(auth.getUsername())
                .email(auth.getEmail())
                .role(auth.getRole() != null ? auth.getRole().name() : null)
                .createdAt(auth.getCreatedAt())
                .lastLogin(auth.getLastLogin())
                .build();
    }
}
