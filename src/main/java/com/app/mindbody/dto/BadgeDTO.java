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
public class BadgeDTO {
    private String badge_name;
    private int badge_id;
    private String badge_description;
    private RequirementTypeEnums requirement_type;
    private int requirement_value;
    private int progress_value;
    private boolean completed;

    public static BadgeDTO fromEntity(Badge badge, int progress_value) {



        return BadgeDTO.builder()
                .badge_id(badge.getId())
                .badge_name(badge.getBadge_name())
                .badge_description(badge.getBadge_description())
                .requirement_type(badge.getRequirement_type())
                .requirement_value(badge.getRequirementValue())
                .progress_value(progress_value)
                .completed(progress_value >= badge.getRequirementValue())
                .build();
    }
}
