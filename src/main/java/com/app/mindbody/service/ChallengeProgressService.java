package com.app.mindbody.service;

import com.app.mindbody.models.UserChallenge;
import com.app.mindbody.models.UserProfile;
import com.app.mindbody.repositories.UserProfileChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeProgressService {

    private final UserProfileChallengeRepository userProfileChallengeRepository;
    private final ChallengeCalculator challengeCalculator;


    @Transactional
    public void updateAllChallengeProgress(UserProfile user) {
        List<UserChallenge> activeChallenges = userProfileChallengeRepository
                .findAllByUserProfile(user)
                .stream()
                .filter(uc -> !uc.isCompleted())
                .toList();

        for (UserChallenge userChallenge : activeChallenges) {
            updateChallengeProgress(userChallenge, user);
        }

        if (!activeChallenges.isEmpty()) {
            userProfileChallengeRepository.saveAll(activeChallenges);
            log.info("Updated progress for {} active challenges for user {}",
                    activeChallenges.size(), user.getAuth().getUsername());
        }
    }

    private void updateChallengeProgress(UserChallenge userChallenge, UserProfile user) {
        // Calculate current progress based on user stats
        Integer currentProgress = challengeCalculator.calculateProgress(
                user,
                userChallenge.getChallenge().getChallengeDescription()
        );

        userChallenge.setCurrentProgress(currentProgress);

        // Auto-complete if requirement is met
        boolean requirementMet = challengeCalculator.isRequirementMet(
                currentProgress,
                userChallenge.getChallenge().getRequirementValue()
        );

        if (requirementMet && !userChallenge.isCompleted()) {
            userChallenge.setCompleted(true);
            log.info("Challenge auto-completed: {} for user {}",
                    userChallenge.getChallenge().getChallengeName(),
                    user.getAuth().getUsername());
        }
    }
}