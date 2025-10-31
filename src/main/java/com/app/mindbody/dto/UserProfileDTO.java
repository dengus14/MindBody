package com.app.mindbody.dto;

import com.app.mindbody.models.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    private Long id;
    private int streakCount;
    private int longestStreak;
    private int totalMinutes;
    private int points;
    private int totalWorkouts;
    private int totalMornings;
    private int totalEvenings;
    private LocalDate lastWorkout;
    public static UserProfileDTO fromEntity(UserProfile profile) {
        return UserProfileDTO.builder()
                .id(profile.getId())
                .streakCount(profile.getStreakCount())
                .longestStreak(profile.getLongestStreak())
                .totalMinutes(profile.getTotalMinutes())
                .points(profile.getPoints())
                .totalWorkouts(profile.getTotalWorkouts())
                .totalMornings(profile.getTotalMornings())
                .totalEvenings(profile.getTotalEvenings())
                .lastWorkout(profile.getLastWorkout())
                .build();
    }
}
