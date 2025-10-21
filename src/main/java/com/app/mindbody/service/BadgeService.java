package com.app.mindbody.service;


import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.BadgeProgressDTO;
import com.app.mindbody.models.Badge;
import com.app.mindbody.repositories.BadgeRepository;
import com.app.mindbody.repositories.UserBadgeRepository;
import com.app.mindbody.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {


    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final JwtService jwtService;


    public BadgeProgressDTO getProgress(String token){

        String username = jwtService.extractUsername(token);
        var user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        List<Badge> allBadges = badgeRepository.findAllByOrderByRequirementValueAsc();

        for (Badge badge : allBadges) {
            if (user.getStreak_count() < badge.getRequirementValue() && !(userBadgeRepository.findByUserAndBadge(user, badge).isPresent())){
                int daysRemaining = badge.getRequirementValue() - user.getStreak_count();
                int currentStreak = user.getStreak_count();
                return BadgeProgressDTO.getProgressDTO(daysRemaining, badge.getRequirementValue(), currentStreak, badge.getBadge_name());
            }
        }
        return BadgeProgressDTO.getProgressDTO(0, 0, 0, "No Badges Earned");
    }

}