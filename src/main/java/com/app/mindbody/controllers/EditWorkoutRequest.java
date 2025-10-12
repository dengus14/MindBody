package com.app.mindbody.controllers;

import com.app.mindbody.enums.WorkoutTypeEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditWorkoutRequest {

    @NotBlank
    private Long id;
    private WorkoutTypeEnum workoutType;
    private int durationMinutes;
    private String notes;

}
