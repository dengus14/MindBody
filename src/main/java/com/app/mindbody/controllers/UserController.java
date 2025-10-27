package com.app.mindbody.controllers;

import com.app.mindbody.config.JwtService;
import com.app.mindbody.dto.UserDTO;
import com.app.mindbody.models.User;
import com.app.mindbody.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        // Extract JWT
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Map entity → DTO
        UserDTO dto = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .streak_count(user.getStreak_count())
                .longest_streak(user.getLongest_streak())
                .totalMinutes(user.getTotalMinutes())
                .last_workout(user.getLast_workout())
                .created_at(user.getCreated_at())
                .build();

        return ResponseEntity.ok(dto);
    }
    @GetMapping("/top")
    public ResponseEntity<List<UserDTO>> getTopUsersByPoints() {
        List<User> users = userRepository.findTop50ByOrderByPointsDesc();

        // sanitize nulls to zero
        List<UserDTO> dtos = users.stream()
                .map(u -> UserDTO.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .email(u.getEmail())
                        .streak_count(u.getStreak_count() == null ? 0 : u.getStreak_count())
                        .longest_streak(u.getLongest_streak() == null ? 0 : u.getLongest_streak())
                        .totalMinutes(u.getTotalMinutes() == null ? 0 : u.getTotalMinutes())
                        .points(u.getPoints() == null ? 0 : u.getPoints())
                        .last_workout(u.getLast_workout())
                        .created_at(u.getCreated_at())
                        .build())
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
