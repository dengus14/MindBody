package com.app.mindbody.INTEGRATIONS;

import com.app.mindbody.dto.LoginDTO;
import com.app.mindbody.dto.RegisterDTO;
import com.app.mindbody.dto.BadgeDTO;
import com.app.mindbody.dto.BadgeProgressDTO;
import com.app.mindbody.enums.RequirementTypeEnums;
import com.app.mindbody.models.Badge;
import com.app.mindbody.models.User;
import com.app.mindbody.models.UserBadge;
import com.app.mindbody.repositories.BadgeRepository;
import com.app.mindbody.repositories.UserBadgeRepository;
import com.app.mindbody.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BadgeIntegrationTest {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private UserRepository userRepository;
    @Autowired private BadgeRepository badgeRepository;
    @Autowired private UserBadgeRepository userBadgeRepository;

    private static final String TEST_USERNAME = "denis";
    private static final String TEST_EMAIL = "denis@example.com";
    private static final String TEST_PASSWORD = "password123";

    private final String AUTH_BASE = "/api/auth";
    private final String BADGE_BASE = "/api/badges";

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

    @AfterEach
    void cleanUp() {
        userBadgeRepository.deleteAll();
        badgeRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String registerAndLogin() {
        // Register
        RegisterDTO registerRequest = new RegisterDTO();
        registerRequest.setUsername(TEST_USERNAME);
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);

        restTemplate.postForEntity(AUTH_BASE + "/register", registerRequest, Object.class);

        // Login
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);

        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(AUTH_BASE + "/login", loginRequest, Map.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        return (String) loginResponse.getBody().get("token");
    }

    // 🟢 Test 1 — No badges yet
    @Test
    @Order(1)
    @DisplayName("1. Should return empty badge list when user has none")
    void testGetUserBadges_Empty() {
        String token = registerAndLogin();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<BadgeDTO[]> response = restTemplate.exchange(
                BADGE_BASE + "/user/all",
                HttpMethod.GET,
                entity,
                BadgeDTO[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().length);
    }

    // 🟢 Test 2 — Return earned badges
    @Test
    @Order(2)
    @DisplayName("2. Should return user's earned badges")
    void testGetUserBadges_WithBadges() {
        String token = registerAndLogin();

        User user = userRepository.findByUsername(TEST_USERNAME).orElseThrow();

        // Create badges and user badges manually
        Badge badge1 = badgeRepository.save(
                Badge.builder()
                        .badge_name("Streak 3 Days")
                        .badge_description("Worked out 3 days in a row")
                        .requirement_type(RequirementTypeEnums.STREAK)
                        .requirementValue(3)
                        .build()
        );

        UserBadge userBadge = userBadgeRepository.save(
                UserBadge.builder()
                        .user(user)
                        .badge(badge1)
                        .earned_at(LocalDateTime.now())
                        .build()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<BadgeDTO[]> response = restTemplate.exchange(
                BADGE_BASE + "/user/all",
                HttpMethod.GET,
                entity,
                BadgeDTO[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals("Streak 3 Days", response.getBody()[0].getBadge_name());
    }

    // 🟢 Test 3 — Badge progress
    @Test
    @Order(3)
    @DisplayName("3. Should return user's progress toward next badge")
    void testGetProgress() {
        String token = registerAndLogin();

        User user = userRepository.findByUsername(TEST_USERNAME).orElseThrow();
        user.setStreak_count(2);
        userRepository.save(user);

        badgeRepository.saveAll(List.of(
                Badge.builder().badge_name("Streak 3").requirementValue(3).requirement_type(RequirementTypeEnums.STREAK).build(),
                Badge.builder().badge_name("Streak 5").requirementValue(5).requirement_type(RequirementTypeEnums.STREAK).build()
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<BadgeProgressDTO> response = restTemplate.exchange(
                BADGE_BASE + "/user/get/all",
                HttpMethod.GET,
                entity,
                BadgeProgressDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Streak 3", response.getBody().getNextBadgeName());
        assertEquals(1, response.getBody().getDaysRemaining());
        assertEquals(2, response.getBody().getCurrentStreak());
    }
}
