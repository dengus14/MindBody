package com.app.mindbody.INTEGRATIONS;

import com.app.mindbody.config.ApplicationConfig;
import com.app.mindbody.dto.*;
import com.app.mindbody.enums.WorkoutTypeEnum;
import com.app.mindbody.models.Workout;
import com.app.mindbody.repositories.UserRepository;
import com.app.mindbody.repositories.WorkoutRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(ApplicationConfig.class) // ensures your beans are loaded
public class WorkoutIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    private final String baseUrl = "/api/auth";

    private static final String TEST_USERNAME = "denis";
    private static final String TEST_EMAIL = "denis@example.com";
    private static final String TEST_PASSWORD = "password123";

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
        workoutRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("1. Register User - Should return JWT")
    void testUserRegistration() {
        RegisterDTO registerRequest = new RegisterDTO();
        registerRequest.setUsername(TEST_USERNAME);
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);

        ResponseEntity<?> response = restTemplate.postForEntity(
                baseUrl + "/register",
                registerRequest,
                Object.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        User savedUser = userRepository.findByUsername(TEST_USERNAME).orElse(null);
        assertNotNull(savedUser);
        assertEquals(TEST_EMAIL, savedUser.getEmail());
    }

    @Test
    @Order(2)
    @DisplayName("2. Login User - Should return JWT")
    void testUserLogin() {
        // Create user manually for login
        User user = User.builder()
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .password_hash(passwordEncoder.encode(TEST_PASSWORD))
                .role(com.app.mindbody.enums.UserRoleEnums.USER)
                .build();
        userRepository.save(user);

        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);

        ResponseEntity<?> response = restTemplate.postForEntity(
                baseUrl + "/login",
                loginRequest,
                Object.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(3)
    @DisplayName("3. Get Workout History - Should return empty list initially")
    void testGetWorkoutHistory() {
        // Register & login first to get token
        RegisterDTO registerRequest = new RegisterDTO();
        registerRequest.setUsername(TEST_USERNAME);
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);

        restTemplate.postForEntity(baseUrl + "/register", registerRequest, Object.class);

        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);

        ResponseEntity<?> loginResponse = restTemplate.postForEntity(baseUrl + "/login", loginRequest, Object.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        // Extract JWT from response map
        @SuppressWarnings("unchecked")
        String token = ((java.util.Map<String, String>) loginResponse.getBody()).get("token");
        assertNotNull(token);

        // Call getHistory endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<WorkoutHistoryDTO[]> historyResponse = restTemplate.exchange(
                "/api/getHistory",
                HttpMethod.GET,
                entity,
                WorkoutHistoryDTO[].class
        );

        assertEquals(HttpStatus.OK, historyResponse.getStatusCode());
        assertNotNull(historyResponse.getBody());
        assertEquals(0, historyResponse.getBody().length);

        // Add a workout manually
        Workout workout = Workout.builder()
                .user(userRepository.findByUsername(TEST_USERNAME).get())
                .workoutType(com.app.mindbody.enums.WorkoutTypeEnum.PUSH)
                .durationMinutes(60)
                .createdAt(LocalDateTime.now())
                .build();
        workoutRepository.save(workout);

        // Call getHistory again
        ResponseEntity<WorkoutHistoryDTO[]> updatedHistory = restTemplate.exchange(
                "/api/getHistory",
                HttpMethod.GET,
                entity,
                WorkoutHistoryDTO[].class
        );

        assertEquals(HttpStatus.OK, updatedHistory.getStatusCode());
        assertNotNull(updatedHistory.getBody());
        assertEquals(1, updatedHistory.getBody().length);
        assertEquals(60, updatedHistory.getBody()[0].getDurationMinutes());
    }

    @Test
    @Order(4)
    @DisplayName("4. Edit Workout - Should update workout correctly")
    void testEditWorkout() {
        // 1️⃣ Register & login to get JWT
        RegisterDTO registerRequest = new RegisterDTO();
        registerRequest.setUsername(TEST_USERNAME);
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);

        restTemplate.postForEntity(baseUrl + "/register", registerRequest, Object.class);

        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);

        ResponseEntity<?> loginResponse = restTemplate.postForEntity(baseUrl + "/login", loginRequest, Object.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        @SuppressWarnings("unchecked")
        String token = ((java.util.Map<String, String>) loginResponse.getBody()).get("token");
        assertNotNull(token);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        // 2️⃣ Add a workout manually
        var user = userRepository.findByUsername(TEST_USERNAME).orElseThrow();
        Workout workout = Workout.builder()
                .user(user)
                .workoutType(WorkoutTypeEnum.PUSH)
                .durationMinutes(60)
                .notes("Initial notes")
                .createdAt(LocalDateTime.now())
                .build();
        workoutRepository.save(workout);

        // 3️⃣ Prepare edit request
        EditWorkoutDTO editRequest = new EditWorkoutDTO();
        editRequest.setId(workout.getId());
        editRequest.setWorkoutType(WorkoutTypeEnum.LEGS);
        editRequest.setDurationMinutes(90);
        editRequest.setNotes("Updated notes");

        HttpEntity<EditWorkoutDTO> entity = new HttpEntity<>(editRequest, headers);

        // 4️⃣ Call edit endpoint
        ResponseEntity<String> editResponse = restTemplate.exchange(
                "/api/editWorkout",
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertEquals(HttpStatus.OK, editResponse.getStatusCode());
        assertNotNull(editResponse.getBody());

        // 5️⃣ Verify workout was updated in DB
        Workout updatedWorkout = workoutRepository.findById(workout.getId()).orElseThrow();
        assertEquals(WorkoutTypeEnum.LEGS, updatedWorkout.getWorkoutType());
        assertEquals(90, updatedWorkout.getDurationMinutes());
        assertEquals("Updated notes", updatedWorkout.getNotes());
    }

    @Test
    @Order(5)
    @DisplayName("5. Remove Workout - Should delete workout correctly")
    void testRemoveWorkout() {
        // 1️⃣ Register & login to get JWT
        RegisterDTO registerRequest = new RegisterDTO();
        registerRequest.setUsername(TEST_USERNAME);
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);

        restTemplate.postForEntity(baseUrl + "/register", registerRequest, Object.class);

        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);

        ResponseEntity<?> loginResponse = restTemplate.postForEntity(baseUrl + "/login", loginRequest, Object.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        @SuppressWarnings("unchecked")
        String token = ((java.util.Map<String, String>) loginResponse.getBody()).get("token");
        assertNotNull(token);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        // 2️⃣ Add a workout manually
        var user = userRepository.findByUsername(TEST_USERNAME).orElseThrow();
        Workout workout = Workout.builder()
                .user(user)
                .workoutType(WorkoutTypeEnum.PUSH)
                .durationMinutes(60)
                .notes("Workout to delete")
                .createdAt(LocalDateTime.now())
                .build();
        workoutRepository.save(workout);

        // 3️⃣ Prepare delete request
        EditWorkoutDTO deleteRequest = new EditWorkoutDTO();
        deleteRequest.setId(workout.getId());

        HttpEntity<EditWorkoutDTO> entity = new HttpEntity<>(deleteRequest, headers);

        // 4️⃣ Call delete endpoint
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                "/api/delWorkout",
                HttpMethod.DELETE,
                entity,
                String.class
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertNotNull(deleteResponse.getBody());

        // 5️⃣ Verify workout no longer exists in DB
        boolean exists = workoutRepository.findById(workout.getId()).isPresent();
        assertFalse(exists, "Workout should be removed from database");
    }
    @Test
    @Order(6)
    @DisplayName("5. Add Workout - Should create a new workout and update streaks")
    void testAddWorkout() {
        // 1️⃣ Register & login to get JWT
        RegisterDTO registerRequest = new RegisterDTO();
        registerRequest.setUsername(TEST_USERNAME);
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);

        restTemplate.postForEntity(baseUrl + "/register", registerRequest, Object.class);

        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);

        ResponseEntity<?> loginResponse = restTemplate.postForEntity(baseUrl + "/login", loginRequest, Object.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        @SuppressWarnings("unchecked")
        String token = ((java.util.Map<String, String>) loginResponse.getBody()).get("token");
        assertNotNull(token);

        // 2️⃣ Create AddWorkoutDTO
        AddWorkoutDTO addWorkoutRequest = new AddWorkoutDTO();
        addWorkoutRequest.setDurationMinutes(45);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<AddWorkoutDTO> entity = new HttpEntity<>(addWorkoutRequest, headers);

        // 3️⃣ Perform POST /api/addWorkout
        ResponseEntity<String> addWorkoutResponse = restTemplate.postForEntity(
                "/api/addWorkout",
                entity,
                String.class
        );

        assertEquals(HttpStatus.OK, addWorkoutResponse.getStatusCode());
        assertNotNull(addWorkoutResponse.getBody());

        // 4️⃣ Verify workout was persisted in DB
        var user = userRepository.findByUsername(TEST_USERNAME).orElseThrow();
        var workouts = workoutRepository.findByUserOrderByCreatedAtDesc(user);

        assertEquals(1, workouts.size());
        var savedWorkout = workouts.get(0);
        assertEquals(45, savedWorkout.getDurationMinutes());
        assertEquals(com.app.mindbody.enums.WorkoutTypeEnum.PUSH, savedWorkout.getWorkoutType());
        assertNotNull(savedWorkout.getCreatedAt());

        // 5️⃣ Check streak logic
        assertEquals(1, user.getStreak_count());
        assertEquals(1, user.getLongest_streak());
    }


}
