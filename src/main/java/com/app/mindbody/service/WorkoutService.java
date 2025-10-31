package com.app.mindbody.service;

import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.AddWorkoutDTO;
import com.app.mindbody.dto.EditWorkoutDTO;
import com.app.mindbody.dto.WorkoutHistoryDTO;
import com.app.mindbody.models.*;
import com.app.mindbody.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final JwtService jwtService;
    private final WorkoutRepository workoutRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserProfileRepository userProfileRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    /** Award badges based on current profile stats */
    private void awardBadges(UserProfile profile) {
        List<Badge> badges = badgeRepository.findAllByOrderByRequirementValueAsc();

        for (Badge badge : badges) {
            boolean alreadyHas = userBadgeRepository
                    .findByUserProfileAndBadge(profile, badge)
                    .isPresent();
            if (alreadyHas) continue;

            boolean qualifies = switch (badge.getRequirement_type()) {
                case STREAK -> profile.getStreakCount() >= badge.getRequirementValue();
                case WORKOUT_COUNT -> profile.getTotalWorkouts() >= badge.getRequirementValue();
                case DURATION -> profile.getLongestWorkout() >= badge.getRequirementValue();
                case TOTAL_DURATION -> profile.getTotalMinutes() >= badge.getRequirementValue();
                case MORNING_WORKOUTS -> profile.getTotalMornings() >= badge.getRequirementValue();
                case EVENING_WORKOUTS -> profile.getTotalEvenings() >= badge.getRequirementValue();
                default -> false;
            };

            if (qualifies) {
                UserBadge earned = UserBadge.builder()
                        .userProfile(profile)
                        .badge(badge)
                        .build();
                userBadgeRepository.save(earned);
            }
        }
    }

    /** Add new workout */
    public String addWorkout(AddWorkoutDTO request, String token) {
        String username = jwtService.extractUsername(token);
        UserAuth auth = userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // ---- update streaks ----
        LocalDate today = LocalDate.now();
        LocalDate last = profile.getLastWorkout();

        if (last != null && today.equals(last.plusDays(1))) {
            profile.setStreakCount(profile.getStreakCount() + 1);
            profile.setLongestStreak(Math.max(profile.getLongestStreak(), profile.getStreakCount()));
        } else if (last == null || !today.equals(last)) {
            profile.setStreakCount(1);
            profile.setLongestStreak(Math.max(profile.getLongestStreak(), 1));
        }

        // ---- update stats ----
        profile.setLastWorkout(today);
        profile.setTotalMinutes(profile.getTotalMinutes() + request.getDurationMinutes());
        profile.setPoints(profile.getPoints() + 10);
        profile.setLongestWorkout(Math.max(profile.getLongestWorkout(), request.getDurationMinutes()));
        profile.setTotalWorkouts(profile.getTotalWorkouts() + 1);

        int hour = LocalDateTime.now().getHour();
        if (hour >= 5 && hour < 12)
            profile.setTotalMornings(profile.getTotalMornings() + 1);
        else
            profile.setTotalEvenings(profile.getTotalEvenings() + 1);

        userProfileRepository.save(profile);
        awardBadges(profile);

        // ---- save workout ----
        Workout workout = Workout.builder()
                .userProfile(profile)
                .durationMinutes(request.getDurationMinutes())
                .workoutType(request.getWorkoutType())
                .notes(request.getNotes())
                .build();

        workoutRepository.save(workout);
        return workout.toString();
    }

    /** Edit existing workout */
    public String editWorkout(EditWorkoutDTO request, String token) {
        String username = jwtService.extractUsername(token);
        userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Workout workout = workoutRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        workout.setDurationMinutes(request.getDurationMinutes());
        workout.setWorkoutType(request.getWorkoutType());
        workout.setNotes(request.getNotes());
        workoutRepository.save(workout);
        return workout.toString();
    }

    /** Remove workout */
    @Transactional
    public String removeWorkout(EditWorkoutDTO request, String token) {
        String username = jwtService.extractUsername(token);
        userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Workout workout = workoutRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        workoutRepository.removeById(request.getId());
        return workout.toString();
    }

    /** Get workout history */
    public List<WorkoutHistoryDTO> getHistory(String token) {
        String username = jwtService.extractUsername(token);
        UserAuth auth = userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserProfile profile = userProfileRepository.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        List<Workout> workouts = workoutRepository.findByUserProfileOrderByCreatedAtDesc(profile);
        return workouts.stream()
                .map(WorkoutHistoryDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
