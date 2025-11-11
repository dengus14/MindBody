package com.app.mindbody.controllers;

import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.UserAuthDTO;
import com.app.mindbody.dto.UserFullDTO;
import com.app.mindbody.dto.UserProfileDTO;
import com.app.mindbody.models.UserAuth;
import com.app.mindbody.models.UserProfile;
import com.app.mindbody.repositories.UserAuthRepository;
import com.app.mindbody.repositories.UserProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final JwtService jwtService;
    private final UserAuthRepository userAuthRepository;
    private final UserProfileRepository userProfileRepository;

    /** Extract JWT from Authorization header */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }
        return header.substring(7);
    }

    /** Get authenticated user (auth info + profile stats) */
    @GetMapping("/me")
    public ResponseEntity<UserFullDTO> getCurrentUser(HttpServletRequest request) {
        String token = extractToken(request);
        String username = jwtService.extractUsername(token);

        UserAuth auth = userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserProfile profile = userProfileRepository.findByAuth(auth)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));

        UserFullDTO dto = UserFullDTO.builder()
                .auth(UserAuthDTO.fromEntity(auth))
                .profile(UserProfileDTO.fromEntity(profile))
                .build();

        return ResponseEntity.ok(dto);
    }

    /** Get top users by points (leaderboard) */
    @GetMapping("/top")
    public ResponseEntity<List<UserProfileDTO>> getTopUsersByPoints() {
        List<UserProfile> profiles = userProfileRepository.findTop50ByOrderByPointsDesc();

        List<UserProfileDTO> dtos = profiles.stream()
                .map(UserProfileDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
