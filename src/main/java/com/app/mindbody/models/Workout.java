package com.app.mindbody.models;

import com.app.mindbody.enums.WorkoutTypeEnum;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name="workouts")

public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    @CreatedDate
    @Column(name = "date", nullable = false)
    private LocalDateTime created_at;

    @Enumerated
    @Column(name="workout_type",nullable = false)
    private WorkoutTypeEnum workoutType;

    @Column(name="duration_minutes",nullable = false)
    private int durationMinutes;

    @Column(name="notes", columnDefinition = "TEXT")
    private String notes;


}
