package com.app.mindbody.repositories;

import com.app.mindbody.models.Badge;
import com.app.mindbody.models.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.mindbody.models.User;

import java.util.List;
import java.util.Optional;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    Optional<UserBadge> findByUserAndBadge(User user, Badge badge);
    List<UserBadge> findAllByUser(User user);

}
