package com.app.mindbody.service;

import com.app.mindbody.enums.ChallengeType;
import com.app.mindbody.models.Challenge;
import com.app.mindbody.models.User;
import com.app.mindbody.models.UserChallenge;
import com.app.mindbody.repositories.ChallengeRepository;
import com.app.mindbody.repositories.UserChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeAssignmentService {

    private static final int CHALLENGES_PER_DAY = 3;
    private static final int CHALLENGES_PER_WEEK = 3;
    private static final int AVOID_RECENT_DAYS = 7; // Don't repeat challenges from last 7 days
    private static final int AVOID_RECENT_WEEKS = 4; // Don't repeat challenges from last 4 weeks

    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;


    @Transactional
    public void assignDailyChallenges(User user, LocalDate date) {
        boolean alreadyAssigned = userChallengeRepository
                .existsByUserAndChallenge_TypeAndAssignedDate(user, ChallengeType.DAILY, date);

        if (alreadyAssigned) {
            log.debug("Daily challenges already assigned for user {} on {}", user.getUsername(), date);
            return;
        }

        List<Challenge> selectedChallenges = selectRandomChallenges(
                user,
                ChallengeType.DAILY,
                CHALLENGES_PER_DAY,
                date.minusDays(AVOID_RECENT_DAYS)
        );

        List<UserChallenge> userChallenges = new ArrayList<>();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int year = date.get(weekFields.weekBasedYear());
        int weekNumber = date.get(weekFields.weekOfWeekBasedYear());

        for (Challenge challenge : selectedChallenges) {
            UserChallenge userChallenge = UserChallenge.builder()
                    .user(user)
                    .challenge(challenge)
                    .assignedAt(date)
                    .assignedDate(date)
                    .assignedWeekYear(year)
                    .assignedWeekNumber(weekNumber)
                    .completed(false)
                    .currentProgress(0)
                    .claimed(false)
                    .build();
            userChallenges.add(userChallenge);
        }

        userChallengeRepository.saveAll(userChallenges);
        log.info("Assigned {} daily challenges to user {}", selectedChallenges.size(), user.getUsername());
    }


    @Transactional
    public void assignWeeklyChallenges(User user, LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int year = date.get(weekFields.weekBasedYear());
        int weekNumber = date.get(weekFields.weekOfWeekBasedYear());

        boolean alreadyAssigned = userChallengeRepository
                .existsByUserAndChallenge_TypeAndAssignedWeekYearAndAssignedWeekNumber(
                        user, ChallengeType.WEEKLY, year, weekNumber
                );

        if (alreadyAssigned) {
            log.debug("Weekly challenges already assigned for user {} in week {}-{}",
                    user.getUsername(), year, weekNumber);
            return;
        }

        List<Challenge> selectedChallenges = selectRandomChallenges(
                user,
                ChallengeType.WEEKLY,
                CHALLENGES_PER_WEEK,
                date.minusWeeks(AVOID_RECENT_WEEKS)
        );

        List<UserChallenge> userChallenges = new ArrayList<>();

        for (Challenge challenge : selectedChallenges) {
            UserChallenge userChallenge = UserChallenge.builder()
                    .user(user)
                    .challenge(challenge)
                    .assignedAt(date)
                    .assignedDate(date)
                    .assignedWeekYear(year)
                    .assignedWeekNumber(weekNumber)
                    .completed(false)
                    .currentProgress(0)
                    .claimed(false)
                    .build();
            userChallenges.add(userChallenge);
        }

        userChallengeRepository.saveAll(userChallenges);
        log.info("Assigned {} weekly challenges to user {} for week {}-{}",
                selectedChallenges.size(), user.getUsername(), year, weekNumber);
    }


    private List<Challenge> selectRandomChallenges(
            User user,
            ChallengeType type,
            int count,
            LocalDate avoidSince
    ) {
        // Get recently assigned challenge IDs to avoid
        List<Integer> recentIds = userChallengeRepository.findRecentChallengeIds(
                user, type, avoidSince
        );

        List<Challenge> challenges;

        if (recentIds.isEmpty()) {
            // No recent challenges to exclude
            challenges = challengeRepository.findRandomByType(type.name(), count);
        } else {
            // Try to get challenges excluding recent ones
            challenges = challengeRepository.findRandomByTypeExcluding(
                    type.name(), recentIds, count
            );

            // If not enough challenges available (excluding recent), fall back to random
            if (challenges.size() < count) {
                log.warn("Not enough {} challenges excluding recent IDs. Falling back to random selection.", type);
                challenges = challengeRepository.findRandomByType(type.name(), count);
            }
        }

        // Shuffle for extra randomness
        Collections.shuffle(challenges);

        return challenges.subList(0, Math.min(count, challenges.size()));
    }
}