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


    public String addWorkout(AddWorkoutDTO request, String token){

        //retrieve user from JWT token
        String username = jwtService.extractUsername(token);
        var user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        //update streak count
        if (user.getLast_workout() != null && LocalDate.now().equals(user.getLast_workout().plusDays(1))) {
            user.setStreak_count(user.getStreak_count() + 1);
            if (user.getStreak_count() >= user.getLongest_streak()) {
                user.setLongest_streak(user.getStreak_count());
            }

        } else if (user.getLast_workout() == null || !LocalDate.now().equals(user.getLast_workout())) {

            user.setStreak_count(1);
            if(user.getLongest_streak() < 1){
                user.setLongest_streak(1);
            }
        }


        // adds badges to the user if he earned them
        List<Badge> badgesList = badgeRepository.findAllByOrderByRequirementValueAsc();

        for  (Badge badge : badgesList) {
            if ( user.getStreak_count() >= badge.getRequirementValue() && !(userBadgeRepository.findByUserAndBadge(user,badge).isPresent())) {

                UserBadge userBadge = new UserBadge();
                userBadge.setUser(user);
                userBadge.setBadge(badge);
                userBadgeRepository.save(userBadge);
            }
        }
        //update user
        user.setLast_workout(LocalDate.now());
        user.setTotalMinutes(user.getTotalMinutes()+ request.getDurationMinutes());
        userRepository.save(user);

        //create new workout object
        var workout = Workout
                .builder()
                .user(user)
                .durationMinutes(request.getDurationMinutes())
                .workoutType(request.getWorkoutType())
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
