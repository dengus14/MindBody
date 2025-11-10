package com.app.mindbody.service;

import com.app.mindbody.dto.AddWorkoutDTO;
import com.app.mindbody.models.UserProfile;
import com.app.mindbody.repositories.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    UserProfileRepository userProfileRepository;


    public void updateStatsForNewWorkout(UserProfile profile, AddWorkoutDTO workoutData){

        LocalDate today = LocalDate.now();
        LocalDate last = profile.getLastWorkout();

        profile.setLastWorkout(today);
        profile.setTotalMinutes(profile.getTotalMinutes() + workoutData.getDurationMinutes());
        profile.setPoints(profile.getPoints() + 10);
        profile.setLongestWorkout(Math.max(profile.getLongestWorkout(), workoutData.getDurationMinutes()));
        profile.setTotalWorkouts(profile.getTotalWorkouts() + 1);
        int hour = LocalDateTime.now().getHour();
        if (hour >= 5 && hour < 12)
            profile.setTotalMornings(profile.getTotalMornings() + 1);
        else
            profile.setTotalEvenings(profile.getTotalEvenings() + 1);
        log.debug("Updated stats: totalWorkouts={}, totalMinutes={}, points={}",
                profile.getTotalWorkouts(), profile.getTotalMinutes(), profile.getPoints());
    }




}
