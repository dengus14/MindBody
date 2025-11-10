package com.app.mindbody.service;

import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.AddWorkoutDTO;
import com.app.mindbody.dto.EditWorkoutDTO;
import com.app.mindbody.dto.WorkoutHistoryDTO;
import com.app.mindbody.models.*;
import com.app.mindbody.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final JwtService jwtService;
    private final WorkoutRepository workoutRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserProfileRepository userProfileRepository;
    private final BadgeService badgeService;
    private final UserProfileService userProfileService;
    private final StreakCountService streakCountService;

    private UserAuth getAuth(String token) {
        String username = jwtService.extractUsername(token);
        return userAuthRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("UserAuth not found for username={}", username);
                    return new RuntimeException("User not found");
                });
    }

    /** Add new workout */
    @Transactional
    public String addWorkout(AddWorkoutDTO request, String token) {
        log.info("Adding new workout for token={}", token.substring(0, Math.min(10, token.length())));

        UserAuth auth = getAuth(token);
        UserProfile profile = userProfileRepository.findByAuth(auth)
                .orElseThrow(() -> {
                    log.error("UserProfile missing for auth id={}", auth.getId());
                    return new RuntimeException("Profile not found");
                });

        Workout workout = Workout.builder()
                .userProfile(profile)
                .durationMinutes(request.getDurationMinutes())
                .workoutType(request.getWorkoutType())
                .notes(request.getNotes())
                .build();
        workoutRepository.save(workout);

        streakCountService.updateStreakForWorkout(profile);
        userProfileService.updateStatsForNewWorkout(profile,request);
        userProfileRepository.save(profile);
        badgeService.awardBadges(profile);
        log.info("Workout saved (id={}) for user={}", workout.getId());
        return workout.toString();
    }

    /** Edit existing workout */
    public String editWorkout(EditWorkoutDTO request, String token) {
        UserAuth auth = getAuth(token);

        Workout workout = workoutRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        workout.setDurationMinutes(request.getDurationMinutes());
        workout.setWorkoutType(request.getWorkoutType());
        workout.setNotes(request.getNotes());
        workoutRepository.save(workout);
        log.info("Workout id={} updated successfully", workout.getId());
        return workout.toString();
    }

    /** Remove workout */
    @Transactional
    public String removeWorkout(EditWorkoutDTO request, String token) {
        UserAuth auth = getAuth(token);

        Workout workout = workoutRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Workout not found"));

        workoutRepository.removeById(request.getId());
        log.warn("Workout id={} removed by user={}", request.getId());
        return workout.toString();
    }

    /** Get workout history */
    public List<WorkoutHistoryDTO> getHistory(String token) {
        UserAuth auth = getAuth(token);
        UserProfile profile = userProfileRepository.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        List<Workout> workouts = workoutRepository.findByUserProfileOrderByCreatedAtDesc(profile);
        log.debug("Found {} workouts for user={}", workouts.size());
        return workouts.stream()
                .map(WorkoutHistoryDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
