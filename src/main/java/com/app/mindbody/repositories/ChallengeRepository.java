package com.app.mindbody.repositories;

import com.app.mindbody.enums.ChallengeType;
import com.app.mindbody.models.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {

    List<Challenge> findAllByOrderByRequirementValueAsc();


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