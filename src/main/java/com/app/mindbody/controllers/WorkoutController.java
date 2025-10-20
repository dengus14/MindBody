package com.app.mindbody.controllers;

import com.app.mindbody.dto.AddWorkoutDTO;
import com.app.mindbody.dto.EditWorkoutDTO;
import com.app.mindbody.service.WorkoutService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class WorkoutController {
    private final WorkoutService workoutService;

    private String returnValidToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }
        return authHeader.substring(7);
    }

    @PostMapping("/addWorkout")
    public ResponseEntity<String> add(@RequestBody AddWorkoutDTO request, @NonNull HttpServletRequest header){
        String token = returnValidToken(header);
        return ResponseEntity.ok(workoutService.addWorkout(request,token));
    }



    @PutMapping("/editWorkout")
    public ResponseEntity<String> edit(@RequestBody EditWorkoutDTO request, @NonNull HttpServletRequest header){

        String token = returnValidToken(header);
        return ResponseEntity.ok(workoutService.editWorkout(request,token));
    }

    @DeleteMapping("/delWorkout")
    public ResponseEntity<String> delete(@RequestBody EditWorkoutDTO request, @NonNull HttpServletRequest header){

        String token = returnValidToken(header);
        return ResponseEntity.ok(workoutService.removeWorkout(request,token));
    }
}
