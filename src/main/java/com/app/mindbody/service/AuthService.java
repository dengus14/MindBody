package com.app.mindbody.service;

import com.app.mindbody.config.JwtService;
import com.app.mindbody.controllers.AuthenticationResponse;
import com.app.mindbody.dto.LoginDTO;
import com.app.mindbody.dto.RegisterDTO;
import com.app.mindbody.enums.UserRoleEnums;
import com.app.mindbody.models.UserAuth;
import com.app.mindbody.models.UserProfile;
import com.app.mindbody.repositories.UserAuthRepository;
import com.app.mindbody.repositories.UserProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final UserAuthRepository userAuthRepository;
    private final UserProfileRepository userProfileRepository;

    public AuthenticationResponse register(RegisterDTO request) {
        log.info("User {} attempting to register", request.getUsername());
        //Validations
        try {
        if (userAuthRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userAuthRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

            //UserAuth entity
            var userAuth = UserAuth
                    .builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .role(UserRoleEnums.USER)
                    .build();

            userAuthRepository.save(userAuth);

            //UserProfile entity
            var profile = UserProfile.builder()
                    .auth(userAuth)
                    .streakCount(0)
                    .longestStreak(0)
                    .points(0)
                    .totalMinutes(0)
                    .totalWorkouts(0)
                    .build();

            userProfileRepository.save(profile);

            var jwtToken = jwtService.generateToken(userAuth);
            return AuthenticationResponse.builder().token(jwtToken).build();
        } catch (IllegalArgumentException e){
            log.warn("Validation failed for {}: {}", request.getUsername(), e.getMessage());
            throw e;
        }
        catch(Exception e){
            log.warn("Register failed for {}", request.getUsername(),e);
            throw e;

        }
    }

    public AuthenticationResponse login(LoginDTO request) {
        log.info("User {} attempting to log in", request.getUsername());
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()
                    )
            );

            var userAuth = userAuthRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
            log.debug("User {} authenticated successfully", userAuth.getUsername());
            return AuthenticationResponse.builder().token(jwtService.generateToken(userAuth)).build();
        } catch(Exception e){
            log.warn("Login failed for {}",request.getUsername(),e);
            throw e;
        }

        }
}
