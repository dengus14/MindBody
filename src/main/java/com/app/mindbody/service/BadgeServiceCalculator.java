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
                break;
            case RequirementTypeEnums.DURATION:
                return user.getLongest_workout();
                break;
            case RequirementTypeEnums.TOTAL_DURATION:
                return user.getTotalMinutes();
                break;
            case RequirementTypeEnums.WORKOUT_COUNT:
                return user.getTotalWorkouts();
                break;
            case RequirementTypeEnums.MORNING_WORKOUTS:
                return user.getTotalMornings();
                break;
            case RequirementTypeEnums.EVENING_WORKOUTS:
                return user.getTotalEvenings();
                break;



        }

    }
}
