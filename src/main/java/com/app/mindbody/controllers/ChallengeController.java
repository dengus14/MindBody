package com.app.mindbody.controllers;

import com.app.mindbody.dto.ChallengeDTO;
import com.app.mindbody.dto.ClaimChallengeRequest;
import com.app.mindbody.dto.ClaimChallengeResponse;
import com.app.mindbody.models.UserAuth;
import com.app.mindbody.models.UserProfile;
import com.app.mindbody.repositories.UserProfileRepository;
import com.app.mindbody.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges/user")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;
    private final UserProfileRepository userProfileRepository;


    @GetMapping("/active")
    public ResponseEntity<List<ChallengeDTO>> getActiveChallenges(
            @AuthenticationPrincipal UserAuth userAuth
    ) {
        // ✅ Fetch UserProfile from database using UserAuth ID
        UserProfile user = userProfileRepository.findById(userAuth.getId())
                .orElseThrow(() -> new RuntimeException("User profile not found for user: " + userAuth.getUsername()));

        // Ensure challenges are assigned for today/this week
        challengeService.ensureChallengesAssigned(user);

        List<ChallengeDTO> challenges = challengeService.getActiveChallenges(user);
        return ResponseEntity.ok(challenges);
    }


    @PostMapping("/claim")
    public ResponseEntity<ClaimChallengeResponse> claimChallenge(
            @RequestBody ClaimChallengeRequest request,
            @AuthenticationPrincipal UserAuth userAuth
    ) {
        // ✅ Fetch UserProfile from database
        UserProfile user = userProfileRepository.findById(userAuth.getId())
                .orElseThrow(() -> new RuntimeException("User profile not found for user: " + userAuth.getUsername()));

        ClaimChallengeResponse response = challengeService.claimChallenge(request, user);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}