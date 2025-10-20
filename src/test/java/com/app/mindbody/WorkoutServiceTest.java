package com.app.mindbody;

import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.WorkoutHistoryDTO;
import com.app.mindbody.enums.WorkoutTypeEnum;
import com.app.mindbody.models.User;
import com.app.mindbody.models.Workout;
import com.app.mindbody.repositories.UserRepository;
import com.app.mindbody.repositories.WorkoutRepository;
import com.app.mindbody.service.WorkoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkoutRepository workoutRepository;

    @InjectMocks
    private WorkoutService workoutService;

    private User user;
    private Workout workout1;
    private Workout workout2;
    private final String token = "mock-token";

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .password_hash("hash")
                .build();

        workout1 = Workout.builder()
                .id(10L)
                .user(user)
                .workoutType(WorkoutTypeEnum.LEGS)
                .durationMinutes(45)
                .notes("Morning run")
                .created_at(LocalDateTime.now().minusDays(1))
                .build();

        workout2 = Workout.builder()
                .id(11L)
                .user(user)
                .workoutType(WorkoutTypeEnum.PUSH)
                .durationMinutes(60)
                .notes("Evening gym")
                .created_at(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldReturnUserWorkoutsSortedByDate() {
        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(workoutRepository.findByUserOrderByCreated_atDesc(user))
                .thenReturn(List.of(workout2, workout1));

        List<WorkoutHistoryDTO> result = workoutService.getHistory(token);

        assertEquals(2, result.size());
        assertEquals(WorkoutTypeEnum.PUSH, result.get(0).getWorkoutType());
        assertEquals("Evening gym", result.get(0).getNotes());
        assertTrue(result.get(0).getCreated_at().isAfter(result.get(1).getCreated_at()));

        verify(jwtService, times(1)).extractUsername(token);
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(workoutRepository, times(1)).findByUserOrderByCreated_atDesc(user);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoWorkouts() {
        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(workoutRepository.findByUserOrderByCreated_atDesc(user))
                .thenReturn(Collections.emptyList());

        List<WorkoutHistoryDTO> result = workoutService.getHistory(token);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionIfUserNotFound() {
        when(jwtService.extractUsername(token)).thenReturn("missing@example.com");
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> workoutService.getHistory(token));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void shouldMapWorkoutFieldsCorrectly() {
        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(workoutRepository.findByUserOrderByCreated_atDesc(user))
                .thenReturn(List.of(workout1));

        List<WorkoutHistoryDTO> result = workoutService.getHistory(token);

        WorkoutHistoryDTO dto = result.get(0);
        assertEquals(WorkoutTypeEnum.LEGS, dto.getWorkoutType());
        assertEquals(45, dto.getDurationMinutes());
        assertEquals("Morning run", dto.getNotes());
        assertNotNull(dto.getCreated_at());
    }

    @Test
    void shouldCallDependenciesExactlyOnce() {
        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(workoutRepository.findByUserOrderByCreated_atDesc(user))
                .thenReturn(List.of(workout1));

        workoutService.getHistory(token);

        verify(jwtService, times(1)).extractUsername(token);
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(workoutRepository, times(1)).findByUserOrderByCreated_atDesc(user);
        verifyNoMoreInteractions(jwtService, userRepository, workoutRepository);
    }
}
