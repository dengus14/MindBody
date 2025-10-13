package com.app.mindbody.repositories;

import com.app.mindbody.models.Badge;
import com.app.mindbody.models.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.mindbody.models.User;
import java.util.Optional;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Integer> {
    Optional<UserBadge> findByUserAndBadge(User user, Badge badge);

}
