package com.app.mindbody.dto;

import com.app.mindbody.enums.WorkoutTypeEnum;
import com.app.mindbody.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddWorkoutDTO {
    private User user;
    private WorkoutTypeEnum workoutType;
    private int durationMinutes;
    private String notes;
}
