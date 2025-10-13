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

    @PostConstruct // runs once when the Spring context finishes loading
    public void seedBadges() {
        if (badgeRepository.count() == 0) { // only seed if table is empty
            List<Badge> defaultBadges = List.of(
                    new Badge(0, "Consistency Rookie", "Log workouts 3 days in a row", RequirementTypeEnums.STREAK,3),
                    new Badge(0, "Week Warrior", "Hit a 7-day workout streak", RequirementTypeEnums.STREAK,7),
                    new Badge(0, "Unstoppable", "Achieve a 30-day streak", RequirementTypeEnums.STREAK,30)
            );

            badgeRepository.saveAll(defaultBadges);
            System.out.println("✅ Default badges seeded successfully!");
        } else {
            System.out.println("ℹ️ Badges already exist — skipping seeding.");
        }
    }
}
