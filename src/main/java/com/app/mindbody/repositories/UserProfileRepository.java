package com.app.mindbody.repositories;

import com.app.mindbody.models.UserAuth;
import com.app.mindbody.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile,Long> {
    Optional<UserProfile> findByAuth(UserAuth auth);
    List<UserProfile> findByAllByAuth();
    List<UserProfile> findTop50ByOrderByPointsDesc();
}
