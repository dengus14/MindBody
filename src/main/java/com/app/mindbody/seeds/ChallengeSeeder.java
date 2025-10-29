package com.app.mindbody.seeds;

import com.app.mindbody.enums.ChallengeType;
import com.app.mindbody.models.Challenge;
import com.app.mindbody.repositories.ChallengeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChallengeSeeder {

    private final ChallengeRepository challengeRepository;

    @PostConstruct
    public void seedChallenges() {
        if (challengeRepository.count() == 0) {

            List<Challenge> challenges = new ArrayList<>();



            challenges.add(new Challenge(0, "Early Bird", "Complete a workout before 8 AM", ChallengeType.DAILY, 30, 8));
            challenges.add(new Challenge(0, "Night Owl", "Complete a workout after 9 PM", ChallengeType.DAILY, 30, 21));
            challenges.add(new Challenge(0, "Speed Demon", "Finish a workout in under 20 minutes", ChallengeType.DAILY, 25, 20));
            challenges.add(new Challenge(0, "Endurance Test", "Complete a workout over 60 minutes", ChallengeType.DAILY, 50, 60));
            challenges.add(new Challenge(0, "Double Down", "Log 2 workouts today", ChallengeType.DAILY, 40, 2));
            challenges.add(new Challenge(0, "Triple Threat", "Complete 3 workouts today", ChallengeType.DAILY, 60, 3));
            challenges.add(new Challenge(0, "Century Club", "Log 100 total minutes today", ChallengeType.DAILY, 60, 100));
            challenges.add(new Challenge(0, "Half Hour Hero", "Complete exactly 30 minutes", ChallengeType.DAILY, 30, 30));
            challenges.add(new Challenge(0, "Push Day", "Complete a push workout", ChallengeType.DAILY, 25, 1));
            challenges.add(new Challenge(0, "Pull Power", "Complete a pull workout", ChallengeType.DAILY, 25, 2));
            challenges.add(new Challenge(0, "Leg Day Legend", "Complete a legs workout", ChallengeType.DAILY, 30, 3));
            challenges.add(new Challenge(0, "Cardio King", "Complete a cardio session", ChallengeType.DAILY, 25, 4));
            challenges.add(new Challenge(0, "Core Commander", "Complete a core workout", ChallengeType.DAILY, 20, 5));
            challenges.add(new Challenge(0, "Full Body Blitz", "Complete a full body workout", ChallengeType.DAILY, 35, 6));
            challenges.add(new Challenge(0, "Morning & Night", "Workout once before noon and once after 6 PM", ChallengeType.DAILY, 45, 2));
            challenges.add(new Challenge(0, "Variety Pack", "Complete 2 different workout types", ChallengeType.DAILY, 40, 2));
            challenges.add(new Challenge(0, "Weekend Warrior", "Complete a workout on Saturday or Sunday", ChallengeType.DAILY, 25, 1));
            challenges.add(new Challenge(0, "Weekday Grinder", "Complete a workout Monday-Friday", ChallengeType.DAILY, 20, 1));
            challenges.add(new Challenge(0, "Lunch Break Burn", "Workout between 11 AM - 2 PM", ChallengeType.DAILY, 30, 11));
            challenges.add(new Challenge(0, "Beat Yesterday", "Complete a longer workout than yesterday", ChallengeType.DAILY, 35, 1));
            challenges.add(new Challenge(0, "Consistency Check", "Maintain your current streak", ChallengeType.DAILY, 20, 1));
            challenges.add(new Challenge(0, "Comeback Kid", "Workout after missing a day", ChallengeType.DAILY, 30, 1));
            challenges.add(new Challenge(0, "Marathon Session", "Complete a 90+ minute workout", ChallengeType.DAILY, 70, 90));
            challenges.add(new Challenge(0, "Power Hour", "Log exactly 60 minutes", ChallengeType.DAILY, 40, 60));
            challenges.add(new Challenge(0, "Quick Burst", "Complete a 10-minute workout", ChallengeType.DAILY, 15, 10));
            challenges.add(new Challenge(0, "Two Hour Beast", "Log 120 total minutes today", ChallengeType.DAILY, 80, 120));
            challenges.add(new Challenge(0, "Type Explorer", "Try a workout type you haven't done this week", ChallengeType.DAILY, 35, 1));
            challenges.add(new Challenge(0, "Prime Time", "Workout during peak hours (5-8 PM)", ChallengeType.DAILY, 25, 17));
            challenges.add(new Challenge(0, "Off-Peak Hero", "Workout during off-hours (9 AM-4 PM)", ChallengeType.DAILY, 25, 9));
            challenges.add(new Challenge(0, "Dawn to Dusk", "Complete workouts in morning, afternoon, AND evening", ChallengeType.DAILY, 70, 3));



            challenges.add(new Challenge(0, "Consistency King", "Work out 5 times this week", ChallengeType.WEEKLY, 75, 5));
            challenges.add(new Challenge(0, "Daily Dedication", "Work out 7 days this week", ChallengeType.WEEKLY, 100, 7));
            challenges.add(new Challenge(0, "Three's Company", "Work out 3 times this week", ChallengeType.WEEKLY, 50, 3));
            challenges.add(new Challenge(0, "Perfect Pair", "Work out exactly 2 times this week", ChallengeType.WEEKLY, 40, 2));
            challenges.add(new Challenge(0, "Time Collector", "Log 300 total minutes this week", ChallengeType.WEEKLY, 80, 300));
            challenges.add(new Challenge(0, "Five Hour Club", "Log 300+ minutes this week", ChallengeType.WEEKLY, 100, 300));
            challenges.add(new Challenge(0, "Hour Hunter", "Log 60+ minutes this week", ChallengeType.WEEKLY, 40, 60));
            challenges.add(new Challenge(0, "Two Hour Target", "Log 120+ minutes this week", ChallengeType.WEEKLY, 55, 120));
            challenges.add(new Challenge(0, "Ten Hour Titan", "Log 600+ minutes this week", ChallengeType.WEEKLY, 150, 600));
            challenges.add(new Challenge(0, "Type Master", "Complete all 6 workout types this week", ChallengeType.WEEKLY, 100, 6));
            challenges.add(new Challenge(0, "Variety Seeker", "Complete 4 different workout types this week", ChallengeType.WEEKLY, 70, 4));
            challenges.add(new Challenge(0, "Triple Type", "Complete 3 different workout types this week", ChallengeType.WEEKLY, 50, 3));
            challenges.add(new Challenge(0, "Push Week", "Complete 3 push workouts this week", ChallengeType.WEEKLY, 60, 3));
            challenges.add(new Challenge(0, "Pull Week", "Complete 3 pull workouts this week", ChallengeType.WEEKLY, 60, 3));
            challenges.add(new Challenge(0, "Leg Week", "Complete 2 leg workouts this week", ChallengeType.WEEKLY, 55, 2));
            challenges.add(new Challenge(0, "Cardio Week", "Complete 4 cardio sessions this week", ChallengeType.WEEKLY, 65, 4));
            challenges.add(new Challenge(0, "Morning Routine", "Complete 5 morning workouts this week", ChallengeType.WEEKLY, 80, 5));
            challenges.add(new Challenge(0, "Night Shift", "Complete 4 evening workouts this week", ChallengeType.WEEKLY, 70, 4));
            challenges.add(new Challenge(0, "Early Riser", "Complete 3 workouts before 8 AM this week", ChallengeType.WEEKLY, 75, 3));
            challenges.add(new Challenge(0, "Late Night Grinder", "Complete 3 workouts after 9 PM this week", ChallengeType.WEEKLY, 75, 3));
            challenges.add(new Challenge(0, "Weekend Warrior", "Work out both Saturday and Sunday", ChallengeType.WEEKLY, 50, 2));
            challenges.add(new Challenge(0, "Weekday Warrior", "Work out all 5 weekdays", ChallengeType.WEEKLY, 85, 5));
            challenges.add(new Challenge(0, "No Skip Week", "Work out every single day this week", ChallengeType.WEEKLY, 100, 7));
            challenges.add(new Challenge(0, "Long Session Week", "Complete 3 workouts over 45 minutes this week", ChallengeType.WEEKLY, 80, 3));
            challenges.add(new Challenge(0, "Speed Week", "Complete 5 workouts under 25 minutes this week", ChallengeType.WEEKLY, 70, 5));
            challenges.add(new Challenge(0, "Balanced Week", "Work out at least once in morning, afternoon, and evening", ChallengeType.WEEKLY, 60, 3));
            challenges.add(new Challenge(0, "Double Up Week", "Complete 2 workouts in one day, twice this week", ChallengeType.WEEKLY, 85, 2));
            challenges.add(new Challenge(0, "Progressive Week", "Increase your workout time each day for 5 days", ChallengeType.WEEKLY, 90, 5));
            challenges.add(new Challenge(0, "Endurance Week", "Complete 2 workouts over 60 minutes this week", ChallengeType.WEEKLY, 85, 2));
            challenges.add(new Challenge(0, "Streak Builder", "Maintain a 7-day streak this week", ChallengeType.WEEKLY, 100, 7));

            challengeRepository.saveAll(challenges);
            System.out.println("✅ 60 Unique challenges seeded successfully (30 Daily + 30 Weekly)!");
        } else {
            System.out.println("ℹ️ Challenges already exist — skipping seeding.");
        }
    }
}
