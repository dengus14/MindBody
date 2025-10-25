package com.app.mindbody.seeds;

import com.app.mindbody.enums.RequirementTypeEnums;
import com.app.mindbody.models.Badge;
import com.app.mindbody.repositories.BadgeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BadgeSeeder {

    private final BadgeRepository badgeRepository;

    @PostConstruct
    public void seedBadges() {
        if (badgeRepository.count() == 0) {

            List<Badge> defaultBadges = List.of(
                    // 🧠 STREAK BADGES
                    new Badge(0, "Consistency Rookie", "Log workouts 3 days in a row", RequirementTypeEnums.STREAK, 3),
                    new Badge(0, "Week Warrior", "Hit a 7-day workout streak", RequirementTypeEnums.STREAK, 7),
                    new Badge(0, "Unstoppable", "Achieve a 30-day workout streak", RequirementTypeEnums.STREAK, 30),
                    new Badge(0, "Iron Will", "Reach a 90-day workout streak", RequirementTypeEnums.STREAK, 90),
                    new Badge(0, "Half-Year Hero", "Maintain a 180-day streak", RequirementTypeEnums.STREAK, 180),
                    new Badge(0, "Full-Year Flame", "Work out 365 days straight", RequirementTypeEnums.STREAK, 365),
                    new Badge(0, "Weekend Grinder", "Hit 14-day streak twice", RequirementTypeEnums.STREAK, 14),
                    new Badge(0, "Early Consistency", "Keep a 10-day streak", RequirementTypeEnums.STREAK, 10),
                    new Badge(0, "Monthly Machine", "Achieve 60-day streak", RequirementTypeEnums.STREAK, 60),
                    new Badge(0, "Relentless", "Achieve 120-day streak", RequirementTypeEnums.STREAK, 120),

                    // 🏋️ TOTAL WORKOUTS
                    new Badge(0, "Getting Started", "Complete 5 total workouts", RequirementTypeEnums.WORKOUT_COUNT, 5),
                    new Badge(0, "Breaking Sweat", "Complete 10 workouts", RequirementTypeEnums.WORKOUT_COUNT, 10),
                    new Badge(0, "Routine Builder", "Complete 25 workouts", RequirementTypeEnums.WORKOUT_COUNT, 25),
                    new Badge(0, "Focused Trainer", "Complete 50 workouts", RequirementTypeEnums.WORKOUT_COUNT, 50),
                    new Badge(0, "Workout King", "Complete 100 workouts", RequirementTypeEnums.WORKOUT_COUNT, 100),
                    new Badge(0, "Dedicated Beast", "Complete 200 workouts", RequirementTypeEnums.WORKOUT_COUNT, 200),
                    new Badge(0, "Relentless Lifter", "Complete 300 workouts", RequirementTypeEnums.WORKOUT_COUNT, 300),
                    new Badge(0, "Endurance Trainer", "Complete 400 workouts", RequirementTypeEnums.WORKOUT_COUNT, 400),
                    new Badge(0, "Never Miss", "Complete 500 workouts", RequirementTypeEnums.WORKOUT_COUNT, 500),
                    new Badge(0, "Legendary Grinder", "Complete 1000 workouts", RequirementTypeEnums.WORKOUT_COUNT, 1000),

                    // ⏱️ DURATION (single session length)
                    new Badge(0, "Warmup Warrior", "Complete a 15-minute workout", RequirementTypeEnums.DURATION, 15),
                    new Badge(0, "Focused Mind", "Complete a 30-minute workout", RequirementTypeEnums.DURATION, 30),
                    new Badge(0, "Endurance Seeker", "Complete a 45-minute workout", RequirementTypeEnums.DURATION, 45),
                    new Badge(0, "Power Hour", "Complete a 60-minute workout", RequirementTypeEnums.DURATION, 60),
                    new Badge(0, "Marathon Session", "Complete a 90-minute workout", RequirementTypeEnums.DURATION, 90),
                    new Badge(0, "Ultra Focused", "Complete a 120-minute workout", RequirementTypeEnums.DURATION, 120),
                    new Badge(0, "Iron Focus", "Complete a 150-minute workout", RequirementTypeEnums.DURATION, 150),
                    new Badge(0, "Never Stop", "Complete a 180-minute workout", RequirementTypeEnums.DURATION, 180),
                    new Badge(0, "Grindlord", "Complete a 210-minute workout", RequirementTypeEnums.DURATION, 210),
                    new Badge(0, "Limitless", "Complete a 240-minute workout", RequirementTypeEnums.DURATION, 240),

                    // ⌛ TOTAL DURATION (all workouts combined)
                    new Badge(0, "Time Investor", "Reach 100 total workout minutes", RequirementTypeEnums.TOTAL_DURATION, 100),
                    new Badge(0, "Time Builder", "Reach 300 total workout minutes", RequirementTypeEnums.TOTAL_DURATION, 300),
                    new Badge(0, "Time Warrior", "Reach 600 total workout minutes", RequirementTypeEnums.TOTAL_DURATION, 600),
                    new Badge(0, "Time Crusher", "Reach 1000 total workout minutes", RequirementTypeEnums.TOTAL_DURATION, 1000),
                    new Badge(0, "Time Dominator", "Reach 2000 total workout minutes", RequirementTypeEnums.TOTAL_DURATION, 2000),
                    new Badge(0, "Time Titan", "Reach 5000 total workout minutes", RequirementTypeEnums.TOTAL_DURATION, 5000),
                    new Badge(0, "Time Machine", "Reach 10,000 total workout minutes", RequirementTypeEnums.TOTAL_DURATION, 10000),
                    new Badge(0, "Chrono Lord", "Reach 20,000 total workout minutes", RequirementTypeEnums.TOTAL_DURATION, 20000),
                    new Badge(0, "Ageless", "Reach 30,000 total workout minutes", RequirementTypeEnums.TOTAL_DURATION, 30000),
                    new Badge(0, "Timeless", "Reach 50,000 total workout minutes", RequirementTypeEnums.TOTAL_DURATION, 50000),

//                    // 🔥 CALORIES BURNED (total)
//                    new Badge(0, "Calorie Burner", "Burn 100 calories total", RequirementTypeEnums.CALORIES_BURNED, 100),
//                    new Badge(0, "Fat Fighter", "Burn 500 calories total", RequirementTypeEnums.CALORIES_BURNED, 500),
//                    new Badge(0, "Sweat Master", "Burn 1000 calories total", RequirementTypeEnums.CALORIES_BURNED, 1000),
//                    new Badge(0, "Firestarter", "Burn 2500 calories total", RequirementTypeEnums.CALORIES_BURNED, 2500),
//                    new Badge(0, "Blaze Mode", "Burn 5000 calories total", RequirementTypeEnums.CALORIES_BURNED, 5000),
//                    new Badge(0, "Inferno", "Burn 10,000 calories total", RequirementTypeEnums.CALORIES_BURNED, 10000),
//                    new Badge(0, "Calorie Crusher", "Burn 25,000 calories total", RequirementTypeEnums.CALORIES_BURNED, 25000),
//                    new Badge(0, "Caloric Hero", "Burn 50,000 calories total", RequirementTypeEnums.CALORIES_BURNED, 50000),
//                    new Badge(0, "Metabolic Legend", "Burn 100,000 calories total", RequirementTypeEnums.CALORIES_BURNED, 100000),
//                    new Badge(0, "Phoenix", "Burn 250,000 calories total", RequirementTypeEnums.CALORIES_BURNED, 250000),

                    // 🏋️ TOTAL WEIGHT LIFTED
//                    new Badge(0, "Beginner Lifter", "Lift a total of 1000 lbs", RequirementTypeEnums.TOTAL_WEIGHT_LIFTED, 1000),
//                    new Badge(0, "Iron Apprentice", "Lift a total of 5000 lbs", RequirementTypeEnums.TOTAL_WEIGHT_LIFTED, 5000),
//                    new Badge(0, "Steel Worker", "Lift a total of 10,000 lbs", RequirementTypeEnums.TOTAL_WEIGHT_LIFTED, 10000),
//                    new Badge(0, "Muscle Maker", "Lift a total of 25,000 lbs", RequirementTypeEnums.TOTAL_WEIGHT_LIFTED, 25000),
//                    new Badge(0, "Gym Warrior", "Lift a total of 50,000 lbs", RequirementTypeEnums.TOTAL_WEIGHT_LIFTED, 50000),
//                    new Badge(0, "Iron Hero", "Lift a total of 75,000 lbs", RequirementTypeEnums.TOTAL_WEIGHT_LIFTED, 75000),
//                    new Badge(0, "Power Titan", "Lift a total of 100,000 lbs", RequirementTypeEnums.TOTAL_WEIGHT_LIFTED, 100000),
//                    new Badge(0, "Mega Lifter", "Lift a total of 150,000 lbs", RequirementTypeEnums.TOTAL_WEIGHT_LIFTED, 150000),
//                    new Badge(0, "Giga Lifter", "Lift a total of 200,000 lbs", RequirementTypeEnums.TOTAL_WEIGHT_LIFTED, 200000),
//                    new Badge(0, "Planet Crusher", "Lift a total of 300,000 lbs", RequirementTypeEnums.TOTAL_WEIGHT_LIFTED, 300000),

//                    // 📅 WORKOUTS PER WEEK
//                    new Badge(0, "Weekend Warrior", "Work out 2 times in one week", RequirementTypeEnums.WORKOUTS_PER_WEEK, 2),
//                    new Badge(0, "Balanced Trainer", "Work out 3 times in one week", RequirementTypeEnums.WORKOUTS_PER_WEEK, 3),
//                    new Badge(0, "Consistent Hustler", "Work out 4 times in one week", RequirementTypeEnums.WORKOUTS_PER_WEEK, 4),
//                    new Badge(0, "Weekly Grinder", "Work out 5 times in one week", RequirementTypeEnums.WORKOUTS_PER_WEEK, 5),
//                    new Badge(0, "Almost Daily", "Work out 6 times in one week", RequirementTypeEnums.WORKOUTS_PER_WEEK, 6),
//                    new Badge(0, "Full Week Beast", "Work out 7 times in one week", RequirementTypeEnums.WORKOUTS_PER_WEEK, 7),
//                    new Badge(0, "Weekly Master", "Work out 5x/week for 4 weeks", RequirementTypeEnums.WORKOUTS_PER_WEEK, 28),
//                    new Badge(0, "Discipline Builder", "Work out 5x/week for 8 weeks", RequirementTypeEnums.WORKOUTS_PER_WEEK, 56),
//                    new Badge(0, "Habit Machine", "Work out 5x/week for 12 weeks", RequirementTypeEnums.WORKOUTS_PER_WEEK, 84),
//                    new Badge(0, "Relentless Routine", "Work out 5x/week for 24 weeks", RequirementTypeEnums.WORKOUTS_PER_WEEK, 168),

                    // 🌅 MORNING WORKOUTS
                    new Badge(0, "Early Bird", "Complete 1 morning workout", RequirementTypeEnums.MORNING_WORKOUTS, 1),
                    new Badge(0, "Rising Trainer", "Complete 5 morning workouts", RequirementTypeEnums.MORNING_WORKOUTS, 5),
                    new Badge(0, "Sunrise Grinder", "Complete 10 morning workouts", RequirementTypeEnums.MORNING_WORKOUTS, 10),
                    new Badge(0, "Morning Momentum", "Complete 25 morning workouts", RequirementTypeEnums.MORNING_WORKOUTS, 25),
                    new Badge(0, "Golden Hour Hustler", "Complete 50 morning workouts", RequirementTypeEnums.MORNING_WORKOUTS, 50),
                    new Badge(0, "5AM Club", "Complete 75 morning workouts", RequirementTypeEnums.MORNING_WORKOUTS, 75),
                    new Badge(0, "Morning Beast", "Complete 100 morning workouts", RequirementTypeEnums.MORNING_WORKOUTS, 100),
                    new Badge(0, "Sunrise Legend", "Complete 150 morning workouts", RequirementTypeEnums.MORNING_WORKOUTS, 150),
                    new Badge(0, "Early Emperor", "Complete 200 morning workouts", RequirementTypeEnums.MORNING_WORKOUTS, 200),
                    new Badge(0, "Morning King", "Complete 300 morning workouts", RequirementTypeEnums.MORNING_WORKOUTS, 300),

                    // 🌙 EVENING WORKOUTS
                    new Badge(0, "Night Owl", "Complete 1 evening workout", RequirementTypeEnums.EVENING_WORKOUTS, 1),
                    new Badge(0, "Twilight Trainer", "Complete 5 evening workouts", RequirementTypeEnums.EVENING_WORKOUTS, 5),
                    new Badge(0, "Dusk Grinder", "Complete 10 evening workouts", RequirementTypeEnums.EVENING_WORKOUTS, 10),
                    new Badge(0, "Late Lifter", "Complete 25 evening workouts", RequirementTypeEnums.EVENING_WORKOUTS, 25),
                    new Badge(0, "Moonlight Machine", "Complete 50 evening workouts", RequirementTypeEnums.EVENING_WORKOUTS, 50),
                    new Badge(0, "After Hours Hero", "Complete 75 evening workouts", RequirementTypeEnums.EVENING_WORKOUTS, 75),
                    new Badge(0, "Nighttime Hustler", "Complete 100 evening workouts", RequirementTypeEnums.EVENING_WORKOUTS, 100),
                    new Badge(0, "Dark Mode Trainer", "Complete 150 evening workouts", RequirementTypeEnums.EVENING_WORKOUTS, 150),
                    new Badge(0, "Midnight Power", "Complete 200 evening workouts", RequirementTypeEnums.EVENING_WORKOUTS, 200),
                    new Badge(0, "Nocturnal Beast", "Complete 300 evening workouts", RequirementTypeEnums.EVENING_WORKOUTS, 300)
            );

            badgeRepository.saveAll(defaultBadges);
            System.out.println("✅ 100 Default badges seeded successfully!");
        } else {
            System.out.println("ℹ️ Badges already exist — skipping seeding.");
        }
    }
}
