package com.app.mindbody.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    Long id;
    private String username;
    private String email;
    private int streak_count;
    private int longest_streak;
    private int totalMinutes;
    private LocalDate last_workout;
    private LocalDateTime created_at;
}
