package com.app.mindbody.service;

import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.UserBadgeDTO;
import com.app.mindbody.models.Badge;
import com.app.mindbody.models.UserAuth;
import com.app.mindbody.models.UserProfile;
import com.app.mindbody.models.UserBadge;
import com.app.mindbody.repositories.UserAuthRepository;
import com.app.mindbody.repositories.UserProfileRepository;
import com.app.mindbody.repositories.UserBadgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserBadgeService {

    private final UserBadgeRepository userBadgeRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserProfileRepository userProfileRepository;
    private final JwtService jwtService;

    public List<UserBadgeDTO> getUserBadges(String token) {

        String username = jwtService.extractUsername(token);
        UserAuth auth = userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        List<UserBadge> badges = userBadgeRepository.findAllByUserProfile(profile);


        return badges.stream()
                .map(UserBadgeDTO::fromEntity)
                .collect(Collectors.toList());
    }


    public void updateEarned (List<Badge> badges, UserProfile profile){
        for (Badge badge : badges) {
            boolean alreadyHas = userBadgeRepository
                    .findByUserProfileAndBadge(profile, badge)
                    .isPresent();
            if (alreadyHas) continue;

            boolean qualifies = switch (badge.getRequirement_type()) {
                case STREAK -> profile.getStreakCount() >= badge.getRequirementValue();
                case WORKOUT_COUNT -> profile.getTotalWorkouts() >= badge.getRequirementValue();
                case DURATION -> profile.getLongestWorkout() >= badge.getRequirementValue();
                case TOTAL_DURATION -> profile.getTotalMinutes() >= badge.getRequirementValue();
                case MORNING_WORKOUTS -> profile.getTotalMornings() >= badge.getRequirementValue();
                case EVENING_WORKOUTS -> profile.getTotalEvenings() >= badge.getRequirementValue();
                default -> false;
            };

            if (qualifies) {
                log.info("UserProfile {} earned badge '{}'", profile.getId(), badge.getBadge_name());
                UserBadge earned = UserBadge.builder()
                        .userProfile(profile)
                        .badge(badge)
                        .build();
                userBadgeRepository.save(earned);
            }
        }
    }
}
