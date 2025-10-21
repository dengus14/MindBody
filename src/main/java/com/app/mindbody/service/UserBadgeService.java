package com.app.mindbody.service;


import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.UserBadgeDTO;
import com.app.mindbody.models.UserBadge;
import com.app.mindbody.repositories.UserBadgeRepository;
import com.app.mindbody.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserBadgeService {

    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public List<UserBadgeDTO> getUserBadges(String token) {

        //retrieve user from JWT token
        String username = jwtService.extractUsername(token);
        var user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        List<UserBadge> userBadges = userBadgeRepository.findAllByUser(user);
        return userBadges.stream()
                .map(UserBadgeDTO::fromEntity)  // This calls fromEntity for each item
                .collect(Collectors.toList());
    }
}
