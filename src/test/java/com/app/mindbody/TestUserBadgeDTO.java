package com.app.mindbody;

import com.app.mindbody.config.JwtService;
import com.app.mindbody.controllers.UserBadgeDTO;
import com.app.mindbody.enums.RequirementTypeEnums;
import com.app.mindbody.models.Badge;
import com.app.mindbody.models.User;
import com.app.mindbody.models.UserBadge;
import com.app.mindbody.repositories.UserBadgeRepository;
import com.app.mindbody.repositories.UserRepository;
import com.app.mindbody.service.UserBadgeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestUserBadgeDTO {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @InjectMocks
    private UserBadgeService userBadgeService;

    @Test
    void shouldReturnUserBadgesAsDTOs() {
        // Arrange - Set up test data
        String token = "dummy.jwt.token";
        String userEmail = "test@example.com";

        // Create a mock user
        User user = new User();
        user.setId(1L);
        user.setEmail(userEmail);
        user.setUsername("testuser");

        // Create mock badges
        Badge badge1 = new Badge();
        badge1.setId(1);
        badge1.setBadge_name("Consistency Rookie");
        badge1.setBadge_description("Log workouts 3 days in a row");
        badge1.setRequirement_type(RequirementTypeEnums.STREAK);
        badge1.setRequirement_value(3);

        Badge badge2 = new Badge();
        badge2.setId(2);
        badge2.setBadge_name("Week Warrior");
        badge2.setBadge_description("Hit a 7-day workout streak");
        badge2.setRequirement_type(RequirementTypeEnums.STREAK);
        badge2.setRequirement_value(7);

        // Create mock user badges
        UserBadge userBadge1 = new UserBadge();
        userBadge1.setId(1L);
        userBadge1.setUser(user);
        userBadge1.setBadge(badge1);
        userBadge1.setEarned_at(LocalDateTime.of(2025, 1, 10, 12, 0));

        UserBadge userBadge2 = new UserBadge();
        userBadge2.setId(2L);
        userBadge2.setUser(user);
        userBadge2.setBadge(badge2);
        userBadge2.setEarned_at(LocalDateTime.of(2025, 1, 15, 14, 30));

        List<UserBadge> userBadges = List.of(userBadge1, userBadge2);

        // Mock the service dependencies
        when(jwtService.extractUsername(token)).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(userBadgeRepository.findAllByUser(user)).thenReturn(userBadges);

        // Act - Call the service method
        List<UserBadgeDTO> result = userBadgeService.getUserBadges(token);

        // Assert - Verify the results
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify first badge
        UserBadgeDTO dto1 = result.get(0);
        assertEquals(1, dto1.getBadge_id());
        assertEquals("Consistency Rookie", dto1.getBadge_name());
        assertEquals("Log workouts 3 days in a row", dto1.getBadge_description());
        assertEquals(RequirementTypeEnums.STREAK, dto1.getRequirement_type());
        assertEquals(3, dto1.getRequirement_value());
        assertEquals(LocalDateTime.of(2025, 1, 10, 12, 0), dto1.getEarned_at());

        // Verify second badge
        UserBadgeDTO dto2 = result.get(1);
        assertEquals(2, dto2.getBadge_id());
        assertEquals("Week Warrior", dto2.getBadge_name());
        assertEquals("Hit a 7-day workout streak", dto2.getBadge_description());
        assertEquals(RequirementTypeEnums.STREAK, dto2.getRequirement_type());
        assertEquals(7, dto2.getRequirement_value());
        assertEquals(LocalDateTime.of(2025, 1, 15, 14, 30), dto2.getEarned_at());

        // Verify that the service methods were called
        verify(jwtService, times(1)).extractUsername(token);
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(userBadgeRepository, times(1)).findAllByUser(user);

        System.out.println("✅ Test passed! User badges successfully converted to DTOs");
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoBadges() {
        // Arrange
        String token = "dummy.jwt.token";
        String userEmail = "newuser@example.com";

        User user = new User();
        user.setId(2L);
        user.setEmail(userEmail);

        when(jwtService.extractUsername(token)).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(userBadgeRepository.findAllByUser(user)).thenReturn(List.of());

        // Act
        List<UserBadgeDTO> result = userBadgeService.getUserBadges(token);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());

        System.out.println("✅ Test passed! Empty list returned for user with no badges");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        String token = "dummy.jwt.token";
        String userEmail = "nonexistent@example.com";

        when(jwtService.extractUsername(token)).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userBadgeService.getUserBadges(token);
        });

        assertEquals("User not found", exception.getMessage());

        System.out.println("✅ Test passed! Exception thrown for non-existent user");
    }

    @Test
    void shouldCorrectlyMapAllBadgeFields() {
        // Arrange - Test the DTO mapping directly
        Badge badge = new Badge();
        badge.setId(99);
        badge.setBadge_name("Test Badge");
        badge.setBadge_description("Test Description");
        badge.setRequirement_type(RequirementTypeEnums.STREAK);
        badge.setRequirement_value(10);

        UserBadge userBadge = new UserBadge();
        userBadge.setId(1L);
        userBadge.setBadge(badge);
        userBadge.setEarned_at(LocalDateTime.of(2025, 2, 1, 10, 30));

        // Act - Use the static fromEntity method
        UserBadgeDTO dto = UserBadgeDTO.fromEntity(userBadge);

        // Assert - Verify all fields are correctly mapped
        assertNotNull(dto);
        assertEquals(99, dto.getBadge_id());
        assertEquals("Test Badge", dto.getBadge_name());
        assertEquals("Test Description", dto.getBadge_description());
        assertEquals(RequirementTypeEnums.STREAK, dto.getRequirement_type());
        assertEquals(10, dto.getRequirement_value());
        assertEquals(LocalDateTime.of(2025, 2, 1, 10, 30), dto.getEarned_at());

        System.out.println("✅ Test passed! All DTO fields correctly mapped from entity");
    }
}