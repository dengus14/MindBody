package com.app.mindbody.service;


import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.BadgeDTO;
import com.app.mindbody.dto.BadgeProgressDTO;
import com.app.mindbody.models.Badge;
import com.app.mindbody.models.User;
import com.app.mindbody.models.UserBadge;
import com.app.mindbody.repositories.BadgeRepository;
import com.app.mindbody.repositories.UserBadgeRepository;
import com.app.mindbody.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {


    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final JwtService jwtService;
    private final BadgeServiceCalculator badgeServiceCalculator;


    public List<BadgeDTO> getProgress(String token) {


        String username = jwtService.extractUsername(token);
        var user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        List<Badge> allBadges = badgeRepository.findAllByOrderByRequirementValueAsc();
        List<BadgeDTO> retList = new ArrayList<BadgeDTO>();

        for (Badge badge : allBadges) {
            int progrValue = badgeServiceCalculator.getProgressValue(badge, user);
            BadgeDTO toList = BadgeDTO.fromEntity(badge, progrValue);
            retList.add(toList);
        }
        return retList;
    }

    public void awardBadges(User user) {
        List<Badge> badgesList = badgeRepository.findAllByOrderByRequirementValueAsc();

        for (Badge badge : badgesList) {
            boolean alreadyHasBadge = userBadgeRepository.findByUserAndBadge(user, badge).isPresent();
            if (alreadyHasBadge) continue;

            boolean qualifies = switch (badge.getRequirement_type()) {
                case STREAK -> user.getStreak_count() >= badge.getRequirementValue();
                case WORKOUT_COUNT -> user.getTotalWorkouts() >= badge.getRequirementValue();
                case DURATION -> user.getLongest_workout() >= badge.getRequirementValue();
                case TOTAL_DURATION -> user.getTotalMinutes() >= badge.getRequirementValue();
                case MORNING_WORKOUTS -> user.getTotalMornings() >= badge.getRequirementValue();
                case EVENING_WORKOUTS -> user.getTotalEvenings() >= badge.getRequirementValue();
                default -> false;
            };

            if (qualifies) {
                UserBadge userBadge = new UserBadge();
                userBadge.setUser(user);
                userBadge.setBadge(badge);
                userBadgeRepository.save(userBadge);
            }
        }
    }
}