package com.app.mindbody.repositories;

import com.app.mindbody.enums.ChallengeType;
import com.app.mindbody.models.Challenge;
import com.app.mindbody.models.User;
import com.app.mindbody.models.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {

    Optional<UserChallenge> findByUserAndChallenge(User user, Challenge challenge);

    List<UserChallenge> findAllByUser(User user);

    // Daily challenges
    boolean existsByUserAndChallenge_TypeAndAssignedDate(
            User user,
            ChallengeType type,
            LocalDate assignedDate
    );

    List<UserChallenge> findAllByUserAndAssignedDate(User user, LocalDate assignedDate);

    // Weekly challenges
    boolean existsByUserAndChallenge_TypeAndAssignedWeekYearAndAssignedWeekNumber(
            User user,
            ChallengeType type,
            Integer year,
            Integer weekNumber
    );

    List<UserChallenge> findAllByUserAndAssignedWeekYearAndAssignedWeekNumber(
            User user,
            Integer year,
            Integer weekNumber
    );

    // Get recently assigned challenge IDs (last N days for dailies, last N weeks for weeklies)
    @Query("SELECT uc.challenge.id FROM UserChallenge uc " +
            "WHERE uc.user = :user " +
            "AND uc.challenge.type = :type " +
            "AND uc.assignedDate >= :since")
    List<Integer> findRecentChallengeIds(
            @Param("user") User user,
            @Param("type") ChallengeType type,
            @Param("since") LocalDate since
    );

    // Get unclaimed completed challenges
    List<UserChallenge> findAllByUserAndCompletedTrueAndClaimedFalse(User user);

    // Get active challenges (assigned today/this week, not completed or completed but unclaimed)
    @Query("SELECT uc FROM UserChallenge uc " +
            "WHERE uc.user = :user " +
            "AND uc.assignedDate = :date " +
            "AND (uc.completed = false OR (uc.completed = true AND uc.claimed = false))")
    List<UserChallenge> findActiveDailyChallenges(
            @Param("user") User user,
            @Param("date") LocalDate date
    );

    @Query("SELECT uc FROM UserChallenge uc " +
            "WHERE uc.user = :user " +
            "AND uc.assignedWeekYear = :year " +
            "AND uc.assignedWeekNumber = :weekNumber " +
            "AND (uc.completed = false OR (uc.completed = true AND uc.claimed = false))")
    List<UserChallenge> findActiveWeeklyChallenges(
            @Param("user") User user,
            @Param("year") Integer year,
            @Param("weekNumber") Integer weekNumber
    );
}