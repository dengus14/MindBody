package com.app.mindbody.models;

import com.app.mindbody.enums.ChallengeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "user_challenges",
        indexes = {
                @Index(name = "idx_user_challenge_date", columnList = "user_profile_id, assigned_date"),
                @Index(name = "idx_user_challenge_week", columnList = "user_profile_id, assigned_week_year, assigned_week_number"),
                @Index(name = "idx_user_completed", columnList = "user_profile_id, completed")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_challenge_daily", columnNames = {"user_profile_id", "challenge_id", "assigned_date"}),
                @UniqueConstraint(name = "uk_user_challenge_weekly", columnNames = {"user_profile_id", "challenge_id", "assigned_week_year", "assigned_week_number"})
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @CreatedDate
    @Column(name = "assigned_at", nullable = false)
    private LocalDate assignedAt;

    // For DAILY challenges
    @Column(name = "assigned_date")
    private LocalDate assignedDate;

    // For WEEKLY challenges (store year + week number separately)
    @Column(name = "assigned_week_year")
    private Integer assignedWeekYear;

    @Column(name = "assigned_week_number")
    private Integer assignedWeekNumber;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    @Column(name = "current_progress", nullable = false)
    private Integer currentProgress = 0;

    @Column(name = "claimed", nullable = false)
    private boolean claimed;

    @Column(name = "claimed_at")
    private LocalDate claimedAt;

    @PrePersist
    public void prePersist() {
        if (currentProgress == null) currentProgress = 0;
        if (!completed) completed = false;
        if (!claimed) claimed = false;
    }
}