package com.app.mindbody.repositories;

import com.app.mindbody.models.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Integer> {
}
