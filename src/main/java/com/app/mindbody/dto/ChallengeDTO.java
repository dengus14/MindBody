package com.app.mindbody.dto;

import com.app.mindbody.enums.ChallengeType;
import com.app.mindbody.models.UserChallenge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeDTO {
    private Long userChallengeId;
    private Integer challengeId;
    private String challengeName;
    private String challengeDescription;
    private ChallengeType challengeType;
    private Integer requirementValue;
    private Integer currentProgress;
    private Integer points;
    private boolean completed;
    private boolean claimed;
    private String assignedDate;

    public static ChallengeDTO fromEntity(UserChallenge userChallenge) {
        return ChallengeDTO.builder()
                .userChallengeId(userChallenge.getId())
                .challengeId(userChallenge.getChallenge().getId())
                .challengeName(userChallenge.getChallenge().getChallengeName())
                .challengeDescription(userChallenge.getChallenge().getChallengeDescription())
                .challengeType(userChallenge.getChallenge().getType())
                .requirementValue(userChallenge.getChallenge().getRequirementValue())
                .currentProgress(userChallenge.getCurrentProgress())
                .points(userChallenge.getChallenge().getPoints())
                .completed(userChallenge.isCompleted())
                .claimed(userChallenge.isClaimed())
                .assignedDate(userChallenge.getAssignedAt().toString())
                .build();
    }
}