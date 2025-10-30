package com.app.mindbody.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "user_profile")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    @Id
    private Long id; // same as UserAuth.id

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private UserAuth auth;

    private Integer streakCount = 0;
    private Integer longestStreak = 0;
    private Integer longestWorkout = 0;
    private LocalDate lastWorkout;
    private Integer totalMinutes = 0;
    private Integer points = 0;
    private Integer totalWorkouts = 0;
    private Integer totalCaloriesBurned = 0;
    private Integer totalMornings = 0;
    private Integer totalEvenings = 0;

    @PrePersist
    public void initDefaults() {
        if (streakCount == null) streakCount = 0;
        if (longestStreak == null) longestStreak = 0;
        if (points == null) points = 0;
        if (totalMinutes == null) totalMinutes = 0;
        if (totalWorkouts == null) totalWorkouts = 0;
        if (totalCaloriesBurned == null) totalCaloriesBurned = 0;
        if (totalMornings == null) totalMornings = 0;
        if (totalEvenings == null) totalEvenings = 0;
    }
}
