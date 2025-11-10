package com.app.mindbody.repositories;

import com.app.mindbody.enums.ChallengeType;
import com.app.mindbody.models.Challenge;
import com.app.mindbody.models.UserProfile;
import com.app.mindbody.models.UserChallenge;
import com.app.mindbody.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {

    Optional<UserChallenge> findByUserAndChallenge(UserProfile user, Challenge challenge);

    List<UserChallenge> findAllByUserProfile(UserProfile user);

    // Daily challenges
    boolean existsByUserProfileAndChallenge_TypeAndAssignedDate(
            UserProfile user,
            ChallengeType type,
            LocalDate assignedDate
    );

    List<UserChallenge> findAllByUserAndAssignedDate(UserProfile user, LocalDate assignedDate);

    // Weekly challenges
    boolean existsByUserProfileAndChallenge_TypeAndAssignedWeekYearAndAssignedWeekNumber(
            UserProfile user,
            ChallengeType type,
            Integer year,
            Integer weekNumber
    );

    List<UserChallenge> findAllByUserAndAssignedWeekYearAndAssignedWeekNumber(
            UserProfile user,
            Integer year,
            Integer weekNumber
    );

    // Get recently assigned challenge IDs (last N days for dailies, last N weeks for weeklies)
    @Query("SELECT uc.challenge.id FROM UserChallenge uc " +
            "WHERE uc.user = :user " +
            "AND uc.challenge.type = :type " +
            "AND uc.assignedDate >= :since")
    List<Integer> findRecentChallengeIds(
            @Param("user") UserProfile user,
            @Param("type") ChallengeType type,
            @Param("since") LocalDate since
    );

    // Get unclaimed completed challenges
    List<UserChallenge> findAllByUserProfileAndCompletedTrueAndClaimedFalse(UserProfile user);

    // Get active challenges (assigned today/this week, not completed or completed but unclaimed)
    @Query("SELECT uc FROM UserChallenge uc " +
            "WHERE uc.user = :user " +
            "AND uc.assignedDate = :date " +
            "AND (uc.completed = false OR (uc.completed = true AND uc.claimed = false))")
    List<UserChallenge> findActiveDailyChallenges(
            @Param("user") UserProfile user,
            @Param("date") LocalDate date
    );

    @Query("SELECT uc FROM UserChallenge uc " +
            "WHERE uc.user = :user " +
            "AND uc.assignedWeekYear = :year " +
            "AND uc.assignedWeekNumber = :weekNumber " +
            "AND (uc.completed = false OR (uc.completed = true AND uc.claimed = false))")
    List<UserChallenge> findActiveWeeklyChallenges(
            @Param("user") UserProfile user,
            @Param("year") Integer year,
            @Param("weekNumber") Integer weekNumber
    );
}