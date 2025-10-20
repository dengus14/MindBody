package com.app.mindbody.repositories;

import com.app.mindbody.models.User;
import com.app.mindbody.models.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout,Long> {
    Optional<Workout> findById(Long id);
    Optional<Workout> removeById(Long id);
    List<Workout> findByUserOrderByCreatedAtDesc(User user);
}
