package com.app.mindbody.models;

import com.app.mindbody.enums.ChallengeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "challenges", indexes = {
        @Index(name = "idx_challenge_type", columnList = "type")
})
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "challenge_name", nullable = false)
    private String challengeName;

    @Column(name = "challenge_description", nullable = false, length = 500)
    private String challengeDescription;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ChallengeType type;

    @Column(name = "points", nullable = false)
    private Integer points;

    @Column(name = "requirement_value", nullable = false)
    private Integer requirementValue;
}