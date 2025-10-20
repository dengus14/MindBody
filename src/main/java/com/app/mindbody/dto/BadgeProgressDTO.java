package com.app.mindbody.dto;

import com.app.mindbody.enums.RequirementTypeEnums;
import com.app.mindbody.models.Badge;
import com.app.mindbody.models.UserBadge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BadgeProgressDTO {
    private String nextBadgeName;
    private int daysRemaining;
    private int targetValue;
    private int currentStreak;

    public static BadgeProgressDTO getProgressDTO(int daysRemaining, int targetValue, int currentStreak, String nextBadgeName ) {
        // Extract the badge object from the relationship


        // Build and return the DTO with values from both UserBadge and Badge
        return BadgeProgressDTO.builder()
                .nextBadgeName(nextBadgeName)
                .daysRemaining(daysRemaining)
                .targetValue(targetValue)
                .currentStreak(currentStreak)
                .build();
    }
}
