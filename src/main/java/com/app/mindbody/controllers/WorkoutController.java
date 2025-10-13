package com.app.mindbody.controllers;

import com.app.mindbody.service.WorkoutService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class WorkoutController {
    private final WorkoutService workoutService;

    @PostMapping("/addWorkout")
    public ResponseEntity<String> add(@RequestBody AddWorkoutRequest request,@NonNull HttpServletRequest header){
        final String authHeader = header.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {

            return ResponseEntity.ok("Not Authorized");
        }
        String token = authHeader.substring(7);
        return ResponseEntity.ok(workoutService.addWorkout(request,token));
    }



    @PutMapping("/editWorkout")
    public ResponseEntity<String> edit(@RequestBody EditWorkoutRequest request,@NonNull HttpServletRequest header){
        final String authHeader = header.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {

            return ResponseEntity.ok("Not Authorized");
        }

        String token = authHeader.substring(7);
        return ResponseEntity.ok(workoutService.editWorkout(request,token));
    }

    @DeleteMapping("/delWorkout")
    public ResponseEntity<String> delete(@RequestBody EditWorkoutRequest request,@NonNull HttpServletRequest header){
        final String authHeader = header.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {

            return ResponseEntity.ok("Not Authorized");
        }

        String token = authHeader.substring(7);
        return ResponseEntity.ok(workoutService.removeWorkout(request,token));
    }
}
