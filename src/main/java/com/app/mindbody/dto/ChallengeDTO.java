package com.app.mindbody.dto;


import com.app.mindbody.enums.ChallengeType;
import com.app.mindbody.models.Challenge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeDTO {
    private String challenge_name;
    private Integer challenge_id;
    private String challenge_description;
    private ChallengeType challenge_type;
    private Integer requirementChallengeValue;
    private Integer points;
    private boolean completed;

    public static ChallengeDTO fromEntity(Challenge challenge, boolean completed) {
        // Extract the badge object from the relationship

        // Build and return the DTO with values from both UserBadge and Badge
        return ChallengeDTO.builder()
                .challenge_id(challenge.getId())
                .challenge_name(challenge.getChallenge_name())
                .challenge_description(challenge.getChallenge_description())
                .challenge_type(challenge.getChallenge_type())
                .requirementChallengeValue(challenge.getRequirementChallengeValue())
                .points(challenge.getPoints())
                .completed(completed)
                .build();
    }
}
