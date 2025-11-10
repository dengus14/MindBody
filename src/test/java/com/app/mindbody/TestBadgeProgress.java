package com.app.mindbody;

import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.BadgeDTO;
import com.app.mindbody.enums.RequirementTypeEnums;
import com.app.mindbody.models.Badge;
import com.app.mindbody.repositories.BadgeRepository;
import com.app.mindbody.repositories.UserBadgeRepository;
import com.app.mindbody.repositories.UserRepository;
import com.app.mindbody.service.BadgeService;
import com.app.mindbody.service.BadgeServiceCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestBadgeProgress {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @Mock
    private BadgeServiceCalculator badgeServiceCalculator;

    @InjectMocks
    private BadgeService badgeService;

    @Test
    void shouldReturnAllBadgesWithProgressForStreak() {
        String token = "test.jwt.token";
        User user = new User();
        user.setEmail("test@example.com");
        user.setStreak_count(5);

        Badge badge1 = new Badge(1, "First Badge", "First achievement", RequirementTypeEnums.STREAK, 3);
        Badge badge2 = new Badge(2, "Second Badge", "Second achievement", RequirementTypeEnums.STREAK, 7);

        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));
        when(badgeRepository.findAllByOrderByRequirementValueAsc()).thenReturn(List.of(badge1, badge2));
        when(badgeServiceCalculator.getProgressValue(badge1, user)).thenReturn(user.getStreak_count());
        when(badgeServiceCalculator.getProgressValue(badge2, user)).thenReturn(user.getStreak_count());

        List<BadgeDTO> result = badgeService.getProgress(token);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("First Badge", result.get(0).getBadge_name());
        assertEquals(3, result.get(0).getRequirement_value());
        assertEquals(5, result.get(0).getProgress_value());

        assertEquals("Second Badge", result.get(1).getBadge_name());
        assertEquals(7, result.get(1).getRequirement_value());
        assertEquals(5, result.get(1).getProgress_value());
    }

    @Test
    void shouldReturnCorrectProgressForDifferentRequirementTypes() {
        String token = "test.jwt.token";
        User user = new User();
        user.setEmail("test@example.com");
        user.setStreak_count(10);
        user.setLongest_workout(45);
        user.setTotalMinutes(120);
        user.setTotalWorkouts(5);
        user.setTotalMornings(3);
        user.setTotalEvenings(2);

        Badge badgeStreak = new Badge(1, "Streak Badge", "Streak achievement", RequirementTypeEnums.STREAK, 15);
        Badge badgeDuration = new Badge(2, "Duration Badge", "Longest workout", RequirementTypeEnums.DURATION, 60);
        Badge badgeTotalDuration = new Badge(3, "Total Duration", "Total minutes", RequirementTypeEnums.TOTAL_DURATION, 200);
        Badge badgeWorkoutCount = new Badge(4, "Workout Count", "Total workouts", RequirementTypeEnums.WORKOUT_COUNT, 10);
        Badge badgeMorning = new Badge(5, "Morning Badge", "Morning workouts", RequirementTypeEnums.MORNING_WORKOUTS, 5);
        Badge badgeEvening = new Badge(6, "Evening Badge", "Evening workouts", RequirementTypeEnums.EVENING_WORKOUTS, 5);

        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));
        when(badgeRepository.findAllByOrderByRequirementValueAsc()).thenReturn(List.of(
                badgeStreak, badgeDuration, badgeTotalDuration, badgeWorkoutCount, badgeMorning, badgeEvening
        ));

        when(badgeServiceCalculator.getProgressValue(badgeStreak, user)).thenReturn(user.getStreak_count());
        when(badgeServiceCalculator.getProgressValue(badgeDuration, user)).thenReturn(user.getLongest_workout());
        when(badgeServiceCalculator.getProgressValue(badgeTotalDuration, user)).thenReturn(user.getTotalMinutes());
        when(badgeServiceCalculator.getProgressValue(badgeWorkoutCount, user)).thenReturn(user.getTotalWorkouts());
        when(badgeServiceCalculator.getProgressValue(badgeMorning, user)).thenReturn(user.getTotalMornings());
        when(badgeServiceCalculator.getProgressValue(badgeEvening, user)).thenReturn(user.getTotalEvenings());

        List<BadgeDTO> result = badgeService.getProgress(token);

        assertEquals(6, result.size());
        assertEquals(10, result.get(0).getProgress_value());
        assertEquals(45, result.get(1).getProgress_value());
        assertEquals(120, result.get(2).getProgress_value());
        assertEquals(5, result.get(3).getProgress_value());
        assertEquals(3, result.get(4).getProgress_value());
        assertEquals(2, result.get(5).getProgress_value());
    }

    @Test
    void shouldReturnEmptyListWhenNoBadgesExist() {
        String token = "test.jwt.token";
        User user = new User();
        user.setEmail("test@example.com");

        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));
        when(badgeRepository.findAllByOrderByRequirementValueAsc()).thenReturn(List.of());

        List<BadgeDTO> result = badgeService.getProgress(token);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        String token = "invalid.jwt.token";

        when(jwtService.extractUsername(token)).thenReturn("nonexistent@example.com");
        when(userRepository.findByUsername("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> badgeService.getProgress(token));
    }
}
