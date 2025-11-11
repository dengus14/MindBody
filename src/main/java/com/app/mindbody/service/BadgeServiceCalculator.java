package com.app.mindbody.service;

import com.app.mindbody.enums.RequirementTypeEnums;
import com.app.mindbody.models.Badge;
import com.app.mindbody.models.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;

@Data
@Builder
@AllArgsConstructor
@Service
public class BadgeServiceCalculator {

    public int getProgressValue(Badge badge, UserProfile profile) {
        if (badge == null || profile == null) return 0;

        return switch (badge.getRequirement_type()) {
            case STREAK -> profile.getStreakCount();
            case DURATION -> profile.getLongestWorkout();
            case TOTAL_DURATION -> profile.getTotalMinutes();
            case WORKOUT_COUNT -> profile.getTotalWorkouts();
            case MORNING_WORKOUTS -> profile.getTotalMornings();
            case EVENING_WORKOUTS -> profile.getTotalEvenings();
            default -> 0;
        };
    }
}
