package com.app.mindbody.repositories;

import com.app.mindbody.enums.ChallengeType;
import com.app.mindbody.models.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {

    List<Challenge> findAllByOrderByRequirementValueAsc();

    /**
     * Get random challenges of a specific type, excluding recently assigned ones
     * PostgreSQL uses RANDOM() instead of RAND()
     */
    @Query(value = "SELECT c.* FROM challenges c " +
            "WHERE c.type = :type " +
            "AND c.id NOT IN :excludedIds " +
            "ORDER BY RANDOM() " +
            "LIMIT :limit", nativeQuery = true)
    List<Challenge> findRandomByTypeExcluding(
            @Param("type") String type,
            @Param("excludedIds") List<Integer> excludedIds,
            @Param("limit") int limit
    );

    /**
     * Fallback when not enough challenges to exclude
     */
    @Query(value = "SELECT c.* FROM challenges c " +
            "WHERE c.type = :type " +
            "ORDER BY RANDOM() " +
            "LIMIT :limit", nativeQuery = true)
    List<Challenge> findRandomByType(
            @Param("type") String type,
            @Param("limit") int limit
    );

    List<Challenge> findAllByType(ChallengeType type);
}