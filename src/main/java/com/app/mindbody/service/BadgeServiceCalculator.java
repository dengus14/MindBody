package com.app.mindbody.service;


import com.app.mindbody.enums.RequirementTypeEnums;
import com.app.mindbody.models.Badge;
import com.app.mindbody.models.User;
import com.app.mindbody.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;

@Data
@Builder
@AllArgsConstructor
@Service
public class BadgeServiceCalculator {
    private final UserRepository userRepository;

    public int getProgressValue(Badge badge, User user) {
        switch( badge.getRequirement_type()){
            case RequirementTypeEnums.STREAK:
                return user.getStreak_count();
            case RequirementTypeEnums.DURATION:
                return user.getLongest_workout();
            case RequirementTypeEnums.TOTAL_DURATION:
                return user.getTotalMinutes();
            case RequirementTypeEnums.WORKOUT_COUNT:
                return user.getTotalWorkouts();
            case RequirementTypeEnums.MORNING_WORKOUTS:
                return user.getTotalMornings();
            case RequirementTypeEnums.EVENING_WORKOUTS:
                return user.getTotalEvenings();
        }

        return 0;
    }
}
