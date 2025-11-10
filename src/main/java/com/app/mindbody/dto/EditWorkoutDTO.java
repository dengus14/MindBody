package com.app.mindbody.dto;

import com.app.mindbody.enums.WorkoutTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditWorkoutDTO {

    @NotNull
    private Long id;
    private WorkoutTypeEnum workoutType;
    private int durationMinutes;
    private String notes;

}
