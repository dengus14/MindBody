package com.app.mindbody.service;

import com.app.mindbody.dto.AddWorkoutDTO;
import com.app.mindbody.models.User;
import com.app.mindbody.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserStatsService {

    UserRepository userRepository;

    public void updateStatsForNewWorkout(User user, AddWorkoutDTO workoutData){
        user.setLast_workout(LocalDate.now());
        user.setTotalMinutes(user.getTotalMinutes() + workoutData.getDurationMinutes());
        user.setPoints(user.getPoints() + 10);
        user.setLongest_workout(Math.max(user.getLongest_workout(), workoutData.getDurationMinutes()));
        user.setTotalWorkouts(user.getTotalWorkouts() + 1);
        userRepository.save(user);
    }




}
