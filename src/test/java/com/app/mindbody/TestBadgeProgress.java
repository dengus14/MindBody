package com.app.mindbody;

import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.BadgeProgressDTO;
import com.app.mindbody.enums.RequirementTypeEnums;
import com.app.mindbody.models.Badge;
import com.app.mindbody.models.User;
import com.app.mindbody.models.UserBadge;
import com.app.mindbody.repositories.BadgeRepository;
import com.app.mindbody.repositories.UserBadgeRepository;
import com.app.mindbody.repositories.UserRepository;
import com.app.mindbody.service.BadgeService;
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

    @InjectMocks
    private BadgeService badgeService;

    @Test
    void shouldReturnNextBadgeWhenUserHasNotEarnedAny() {
        // Arrange
        String token = "test.jwt.token";
        User user = new User();
        user.setEmail("test@example.com");
        user.setStreak_count(0);

        Badge badge1 = new Badge(1, "First Badge", "First achievement", RequirementTypeEnums.STREAK, 3);
        Badge badge2 = new Badge(2, "Second Badge", "Second achievement", RequirementTypeEnums.STREAK, 7);

        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(badgeRepository.findAllByOrderByRequirementValueAsc()).thenReturn(List.of(badge1, badge2));
        when(userBadgeRepository.findByUserAndBadge(user, badge1)).thenReturn(Optional.empty());

        // Act
        BadgeProgressDTO result = badgeService.getProgress(token);

        // Assert
        assertNotNull(result);
        assertEquals("First Badge", result.getNextBadgeName());
        assertEquals(3, result.getDaysRemaining());
        assertEquals(3, result.getTargetValue());
        assertEquals(0, result.getCurrentStreak());

        verify(userBadgeRepository).findByUserAndBadge(user, badge1);
    }

    @Test
    void shouldReturnNextUnearnedBadgeWhenUserHasEarnedSome() {
        // Arrange
        String token = "test.jwt.token";
        User user = new User();
        user.setEmail("test@example.com");
        user.setStreak_count(5);

        Badge badge1 = new Badge(1, "First Badge", "First achievement", RequirementTypeEnums.STREAK, 3);
        Badge badge2 = new Badge(2, "Second Badge", "Second achievement", RequirementTypeEnums.STREAK, 7);
        Badge badge3 = new Badge(3, "Third Badge", "Third achievement", RequirementTypeEnums.STREAK, 30);

        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(badgeRepository.findAllByOrderByRequirementValueAsc()).thenReturn(List.of(badge1, badge2, badge3));

        // User hasn't earned badge2 yet (and streak < requirement)
        when(userBadgeRepository.findByUserAndBadge(user, badge2)).thenReturn(Optional.empty());

        // Act
        BadgeProgressDTO result = badgeService.getProgress(token);

        // Assert
        assertNotNull(result);
        assertEquals("Second Badge", result.getNextBadgeName());
        assertEquals(2, result.getDaysRemaining());
        assertEquals(7, result.getTargetValue());
        assertEquals(5, result.getCurrentStreak());

        verify(userBadgeRepository).findByUserAndBadge(user, badge2);
    }

    @Test
    void shouldSkipBadgesWhereStreakExceedsRequirement() {
        // Arrange - User has streak of 5, which exceeds badge1 (3) but hasn't earned it
        // The logic will skip badge1 because streak >= requirement, even if not earned
        String token = "test.jwt.token";
        User user = new User();
        user.setEmail("test@example.com");
        user.setStreak_count(5);

        Badge badge1 = new Badge(1, "First Badge", "First achievement", RequirementTypeEnums.STREAK, 3);
        Badge badge2 = new Badge(2, "Second Badge", "Second achievement", RequirementTypeEnums.STREAK, 7);

        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(badgeRepository.findAllByOrderByRequirementValueAsc()).thenReturn(List.of(badge1, badge2));

        // badge1 is skipped because streak (5) >= requirement (3), even if not earned
        // badge2 is checked because streak (5) < requirement (7)
        when(userBadgeRepository.findByUserAndBadge(user, badge2)).thenReturn(Optional.empty());

        // Act
        BadgeProgressDTO result = badgeService.getProgress(token);

        // Assert
        assertNotNull(result);
        assertEquals("Second Badge", result.getNextBadgeName());
        assertEquals(2, result.getDaysRemaining());

        // badge1 is never checked because the first condition fails
        verify(userBadgeRepository, never()).findByUserAndBadge(user, badge1);
        verify(userBadgeRepository).findByUserAndBadge(user, badge2);
    }

    @Test
    void shouldReturnNoMoreBadgesWhenStreakExceedsAllRequirements() {
        // Arrange
        String token = "test.jwt.token";
        User user = new User();
        user.setEmail("test@example.com");
        user.setStreak_count(100);

        Badge badge1 = new Badge(1, "First Badge", "First achievement", RequirementTypeEnums.STREAK, 3);
        Badge badge2 = new Badge(2, "Second Badge", "Second achievement", RequirementTypeEnums.STREAK, 7);
        Badge badge3 = new Badge(3, "Third Badge", "Third achievement", RequirementTypeEnums.STREAK, 30);

        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(badgeRepository.findAllByOrderByRequirementValueAsc()).thenReturn(List.of(badge1, badge2, badge3));

        // Act
        BadgeProgressDTO result = badgeService.getProgress(token);

        // Assert
        assertNotNull(result);
        assertEquals("No Badges Earned", result.getNextBadgeName());
        assertEquals(0, result.getDaysRemaining());
        assertEquals(0, result.getTargetValue());
        assertEquals(0, result.getCurrentStreak());

        // No badges are checked because streak exceeds all requirements
        verify(userBadgeRepository, never()).findByUserAndBadge(any(), any());
    }

    @Test
    void shouldReturnCorrectProgressWhenUserIsOneAwayFromNextBadge() {
        // Arrange
        String token = "test.jwt.token";
        User user = new User();
        user.setEmail("test@example.com");
        user.setStreak_count(6);

        Badge badge1 = new Badge(1, "Week Warrior", "7 day streak", RequirementTypeEnums.STREAK, 7);

        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(badgeRepository.findAllByOrderByRequirementValueAsc()).thenReturn(List.of(badge1));
        when(userBadgeRepository.findByUserAndBadge(user, badge1)).thenReturn(Optional.empty());

        // Act
        BadgeProgressDTO result = badgeService.getProgress(token);

        // Assert
        assertNotNull(result);
        assertEquals("Week Warrior", result.getNextBadgeName());
        assertEquals(1, result.getDaysRemaining());
        assertEquals(7, result.getTargetValue());
        assertEquals(6, result.getCurrentStreak());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        String token = "invalid.jwt.token";

        when(jwtService.extractUsername(token)).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> badgeService.getProgress(token));
    }

    @Test
    void shouldHandleEmptyBadgeList() {
        // Arrange
        String token = "test.jwt.token";
        User user = new User();
        user.setEmail("test@example.com");
        user.setStreak_count(5);

        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(badgeRepository.findAllByOrderByRequirementValueAsc()).thenReturn(List.of());

        // Act
        BadgeProgressDTO result = badgeService.getProgress(token);

        // Assert
        assertNotNull(result);
        assertEquals("No Badges Earned", result.getNextBadgeName());
        assertEquals(0, result.getDaysRemaining());
        assertEquals(0, result.getTargetValue());
        assertEquals(0, result.getCurrentStreak());
    }

    @Test
    void shouldReturnFirstBadgeWhenUserStreakEqualsZero() {
        // Arrange
        String token = "test.jwt.token";
        User user = new User();
        user.setEmail("test@example.com");
        user.setStreak_count(0);

        Badge badge = new Badge(1, "Starter", "Begin your journey", RequirementTypeEnums.STREAK, 1);

        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(badgeRepository.findAllByOrderByRequirementValueAsc()).thenReturn(List.of(badge));
        when(userBadgeRepository.findByUserAndBadge(user, badge)).thenReturn(Optional.empty());

        // Act
        BadgeProgressDTO result = badgeService.getProgress(token);

        // Assert
        assertNotNull(result);
        assertEquals("Starter", result.getNextBadgeName());
        assertEquals(1, result.getDaysRemaining());
        assertEquals(1, result.getTargetValue());
        assertEquals(0, result.getCurrentStreak());
    }

    @Test
    void shouldSkipEarnedBadgeAndReturnNextUnearned() {
        // Arrange - User has streak 2, badge1 requires 3, badge2 requires 7
        // User earned badge1 already (maybe from before), so skip to badge2
        String token = "test.jwt.token";
        User user = new User();
        user.setEmail("test@example.com");
        user.setStreak_count(2);

        Badge badge1 = new Badge(1, "First Badge", "First achievement", RequirementTypeEnums.STREAK, 3);
        Badge badge2 = new Badge(2, "Second Badge", "Second achievement", RequirementTypeEnums.STREAK, 7);

        UserBadge userBadge1 = new UserBadge();

        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(badgeRepository.findAllByOrderByRequirementValueAsc()).thenReturn(List.of(badge1, badge2));

        // User already earned badge1
        when(userBadgeRepository.findByUserAndBadge(user, badge1)).thenReturn(Optional.of(userBadge1));
        when(userBadgeRepository.findByUserAndBadge(user, badge2)).thenReturn(Optional.empty());

        // Act
        BadgeProgressDTO result = badgeService.getProgress(token);

        // Assert
        assertNotNull(result);
        assertEquals("Second Badge", result.getNextBadgeName());
        assertEquals(5, result.getDaysRemaining());
        assertEquals(7, result.getTargetValue());
        assertEquals(2, result.getCurrentStreak());

        verify(userBadgeRepository).findByUserAndBadge(user, badge1);
        verify(userBadgeRepository).findByUserAndBadge(user, badge2);
    }
}