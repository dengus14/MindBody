package com.app.mindbody.scheduler;

import com.app.mindbody.models.UserProfile;
import com.app.mindbody.repositories.UserProfileRepository;
import com.app.mindbody.service.ChallengeAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChallengeScheduler {

    private final UserProfileRepository userProfileRepository;
    private final ChallengeAssignmentService assignmentService;


    @Scheduled(cron = "0 0 0 * * *")
    public void assignDailyChallenges() {
        log.info("Starting daily challenge assignment job");
        LocalDate today = LocalDate.now();

        List<UserProfile> allUsers = userProfileRepository.findAll();

        for (UserProfile user : allUsers) {
            try {
                assignmentService.assignDailyChallenges(user, today);
            } catch (Exception e) {
                log.error("Failed to assign daily challenges for user {}: {}",
                        user.getAuth().getUsername(), e.getMessage());
            }
        }

        log.info("Completed daily challenge assignment for {} users", allUsers.size());
    }


    @Scheduled(cron = "0 0 0 * * SUN")
    public void assignWeeklyChallenges() {
        log.info("Starting weekly challenge assignment job");
        LocalDate today = LocalDate.now();

        List<UserProfile> allUsers = userProfileRepository.findAll();

        for (UserProfile user : allUsers) {
            try {
                assignmentService.assignWeeklyChallenges(user, today);
            } catch (Exception e) {
                log.error("Failed to assign weekly challenges for user {}: {}",
                        user.getAuth().getUsername(), e.getMessage());
            }
        }

        log.info("Completed weekly challenge assignment for {} users", allUsers.size());
    }
}