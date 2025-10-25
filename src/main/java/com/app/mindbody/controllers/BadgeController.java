package com.app.mindbody.controllers;

import com.app.mindbody.dto.BadgeProgressDTO;
import com.app.mindbody.service.BadgeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequestMapping("/api/badges/user/get")
@RestController
@RequiredArgsConstructor
public class BadgeController {
    private final BadgeService service;

    private String returnValidToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }
        return authHeader.substring(7);
    }

    @GetMapping("/all")
    public List<BadgeProgressDTO> getProgress(HttpServletRequest header) {
        String token = returnValidToken(header);

        return service.getProgress(token);
    }
}
