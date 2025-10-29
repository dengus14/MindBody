package com.app.mindbody.repositories;

import com.app.mindbody.enums.ChallengeType;
import com.app.mindbody.models.Challenge;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {
    List<Challenge> findAllByOrderByRequirementValueAsc();

    List<Challenge> findAllByType(ChallengeType type, Pageable pageable);

}
