package com.app.mindbody.seeds;

import com.app.mindbody.enums.ChallengeType;
import com.app.mindbody.models.Challenge;
import com.app.mindbody.repositories.ChallengeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChallengeSeeder {

    private final ChallengeRepository challengeRepository;

    @PostConstruct
    public void seedChallenges() {
        // Only seed if database is empty
        if (challengeRepository.count() > 0) {
            log.info("Challenges already seeded, skipping...");
            return;
        }

        List<Challenge> challenges = new ArrayList<>();


        challenges.add(Challenge.builder()
                .challengeName("Morning Warrior")
                .challengeDescription("Complete a workout before 12 PM")
                .type(ChallengeType.DAILY)
                .requirementValue(1)
                .points(15)
                .build());

        challenges.add(Challenge.builder()
                .challengeName("Double Duty")
                .challengeDescription("Complete 2 workouts in one day")
                .type(ChallengeType.DAILY)
                .requirementValue(2)
                .points(25)
                .build());

        challenges.add(Challenge.builder()
                .challengeName("Power Hour")
                .challengeDescription("Complete a 60-minute workout")
                .type(ChallengeType.DAILY)
                .requirementValue(60)
                .points(20)
                .build());

        challenges.add(Challenge.builder()
                .challengeName("Quick Burn")
                .challengeDescription("Complete a 30-minute workout")
                .type(ChallengeType.DAILY)
                .requirementValue(30)
                .points(10)
                .build());

        challenges.add(Challenge.builder()
                .challengeName("Push Day Pro")
                .challengeDescription("Complete a PUSH workout")
                .type(ChallengeType.DAILY)
                .requirementValue(1)
                .points(15)
                .build());

        challenges.add(Challenge.builder()
                .challengeName("Pull Day Champion")
                .challengeDescription("Complete a PULL workout")
                .type(ChallengeType.DAILY)
                .requirementValue(1)
                .points(15)
                .build());

        challenges.add(Challenge.builder()
                .challengeName("Leg Day Hero")
                .challengeDescription("Complete a LEGS workout")
                .type(ChallengeType.DAILY)
                .requirementValue(1)
                .points(15)
                .build());

        // ===== WEEKLY CHALLENGES =====
        challenges.add(Challenge.builder()
                .challengeName("Consistency King")
                .challengeDescription("Complete 5 workouts this week")
                .type(ChallengeType.WEEKLY)
                .requirementValue(5)
                .points(50)
                .build());

        challenges.add(Challenge.builder()
                .challengeName("Streak Builder")
                .challengeDescription("Maintain a 7-day workout streak")
                .type(ChallengeType.WEEKLY)
                .requirementValue(7)
                .points(75)
                .build());

        challenges.add(Challenge.builder()
                .challengeName("Time Master")
                .challengeDescription("Log 300 minutes of workouts this week")
                .type(ChallengeType.WEEKLY)
                .requirementValue(300)
                .points(60)
                .build());

        challenges.add(Challenge.builder()
                .challengeName("Full Body Focus")
                .challengeDescription("Complete at least 1 PUSH, 1 PULL, and 1 LEGS workout")
                .type(ChallengeType.WEEKLY)
                .requirementValue(3)
                .points(40)
                .build());

        challenges.add(Challenge.builder()
                .challengeName("Early Riser")
                .challengeDescription("Complete 3 morning workouts this week")
                .type(ChallengeType.WEEKLY)
                .requirementValue(3)
                .points(35)
                .build());

        challenges.add(Challenge.builder()
                .challengeName("Night Owl")
                .challengeDescription("Complete 3 evening workouts this week")
                .type(ChallengeType.WEEKLY)
                .requirementValue(3)
                .points(35)
                .build());

        challenges.add(Challenge.builder()
                .challengeName("Marathon Week")
                .challengeDescription("Complete 10 workouts in one week")
                .type(ChallengeType.WEEKLY)
                .requirementValue(10)
                .points(100)
                .build());

        // Save all challenges in batch
        challengeRepository.saveAll(challenges);
        log.info("Successfully seeded {} challenges ({} daily, {} weekly)",
                challenges.size(),
                challenges.stream().filter(c -> c.getType() == ChallengeType.DAILY).count(),
                challenges.stream().filter(c -> c.getType() == ChallengeType.WEEKLY).count()
        );
    }
}