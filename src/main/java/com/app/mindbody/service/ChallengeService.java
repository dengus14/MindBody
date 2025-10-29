package com.app.mindbody.service;


import com.app.mindbody.config.JwtService;
import com.app.mindbody.enums.ChallengeType;
import com.app.mindbody.models.Challenge;
import com.app.mindbody.models.UserChallenge;
import com.app.mindbody.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.threeten.extra.YearWeek;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {


    private final ChallengeRepository challengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;


    public void getThreeDailies(String token) {


        String username = jwtService.extractUsername(token);
        var user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        boolean assigned = userChallengeRepository.existsByUserAndChallenge_TypeAndAssignedDate(user, ChallengeType.DAILY, LocalDate.now());

        if(assigned){
            return;
        }
        else{
            List<Challenge> threeDailyChallenges = challengeRepository.findAllByType(ChallengeType.DAILY, PageRequest.of(0, 3));
            threeDailyChallenges.forEach(challenge -> {
                userChallengeRepository.save(
                        UserChallenge.builder()
                                .user(user)
                                .challenge(challenge)
                                .assignedDate(LocalDate.now())
                                .assignedWeek(YearWeek.now())
                                .completed(false)
                                .build()
                );
            });

        }

    }


    public void getThreeWeeklies(String token) {


        String username = jwtService.extractUsername(token);
        var user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        boolean assigned = userChallengeRepository.existsByUserAndChallenge_TypeAndAssignedWeek(user, ChallengeType.WEEKLY, YearWeek.now());

        if(assigned){
            return;
        }
        else{
            List<Challenge> threeWeeklyChallenges = challengeRepository.findAllByType(ChallengeType.WEEKLY, PageRequest.of(0, 3));
            threeWeeklyChallenges.forEach(challenge -> {
                userChallengeRepository.save(
                        UserChallenge.builder()
                                .user(user)
                                .challenge(challenge)
                                .assignedDate(LocalDate.now())
                                .assignedWeek(YearWeek.now())
                                .completed(false)
                                .build()
                );
            });

        }

    }
}