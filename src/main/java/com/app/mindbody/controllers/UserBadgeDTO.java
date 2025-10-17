package com.app.mindbody.controllers;


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
public class UserBadgeDTO {
    private String badge_name;
    private int badge_id;
    private String badge_description;
    private LocalDateTime earned_at;
    private RequirementTypeEnums requirement_type;
    private int requirement_value;

    public static UserBadgeDTO fromEntity(UserBadge userBadge) {
        // Extract the badge object from the relationship
        Badge badge = userBadge.getBadge();

        // Build and return the DTO with values from both UserBadge and Badge
        return UserBadgeDTO.builder()
                .badge_id(badge.getId())
                .badge_name(badge.getBadge_name())
                .badge_description(badge.getBadge_description())
                .requirement_type(badge.getRequirement_type())
                .requirement_value(badge.getRequirement_value())
                .earned_at(userBadge.getEarned_at())
                .build();
    }
}
