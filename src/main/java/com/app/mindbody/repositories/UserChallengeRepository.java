package com.app.mindbody.repositories;

import com.app.mindbody.enums.ChallengeType;
import com.app.mindbody.models.Badge;
import com.app.mindbody.models.User;
import com.app.mindbody.models.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.threeten.extra.YearWeek;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
    Optional<UserChallenge> findByUserAndChallenge(User user, Badge badge);
    List<UserChallenge> findAllByUser(User user);
    boolean existsByUserAndChallenge_TypeAndAssignedDate(User user, ChallengeType type, LocalDate assignedDate);

    boolean existsByUserAndChallenge_TypeAndAssignedWeek(User user, ChallengeType type, YearWeek assignedWeek);
}

