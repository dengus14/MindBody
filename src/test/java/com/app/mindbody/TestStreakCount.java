package com.app.mindbody;

import com.app.mindbody.config.JwtService;
import com.app.mindbody.controllers.AddWorkoutRequest;
import com.app.mindbody.models.User;
import com.app.mindbody.repositories.UserRepository;
import com.app.mindbody.repositories.WorkoutRepository;
import com.app.mindbody.service.WorkoutService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestStreakCount {



        @Mock private JwtService jwtService;
        @Mock private WorkoutRepository workoutRepository;
        @Mock private UserRepository userRepository;

        @InjectMocks
        private WorkoutService workoutService;

        @Test
        void shouldIncrementStreakWhenNextDay() {

            String token = "dummy.jwt.token";
            AddWorkoutRequest request = new AddWorkoutRequest();
            request.setDurationMinutes(45);

            User user = new User();
            user.setEmail("test@example.com");
            user.setLast_workout(LocalDate.now().minusDays(1));
            user.setStreak_count(3);
            user.setLongest_streak(3);

            when(jwtService.extractUsername(token)).thenReturn("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));


            workoutService.addWorkout(request, token);


            assertEquals(4, user.getStreak_count());
            assertEquals(4, user.getLongest_streak());
            assertEquals(LocalDate.now(), user.getLast_workout());
            verify(workoutRepository).save(any());//tuta
        }

        @Test
        void shouldResetStreakIfSkippedDay() {

            String token = "dummy.jwt.token";
            AddWorkoutRequest request = new AddWorkoutRequest();
            request.setDurationMinutes(45);

            User user = new User();
            user.setEmail("test@example.com");
            user.setLast_workout(LocalDate.now().minusDays(2));
            user.setStreak_count(5);
            user.setLongest_streak(7);

            when(jwtService.extractUsername(token)).thenReturn("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));


            workoutService.addWorkout(request, token);


            assertEquals(1, user.getStreak_count());
            verify(workoutRepository).save(any());
        }




}
