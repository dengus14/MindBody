package com.app.mindbody.service;

import com.app.mindbody.dto.ChallengeDTO;
import com.app.mindbody.dto.ClaimChallengeRequest;
import com.app.mindbody.dto.ClaimChallengeResponse;
import com.app.mindbody.models.User;
import com.app.mindbody.models.UserChallenge;
import com.app.mindbody.repositories.UserChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeService {

    private final UserChallengeRepository userChallengeRepository;
    private final ChallengeAssignmentService assignmentService;
    private final ChallengeCompletionService completionService;
    private final ChallengeProgressService progressService;

    /**
     * Get user's active challenges (daily + weekly for current period)
     */
    @Transactional(readOnly = true)
    public List<ChallengeDTO> getActiveChallenges(User user) {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int year = today.get(weekFields.weekBasedYear());
        int weekNumber = today.get(weekFields.weekOfWeekBasedYear());

        List<UserChallenge> dailies = userChallengeRepository.findActiveDailyChallenges(user, today);
        List<UserChallenge> weeklies = userChallengeRepository.findActiveWeeklyChallenges(user, year, weekNumber);

        List<UserChallenge> allActive = new ArrayList<>();
        allActive.addAll(dailies);
        allActive.addAll(weeklies);

        return allActive.stream()
                .map(ChallengeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Ensure user has challenges assigned for today/this week
     */
    @Transactional
    public void ensureChallengesAssigned(User user) {
        LocalDate today = LocalDate.now();
        assignmentService.assignDailyChallenges(user, today);
        assignmentService.assignWeeklyChallenges(user, today);
    }

    /**
     * Claim a completed challenge
     */
    @Transactional
    public ClaimChallengeResponse claimChallenge(ClaimChallengeRequest request, User user) {
        return completionService.claimChallenge(request.getUserChallengeId(), user);
    }

    /**
     * Update challenge progress for a user (call after workouts, achievements, etc.)
     */
    @Transactional
    public void updateUserProgress(User user) {
        progressService.updateAllChallengeProgress(user);
    }
}