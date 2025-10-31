package com.app.mindbody.service;


import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.BadgeDTO;
import com.app.mindbody.dto.BadgeProgressDTO;
import com.app.mindbody.models.Badge;
import com.app.mindbody.models.UserAuth;
import com.app.mindbody.models.UserProfile;
import com.app.mindbody.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {


    private final BadgeRepository badgeRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final JwtService jwtService;
    private final BadgeServiceCalculator badgeServiceCalculator;


    public List<BadgeDTO> getProgress(String token) {

//        String username = jwtService.extractUsername(token);
//        var user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
//        List<Badge> allBadges = badgeRepository.findAllByOrderByRequirementValueAsc();
//
//        for (Badge badge : allBadges) {
//            if (user.getStreak_count() < badge.getRequirementValue() && !(userBadgeRepository.findByUserAndBadge(user, badge).isPresent())){
//                int daysRemaining = badge.getRequirementValue() - user.getStreak_count();
//                int currentStreak = user.getStreak_count();
//                return BadgeProgressDTO.getProgressDTO(daysRemaining, badge.getRequirementValue(), currentStreak, badge.getBadge_name());
//            }
//        }
//        return BadgeProgressDTO.getProgressDTO(0, 0, 0, "No Badges Earned");
//    }


        String username = jwtService.extractUsername(token);

        UserAuth auth = userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserProfile profile = userProfileRepository.findByAuth(auth)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        List<Badge> allBadges = badgeRepository.findAllByOrderByRequirementValueAsc();
        List<BadgeDTO> retList = new ArrayList<BadgeDTO>();

        for (Badge badge : allBadges) {
            int progrValue = badgeServiceCalculator.getProgressValue(badge, profile);
            BadgeDTO toList = BadgeDTO.fromEntity(badge, progrValue);
            retList.add(toList);
        }
        return retList;
    }
}