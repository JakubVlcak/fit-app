package com.github.fit_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fit_app.configuration.TestcontainersConfiguration;
import com.github.fit_app.dto.ActivityRequest;
import com.github.fit_app.dto.ExerciseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Testcontainers
@Import(TestcontainersConfiguration.class)
@Sql(scripts = {"/test-data/users.sql", "/test-data/activities.sql", "/test-data/exercises.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser")
    void testCreateActivity_success() throws Exception {
        ActivityRequest activityRequest = createActivityRequest();
        mockMvc.perform(MockMvcRequestBuilders.post("/activities").with(authentication(customAuth()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(activityRequest))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetActivities_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/activities").with(authentication(customAuth()))
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    private ActivityRequest createActivityRequest() {
        ActivityRequest activityRequest = new ActivityRequest();
        activityRequest.setDuration(100L);
        activityRequest.setName("test");
        activityRequest.setDescription("description");
        ExerciseDto exercise = new ExerciseDto();
        exercise.setName("exercise1");
        exercise.setDeathStop(false);
        exercise.setReps(50L);
        exercise.setWeight(50.0);
        exercise.setExecution("test execution");

        activityRequest.setExercises(List.of(exercise));
        return activityRequest;
    }

    private Authentication customAuth() {
        Instant now = Instant.now();
        Jwt jwt = new Jwt(
                "testtoken",
                now,
                now.plusSeconds(3600),
                Map.of("alg", "RS256", "typ", "JWT"),
                Map.of("name", "testuser", "email", "test@example.com", "sub", "testuser") // claims
        );

        return new JwtAuthenticationToken(jwt, List.of());
    }

}
