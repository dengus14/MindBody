package com.app.mindbody.service;

import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.UserBadgeDTO;
import com.app.mindbody.models.UserAuth;
import com.app.mindbody.models.UserProfile;
import com.app.mindbody.models.UserBadge;
import com.app.mindbody.repositories.UserAuthRepository;
import com.app.mindbody.repositories.UserProfileRepository;
import com.app.mindbody.repositories.UserBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
}
