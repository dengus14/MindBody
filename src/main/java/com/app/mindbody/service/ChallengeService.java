package com.app.mindbody.service;

import com.app.mindbody.dto.ChallengeDTO;
import com.app.mindbody.dto.ClaimChallengeRequest;
import com.app.mindbody.dto.ClaimChallengeResponse;
import com.app.mindbody.models.UserChallenge;
import com.app.mindbody.models.UserProfile;
import com.app.mindbody.repositories.UserProfileChallengeRepository;
import com.app.mindbody.repositories.UserProfileRepository; // Add this import
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

    private final UserProfileChallengeRepository userProfileChallengeRepository;
    private final UserProfileRepository userProfileRepository; // Add this
    private final ChallengeAssignmentService assignmentService;
    private final ChallengeCompletionService completionService;
    private final ChallengeProgressService progressService;

    /**
     * Get user's active challenges (daily + weekly for current period)
     */
    @Transactional(readOnly = true)
    public List<ChallengeDTO> getActiveChallenges(UserProfile user) {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int year = today.get(weekFields.weekBasedYear());
        int weekNumber = today.get(weekFields.weekOfWeekBasedYear());

        List<UserChallenge> dailies = userProfileChallengeRepository.findActiveDailyChallenges(user, today);
        List<UserChallenge> weeklies = userProfileChallengeRepository.findActiveWeeklyChallenges(user, year, weekNumber);

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
    public void ensureChallengesAssigned(UserProfile user) {
        // ✅ FIX: Reload the UserProfile from database to ensure it's managed and has valid ID
        UserProfile managedUser = userProfileRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalStateException("User profile not found: " + user.getId()));

        log.debug("Ensuring challenges for user ID: {}, username: {}",
                managedUser.getId(),
                managedUser.getAuth() != null ? managedUser.getAuth().getUsername() : "unknown");

        LocalDate today = LocalDate.now();
        assignmentService.assignDailyChallenges(managedUser, today);
        assignmentService.assignWeeklyChallenges(managedUser, today);
    }

    /**
     * Claim a completed challenge
     */
    @Transactional
    public ClaimChallengeResponse claimChallenge(ClaimChallengeRequest request, UserProfile user) {
        return completionService.claimChallenge(request.getUserChallengeId(), user);
    }

    /**
     * Update challenge progress for a user (call after workouts, achievements, etc.)
     */
    @Transactional
    public void updateUserProgress(UserProfile user) {
        progressService.updateAllChallengeProgress(user);
    }
}