package com.app.mindbody.service;


import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.BadgeDTO;
import com.app.mindbody.dto.BadgeProgressDTO;
import com.app.mindbody.models.Badge;
import com.app.mindbody.models.UserAuth;
import com.app.mindbody.models.UserBadge;
import com.app.mindbody.models.UserProfile;
import com.app.mindbody.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeService {


    private final BadgeRepository badgeRepository;
    private final UserAuthRepository userAuthRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserBadgeService userBadgeService;
    private final JwtService jwtService;
    private final BadgeServiceCalculator badgeServiceCalculator;


    public List<BadgeDTO> getProgress(String token) {


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

    public void awardBadges(UserProfile profile) {
        log.debug("Evaluating badges for userProfile id={}", profile.getId());
        List<Badge> badges = badgeRepository.findAllByOrderByRequirementValueAsc();
        userBadgeService.updateEarned(badges, profile);
    }
}