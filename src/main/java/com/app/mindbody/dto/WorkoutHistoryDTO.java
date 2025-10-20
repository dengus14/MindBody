package com.app.mindbody.dto;

import com.app.mindbody.enums.RequirementTypeEnums;
import com.app.mindbody.enums.WorkoutTypeEnum;
import com.app.mindbody.models.Badge;
import com.app.mindbody.models.UserBadge;
import com.app.mindbody.models.Workout;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutHistoryDTO {

    private LocalDateTime created_at;
    private WorkoutTypeEnum workoutType;
    private int durationMinutes;
    private String notes;

    public static WorkoutHistoryDTO fromEntity(Workout workout) {
        // Extract the badge object from the relationship


        // Build and return the DTO with values from both UserBadge and Badge
        return WorkoutHistoryDTO.builder()
                .created_at(workout.getCreated_at())
                .workoutType(workout.getWorkoutType())
                .durationMinutes(workout.getDurationMinutes())
                .notes(workout.getNotes())
                .build();
    }


}
