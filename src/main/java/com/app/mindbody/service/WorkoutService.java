package com.app.mindbody.service;


import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.AddWorkoutDTO;
import com.app.mindbody.dto.EditWorkoutDTO;
import com.app.mindbody.dto.WorkoutHistoryDTO;
import com.app.mindbody.enums.WorkoutTypeEnum;
import com.app.mindbody.models.Badge;
import com.app.mindbody.models.UserBadge;
import com.app.mindbody.models.Workout;
import com.app.mindbody.repositories.BadgeRepository;
import com.app.mindbody.repositories.UserBadgeRepository;
import com.app.mindbody.repositories.UserRepository;
import com.app.mindbody.repositories.WorkoutRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutService {
    private final JwtService jwtService;
    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;


    public String addWorkout(AddWorkoutDTO request, String token) {
        // Retrieve user from JWT token
        String username = jwtService.extractUsername(token);
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // === Defensive null guards ===
        user.setStreak_count(user.getStreak_count() == null ? 0 : user.getStreak_count());
        user.setLongest_streak(user.getLongest_streak() == null ? 0 : user.getLongest_streak());
        user.setLongest_workout(user.getLongest_workout() == null ? 0 : user.getLongest_workout());
        user.setTotalMinutes(user.getTotalMinutes() == null ? 0 : user.getTotalMinutes());
        user.setPoints(user.getPoints() == null ? 0 : user.getPoints());
        user.setTotalWorkouts(user.getTotalWorkouts() == null ? 0 : user.getTotalWorkouts());
        user.setTotalMornings(user.getTotalMornings() == null ? 0 : user.getTotalMornings());
        user.setTotalEvenings(user.getTotalEvenings() == null ? 0 : user.getTotalEvenings());

        // === Update streak count ===
        if (user.getLast_workout() != null && LocalDate.now().equals(user.getLast_workout().plusDays(1))) {
            user.setStreak_count(user.getStreak_count() + 1);
            if (user.getStreak_count() >= user.getLongest_streak()) {
                user.setLongest_streak(user.getStreak_count());
            }
        } else if (user.getLast_workout() == null || !LocalDate.now().equals(user.getLast_workout())) {
            user.setStreak_count(1);
            if (user.getLongest_streak() < 1) {
                user.setLongest_streak(1);
            }
        }

        // === Badge assignment ===
        List<Badge> badgesList = badgeRepository.findAllByOrderByRequirementValueAsc();
        for (Badge badge : badgesList) {
            boolean alreadyHasBadge = userBadgeRepository.findByUserAndBadge(user, badge).isPresent();
            if (user.getStreak_count() >= badge.getRequirementValue() && !alreadyHasBadge) {
                UserBadge userBadge = new UserBadge();
                userBadge.setUser(user);
                userBadge.setBadge(badge);
                userBadgeRepository.save(userBadge);
            }
        }

        // === Workout + stats updates ===
        user.setLast_workout(LocalDate.now());
        user.setTotalMinutes(user.getTotalMinutes() + request.getDurationMinutes());
        user.setPoints(user.getPoints() + 10);
        user.setLongest_workout(
                user.getLongest_workout() < request.getDurationMinutes()
                        ? request.getDurationMinutes()
                        : user.getLongest_workout()
        );
        user.setTotalWorkouts(user.getTotalWorkouts() + 1);

        // Morning/evening counts
        int hour = LocalDateTime.now().getHour();
        if (hour >= 5 && hour < 12) {
            user.setTotalMornings(user.getTotalMornings() + 1);
        } else {
            user.setTotalEvenings(user.getTotalEvenings() + 1);
        }

        userRepository.save(user);

        // === Save workout record ===
        Workout workout = Workout.builder()
                .user(user)
                .durationMinutes(request.getDurationMinutes())
                .workoutType(request.getWorkoutType())
                .notes(request.getNotes())
                .build();

        workoutRepository.save(workout);

        return workout.toString();
    }




    public String editWorkout(EditWorkoutDTO request, String token){

        String username = jwtService.extractUsername(token);
        var user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        var workout = workoutRepository.findById(request.getId()).orElseThrow(() -> new RuntimeException("Workout not found"));

        if(!workout.getUser().equals(user)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot edit someone else's workout");

        }

        workout.setDurationMinutes(request.getDurationMinutes());
        workout.setWorkoutType(request.getWorkoutType());
        workout.setNotes(request.getNotes());
        workoutRepository.save(workout);
        return workout.toString();
    }




    @Transactional
    public String removeWorkout(EditWorkoutDTO request, String token){

        String username = jwtService.extractUsername(token);
        var user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        var workout = workoutRepository.findById(request.getId()).orElseThrow(() -> new RuntimeException("Workout not found"));

        if(!workout.getUser().equals(user)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot remove someone else's workout");

        }
        workoutRepository.removeById(request.getId());
        return workout.toString();
    }
    public List<WorkoutHistoryDTO> getHistory(String token){
        String username = jwtService.extractUsername(token);
        var user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        List<Workout> allWorkouts = workoutRepository.findByUserOrderByCreatedAtDesc(user);
        return allWorkouts.stream()
                .map(WorkoutHistoryDTO::fromEntity)  // This calls fromEntity for each item
                .collect(Collectors.toList());


    }




}
