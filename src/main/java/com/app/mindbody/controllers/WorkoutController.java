package com.app.mindbody.controllers;

import com.app.mindbody.service.WorkoutService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class WorkoutController {
    private final WorkoutService workoutService;

    @PostMapping("/addWorkout")
    public ResponseEntity<String> register(@RequestBody AddWorkoutRequest request,@NonNull HttpServletRequest drequest){
        System.out.println("lol");
        final String authHeader = drequest.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {

            return ResponseEntity.ok("Dolboeb...");
        }
        String token = authHeader.substring(7);
        System.out.println("I am alive");
        return ResponseEntity.ok(workoutService.addWorkout(request,token));
    }
}
