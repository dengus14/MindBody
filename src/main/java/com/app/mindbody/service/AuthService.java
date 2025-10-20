package com.app.mindbody.service;

import com.app.mindbody.config.JwtService;
import com.app.mindbody.controllers.AuthenticationResponse;
import com.app.mindbody.dto.LoginDTO;
import com.app.mindbody.dto.RegisterDTO;
import com.app.mindbody.enums.UserRoleEnums;
import com.app.mindbody.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import com.app.mindbody.models.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthenticationResponse register(RegisterDTO request) {
        var user = User
                .builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password_hash(passwordEncoder.encode(request.getPassword()))
                .role(UserRoleEnums.USER)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse login(LoginDTO request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        return AuthenticationResponse.builder().token(jwtService.generateToken(user)).build();
    }
}
