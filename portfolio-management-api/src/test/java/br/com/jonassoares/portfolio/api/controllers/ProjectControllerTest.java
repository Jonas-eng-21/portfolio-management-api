package br.com.jonassoares.portfolio.api.controllers;

import br.com.jonassoares.portfolio.api.dtos.ProjectRequest;
import br.com.jonassoares.portfolio.domain.repositories.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "desbravador", roles = {"ADMIN"})
    void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
        ProjectRequest request = new ProjectRequest(
                "",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                new BigDecimal("1000"),
                "Desc",
                1L
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.details[0]").value("The name is required"));
    }

    @Test
    @WithMockUser(username = "desbravador", roles = {"ADMIN"})
    void shouldReturnUnprocessableEntityWhenDatesAreInverted() throws Exception {
        ProjectRequest request = new ProjectRequest(
                "Project Test",
                LocalDate.now(),
                LocalDate.now().minusDays(1),
                new BigDecimal("1000"),
                "Desc",
                1L
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Broken Business Rule"))
                .andExpect(jsonPath("$.message").value("The end date cannot be earlier than the start date."));
    }
}
