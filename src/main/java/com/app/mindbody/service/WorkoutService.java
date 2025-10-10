package com.app.mindbody.service;


import com.app.mindbody.config.JwtService;
import com.app.mindbody.controllers.AddWorkoutRequest;
import com.app.mindbody.enums.UserRoleEnums;
import com.app.mindbody.enums.WorkoutTypeEnum;
import com.app.mindbody.models.User;
import com.app.mindbody.models.Workout;
import com.app.mindbody.repositories.UserRepository;
import com.app.mindbody.repositories.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        var workout = Workout
                .builder()
                .user(user)
                .durationMinutes(request.getDurationMinutes())
                .workoutType(WorkoutTypeEnum.PUSH)
                .build();
        workoutRepository.save(workout);
        return workout.toString();
    }


}
