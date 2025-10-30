package com.app.mindbody.controllers;

import com.app.mindbody.dto.ChallengeDTO;
import com.app.mindbody.dto.ClaimChallengeRequest;
import com.app.mindbody.dto.ClaimChallengeResponse;
import com.app.mindbody.models.User;
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

    /**
     * Get active challenges for authenticated user
     * Automatically assigns challenges if not already assigned
     */
    @GetMapping("/active")
    public ResponseEntity<List<ChallengeDTO>> getActiveChallenges(
            @AuthenticationPrincipal User user
    ) {
        // Ensure challenges are assigned for today/this week
        challengeService.ensureChallengesAssigned(user);

        List<ChallengeDTO> challenges = challengeService.getActiveChallenges(user);
        return ResponseEntity.ok(challenges);
    }

    /**
     * Claim a completed challenge to receive points
     */
    @PostMapping("/claim")
    public ResponseEntity<ClaimChallengeResponse> claimChallenge(
            @RequestBody ClaimChallengeRequest request,
            @AuthenticationPrincipal User user
    ) {
        ClaimChallengeResponse response = challengeService.claimChallenge(request, user);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}