package com.app.mindbody;

import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.AddWorkoutDTO;
import com.app.mindbody.enums.WorkoutTypeEnum;
import com.app.mindbody.models.Badge;
import com.app.mindbody.models.User;
import com.app.mindbody.models.UserBadge;
import com.app.mindbody.repositories.BadgeRepository;
import com.app.mindbody.repositories.UserBadgeRepository;
import com.app.mindbody.repositories.UserRepository;
import com.app.mindbody.repositories.WorkoutRepository;
import com.app.mindbody.service.WorkoutService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestNoDuplicateBadge {

    @Mock private JwtService jwtService;
    @Mock private WorkoutRepository workoutRepository;
    @Mock private UserRepository userRepository;
    @Mock private BadgeRepository badgeRepository;
    @Mock private UserBadgeRepository userBadgeRepository;

    @InjectMocks
    private WorkoutService workoutService;

    @Test
    void shouldNotAddDuplicateBadgeWhenAlreadyOwned() {
        String token = "dummy.jwt.token";
        User user = new User();
        user.setEmail("test@example.com");
        user.setStreak_count(7);
        user.setLast_workout(LocalDate.now().minusDays(1));

        Badge badge = new Badge();
        badge.setId(1);
        badge.setRequirementValue(3);

        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(badgeRepository.findAll()).thenReturn(List.of(badge));

        when(userBadgeRepository.findByUserAndBadge(user, badge)).thenReturn(Optional.of(new UserBadge()));

        AddWorkoutDTO request = new AddWorkoutDTO();
        request.setDurationMinutes(45);
        request.setWorkoutType(WorkoutTypeEnum.PUSH);

        workoutService.addWorkout(request, token);

        verify(userBadgeRepository, never()).save(any(UserBadge.class));
        verify(workoutRepository).save(any());
    }
}
