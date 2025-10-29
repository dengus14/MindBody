package com.app.mindbody.models;


import com.app.mindbody.enums.ChallengeType;
import com.app.mindbody.enums.RequirementTypeEnums;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "challenges")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Challenge {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String challenge_name;

    @Column
    private String challenge_description;

    @Column
    @Enumerated(EnumType.STRING)
    private ChallengeType type;


    @Column
    private Integer points;

    @Column
    private Integer requirementChallengeValue;
}
