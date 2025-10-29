package com.app.mindbody.service;


import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.BadgeDTO;
import com.app.mindbody.dto.BadgeProgressDTO;
import com.app.mindbody.models.Badge;
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
}