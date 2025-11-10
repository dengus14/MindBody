package com.app.mindbody;

import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.AddWorkoutDTO;
import com.app.mindbody.enums.RequirementTypeEnums;
import com.app.mindbody.models.Badge;
import com.app.mindbody.models.UserBadge;
import com.app.mindbody.repositories.*;
import com.app.mindbody.service.WorkoutService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestBadgeAssignment {

    @Mock private JwtService jwtService;
    @Mock private WorkoutRepository workoutRepository;
    @Mock private UserRepository userRepository;
    @Mock private BadgeRepository badgeRepository;
    @Mock private UserBadgeRepository userBadgeRepository;

    @InjectMocks
    private WorkoutService workoutService;

    @Test
    void shouldAwardBadgeWhenRequirementMet() {
        String token = "dummy.jwt.token";
        AddWorkoutDTO request = new AddWorkoutDTO();
        request.setDurationMinutes(30);

        User user = new User();
        user.setUsername("test@example.com");
        user.setStreak_count(7);
        user.setLast_workout(LocalDate.now().minusDays(1));

        Badge badge = new Badge();
        badge.setId(1);
        badge.setBadge_name("Week Warrior");
        badge.setRequirement_type(RequirementTypeEnums.STREAK);
        badge.setRequirementValue(7);

        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));
        when(badgeRepository.findAllByOrderByRequirementValueAsc()).thenReturn(List.of(badge));


        workoutService.addWorkout(request, token);


        ArgumentCaptor<UserBadge> captor = ArgumentCaptor.forClass(UserBadge.class);
        verify(userBadgeRepository).save(captor.capture());

        UserBadge savedBadge = captor.getValue();
        assertEquals(user, savedBadge.getUser());
        assertEquals(badge, savedBadge.getBadge());
    }
}
