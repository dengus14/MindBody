package com.app.mindbody.service;

import com.app.mindbody.models.User;
import com.app.mindbody.models.UserChallenge;
import com.app.mindbody.repositories.UserChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeProgressService {

    private final UserChallengeRepository userChallengeRepository;
    private final ChallengeCalculator challengeCalculator;


    @Transactional
    public void updateAllChallengeProgress(User user) {
        List<UserChallenge> activeChallenges = userChallengeRepository
                .findAllByUser(user)
                .stream()
                .filter(uc -> !uc.isCompleted())
                .toList();

        for (UserChallenge userChallenge : activeChallenges) {
            updateChallengeProgress(userChallenge, user);
        }

        if (!activeChallenges.isEmpty()) {
            userChallengeRepository.saveAll(activeChallenges);
            log.info("Updated progress for {} active challenges for user {}",
                    activeChallenges.size(), user.getUsername());
        }
    }

    private void updateChallengeProgress(UserChallenge userChallenge, User user) {
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
                    user.getUsername());
        }
    }
}