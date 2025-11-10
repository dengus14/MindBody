package com.app.mindbody.service;

import com.app.mindbody.enums.WorkoutTypeEnum;
import com.app.mindbody.models.User;
import com.app.mindbody.models.Workout;
import com.app.mindbody.repositories.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class ChallengeCalculator {

    private final WorkoutRepository workoutRepository;


    public Integer calculateProgress(User user, String challengeName) {
        if (challengeName == null) {
            log.warn("Challenge name is null for user {}", user.getUsername());
            return 0;
        }

        String lowerName = challengeName.toLowerCase();

        // Workout type-specific challenges (MUST check before generic workout count)
        if (lowerName.contains("push")) {
            return calculateWorkoutsByType(user, WorkoutTypeEnum.PUSH);
        }
        if (lowerName.contains("pull")) {
            return calculateWorkoutsByType(user, WorkoutTypeEnum.PULL);
        }
        if (lowerName.contains("legs") || lowerName.contains("leg")) {
            return calculateWorkoutsByType(user, WorkoutTypeEnum.LEGS);
        }

        // Duration-based challenges (sum minutes, not count)
        if (lowerName.contains("minute")) {
            return calculateTotalDuration(user);
        }

        // Streak challenges
        if (lowerName.contains("streak") || lowerName.contains("day streak")) {
            return user.getStreak_count() != null ? user.getStreak_count() : 0;
        }

        // Morning workout challenges
        if (lowerName.contains("AM") || lowerName.contains("morning")) {
            return calculateMorningWorkouts(user);
        }

        // Evening workout challenges
        if (lowerName.contains("evening") || lowerName.contains("PM")) {
            return calculateEveningWorkouts(user);
        }

        // Default: total workout count (any type)
        return user.getTotalWorkouts() != null ? user.getTotalWorkouts() : 0;
    }


    private Integer calculateWorkoutsByType(User user, WorkoutTypeEnum workoutType) {
        List<Workout> workouts = workoutRepository.findByUserOrderByCreatedAtDesc(user);

        long count = workouts.stream()
                .filter(w -> w.getWorkoutType() == workoutType)
                .count();

        log.debug("User {} has {} {} workouts", user.getUsername(), count, workoutType);
        return (int) count;
    }


    private Integer calculateTotalDuration(User user) {
        List<Workout> workouts = workoutRepository.findByUserOrderByCreatedAtDesc(user);

        int totalMinutes = workouts.stream()
                .mapToInt(Workout::getDurationMinutes)
                .sum();

        log.debug("Total duration for user {}: {} minutes", user.getUsername(), totalMinutes);
        return totalMinutes;
    }


    private Integer calculateMorningWorkouts(User user) {
        List<Workout> workouts = workoutRepository.findByUserOrderByCreatedAtDesc(user);

        long morningCount = workouts.stream()
                .filter(w -> w.getCreatedAt() != null && (w.getCreatedAt().toLocalTime().isAfter(LocalTime.of(23, 59)) && w.getCreatedAt().toLocalTime().isBefore(LocalTime.of(11, 59))))
                .count();

        return (int) morningCount;
    }

    private Integer calculateEveningWorkouts(User user) {
        List<Workout> workouts = workoutRepository.findByUserOrderByCreatedAtDesc(user);

        long eveningCount = workouts.stream()
                .filter(w -> w.getCreatedAt() != null && (w.getCreatedAt().toLocalTime().isAfter(LocalTime.of(12, 0)) && w.getCreatedAt().toLocalTime().isBefore(LocalTime.of(23, 59))))
                .count();

        return (int) eveningCount;
    }


    public boolean isRequirementMet(Integer currentProgress, Integer requirementValue) {
        return currentProgress != null
                && requirementValue != null
                && currentProgress >= requirementValue;
    }
}