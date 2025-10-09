package com.app.mindbody.service;

import com.app.mindbody.models.User;
import com.app.mindbody.repositories.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String register(User user){
        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            return "User already exists";
        }
        user.setPassword_hash(passwordEncoder.encode(user.getPassword_hash()));
        userRepository.save(user);
        return "User created successfully";
    }




}
