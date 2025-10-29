package com.app.mindbody.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.threeten.extra.YearWeek;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_challenge")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class) // otherwise @CreatedDate and @LastModifiedDate won't populate
public class UserChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @CreatedDate
    private LocalDate assignedDate;  // for dailies
    private YearWeek assignedWeek;   // for weeklies (or just LocalDate start of week)


    private boolean completed;
}
