package com.app.mindbody.controllers;


import com.app.mindbody.models.UserBadge;
import com.app.mindbody.service.UserBadgeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequestMapping("/api/badges/user")
@RestController
@RequiredArgsConstructor
public class UserBadgeController {
    private final UserBadgeService service;

    private String returnValidToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }
        return authHeader.substring(7);
    }

    @GetMapping("/all")
    public List<UserBadgeDTO> getUserBadges(HttpServletRequest header) {
        String token = returnValidToken(header);

        return service.getUserBadges(token);
    }
}
