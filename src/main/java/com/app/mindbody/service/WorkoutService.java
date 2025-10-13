package com.app.mindbody.service;


import com.app.mindbody.config.JwtService;
import com.app.mindbody.controllers.AddWorkoutRequest;
import com.app.mindbody.controllers.EditWorkoutRequest;
import com.app.mindbody.enums.UserRoleEnums;
import com.app.mindbody.enums.WorkoutTypeEnum;
import com.app.mindbody.models.User;
import com.app.mindbody.models.Workout;
import com.app.mindbody.repositories.UserRepository;
import com.app.mindbody.repositories.WorkoutRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkoutService {
    private final JwtService jwtService;
    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;


    public String addWorkout(AddWorkoutRequest request, String token){


        String username = jwtService.extractUsername(token);
        var user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));


        if (user.getLast_workout() != null && LocalDate.now().equals(user.getLast_workout().plusDays(1))) {
            user.setStreak_count(user.getStreak_count() + 1);
            if (user.getStreak_count() >= user.getLongest_streak()) {
                user.setLongest_streak(user.getStreak_count());
            }
        } else if (user.getLast_workout() == null || !LocalDate.now().equals(user.getLast_workout())) {

            user.setStreak_count(1);
        }
        user.setLast_workout(LocalDate.now());

        userRepository.save(user);

        var workout = Workout
                .builder()
                .user(user)

                .durationMinutes(request.getDurationMinutes())
                .workoutType(WorkoutTypeEnum.PUSH)
                .build();
        workoutRepository.save(workout);



        return workout.toString();
    }



    public String editWorkout(EditWorkoutRequest request, String token){

        String username = jwtService.extractUsername(token);
        var user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));

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
    public String removeWorkout(EditWorkoutRequest request, String token){

        String username = jwtService.extractUsername(token);
        var user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));

        var workout = workoutRepository.findById(request.getId()).orElseThrow(() -> new RuntimeException("Workout not found"));

        if(!workout.getUser().equals(user)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot remove someone else's workout");

        }
        workoutRepository.removeById(request.getId());
        return workout.toString();
    }




}
