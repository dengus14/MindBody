package com.app.mindbody.service;


import com.app.mindbody.models.UserProfile;
import com.app.mindbody.repositories.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
@Slf4j
@Service
@RequiredArgsConstructor
public class StreakCountService {

    private final UserProfileRepository userProfileRepository;
    public void updateStreakForWorkout(UserProfile profile){

        LocalDate today = LocalDate.now();
        LocalDate last = profile.getLastWorkout();

        if (last != null && today.equals(last.plusDays(1))) {

            profile.setStreakCount(profile.getStreakCount() + 1);
            profile.setLongestStreak(Math.max(profile.getLongestStreak(), profile.getStreakCount()));
            log.info("Streak incremented to {} for user {}", profile.getStreakCount(), profile.getAuth().getUsername());
        } else if (last == null || !today.equals(last)) {
            profile.setStreakCount(1);
            profile.setLongestStreak(Math.max(profile.getLongestStreak(), 1));
            log.info("Streak reset for user {}", profile.getAuth().getUsername());
        }
    }
}
