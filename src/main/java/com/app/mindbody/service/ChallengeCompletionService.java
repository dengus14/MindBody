package com.app.mindbody.service;

import com.app.mindbody.dto.ClaimChallengeResponse;
import com.app.mindbody.models.UserChallenge;
import com.app.mindbody.models.UserProfile;
import com.app.mindbody.repositories.UserProfileChallengeRepository;
import com.app.mindbody.repositories.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeCompletionService {

    private final UserProfileChallengeRepository userProfileChallengeRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public ClaimChallengeResponse claimChallenge(Long userChallengeId, UserProfile user) {
        UserChallenge userChallenge = userProfileChallengeRepository.findById(userChallengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        // Validate ownership
        if (!userChallenge.getUserProfile().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: Challenge does not belong to this user");
        }

        // Validate completion status
        if (!userChallenge.isCompleted()) {
            return ClaimChallengeResponse.builder()
                    .success(false)
                    .pointsAwarded(0)
                    .newTotalPoints(user.getPoints())
                    .message("Challenge not yet completed")
                    .build();
        }

        // Validate not already claimed
        if (userChallenge.isClaimed()) {
            return ClaimChallengeResponse.builder()
                    .success(false)
                    .pointsAwarded(0)
                    .newTotalPoints(user.getPoints())
                    .message("Challenge already claimed")
                    .build();
        }

        // Award points
        Integer pointsAwarded = userChallenge.getChallenge().getPoints();
        user.setPoints(user.getPoints() + pointsAwarded);

        userChallenge.setClaimed(true);
        userChallenge.setClaimedAt(LocalDate.now());

        userProfileChallengeRepository.save(userChallenge);
        userProfileRepository.save(user);

        log.info("User {} claimed challenge {} and earned {} points",
                user.getAuth().getUsername(),
                userChallenge.getChallenge().getChallengeName(),
                pointsAwarded);

        return ClaimChallengeResponse.builder()
                .success(true)
                .pointsAwarded(pointsAwarded)
                .newTotalPoints(user.getPoints())
                .message("Challenge claimed successfully!")
                .build();
    }
}