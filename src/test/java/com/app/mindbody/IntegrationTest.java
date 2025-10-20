package com.app.mindbody.IntegrationTests;

import com.app.mindbody.dto.LoginDTO;
import com.app.mindbody.dto.RegisterDTO;
import com.app.mindbody.controllers.AuthenticationResponse;
import com.app.mindbody.enums.UserRoleEnums;
import com.app.mindbody.models.User;
import com.app.mindbody.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String baseUrl = "/api/auth";

    private static final String testUsername = "denis";
    private static final String testUserEmail = "denis@example.com";
    private static final String testUserPassword = "password123";

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    // PostgreSQL container
    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("pass");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Test
    @Order(1)
    @DisplayName("1. User Registration - Should register new user and return JWT")
    void testUserRegistration() {
        RegisterDTO registerRequest = new RegisterDTO();
        registerRequest.setUsername(testUsername);
        registerRequest.setEmail(testUserEmail);
        registerRequest.setPassword(testUserPassword);

        ResponseEntity<AuthenticationResponse> response = restTemplate.postForEntity(
                baseUrl + "/register",
                registerRequest,
                AuthenticationResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());

        User savedUser = userRepository.findByUsername(testUsername).orElse(null);
        assertNotNull(savedUser);
        assertEquals(testUserEmail, savedUser.getEmail());
    }

    @Test
    @Order(2)
    @DisplayName("2. User Login - Should successfully login with correct credentials")
    void testUserLogin() {
        // Arrange - Create user first
        User user = User.builder()
                .username(testUsername)
                .email(testUserEmail)
                .password_hash(passwordEncoder.encode(testUserPassword))
                .role(UserRoleEnums.USER)
                .streak_count(0)
                .longest_streak(0)
                .build();
        userRepository.save(user);

        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setUsername(testUsername);
        loginRequest.setPassword(testUserPassword);

        // Act
        ResponseEntity<AuthenticationResponse> response = restTemplate.postForEntity(
                baseUrl + "/login",
                loginRequest,
                AuthenticationResponse.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());
    }
}
