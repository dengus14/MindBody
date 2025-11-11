package com.app.mindbody.dto;

import com.app.mindbody.enums.WorkoutTypeEnum;
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

    private Long id;
    private LocalDateTime created_at;
    private WorkoutTypeEnum workoutType;
    private int durationMinutes;
    private String notes;

    public static WorkoutHistoryDTO fromEntity(Workout workout) {




        return WorkoutHistoryDTO.builder()
                .id(workout.getId())
                .created_at(workout.getCreatedAt())
                .workoutType(workout.getWorkoutType())
                .durationMinutes(workout.getDurationMinutes())
                .notes(workout.getNotes())
                .build();
    }


}
