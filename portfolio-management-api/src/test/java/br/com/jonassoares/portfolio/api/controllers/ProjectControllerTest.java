package br.com.jonassoares.portfolio.api.controllers;

import br.com.jonassoares.portfolio.api.dtos.ProjectRequest;
import br.com.jonassoares.portfolio.api.dtos.ProjectResponse;
import br.com.jonassoares.portfolio.api.dtos.ProjectStatusRequest;
import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import br.com.jonassoares.portfolio.domain.enums.RiskLevel;
import br.com.jonassoares.portfolio.domain.exceptions.ResourceNotFoundException;
import br.com.jonassoares.portfolio.domain.services.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@WithMockUser
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    private UUID projectId;
    private ProjectRequest projectRequest;
    private ProjectResponse projectResponse;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        projectRequest = new ProjectRequest(
                "Test Project",
                LocalDate.now(),
                LocalDate.now().plusMonths(5),
                new BigDecimal("200000"),
                "Description",
                1L
        );

        projectResponse = new ProjectResponse(
                projectId,
                "Test Project",
                LocalDate.now(),
                LocalDate.now().plusMonths(5),
                null,
                new BigDecimal("200000"),
                "Description",
                1L,
                ProjectStatus.EM_ANALISE,
                RiskLevel.MEDIO_RISCO
        );
    }

    @Test
    @DisplayName("POST /projects: Should return 201 Created on success")
    void create_ShouldReturnCreated() throws Exception {
        when(projectService.create(any(ProjectRequest.class))).thenReturn(projectResponse);

        mockMvc.perform(post("/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/projects/" + projectId)))
                .andExpect(jsonPath("$.id").value(projectId.toString()))
                .andExpect(jsonPath("$.name").value(projectResponse.name()));
    }

    @Test
    @DisplayName("POST /projects: Should return 400 Bad Request on validation failure")
    void create_ShouldReturnBadRequestOnValidationFailure() throws Exception {
        ProjectRequest invalidRequest = new ProjectRequest(
                "",
                null,
                null,
                new BigDecimal("-100"), // Negative budget
                "Description",
                null
        );

        mockMvc.perform(post("/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /projects/{id}: Should return 200 OK for valid ID")
    void findById_ShouldReturnOk() throws Exception {
        when(projectService.findById(projectId)).thenReturn(projectResponse);

        mockMvc.perform(get("/projects/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId.toString()))
                .andExpect(jsonPath("$.name").value(projectResponse.name()));
    }

    @Test
    @DisplayName("GET /projects/{id}: Should return 404 Not Found when service throws ResourceNotFoundException")
    void findById_ShouldReturnNotFound() throws Exception {
        when(projectService.findById(projectId)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/projects/{id}", projectId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /projects: Should return 200 OK with pagination and filters")
    void findAll_ShouldReturnOk() throws Exception {
        Page<ProjectResponse> page = new PageImpl<>(List.of(projectResponse));
        when(projectService.findAll(eq("Test"), eq(ProjectStatus.EM_ANALISE), eq(1L), eq(RiskLevel.MEDIO_RISCO), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/projects")
                        .param("name", "Test")
                        .param("status", "EM_ANALISE")
                        .param("managerId", "1")
                        .param("riskLevel", "MEDIO_RISCO")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(projectId.toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("PUT /projects/{id}: Should return 200 OK on success")
    void update_ShouldReturnOk() throws Exception {
        when(projectService.update(eq(projectId), any(ProjectRequest.class))).thenReturn(projectResponse);

        mockMvc.perform(put("/projects/{id}", projectId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId.toString()));
    }

    @Test
    @DisplayName("PATCH /projects/{id}/status: Should return 200 OK")
    void updateStatus_ShouldReturnOk() throws Exception {
        ProjectStatusRequest statusRequest = new ProjectStatusRequest(ProjectStatus.ANALISE_REALIZADA);
        when(projectService.updateStatus(eq(projectId), eq(ProjectStatus.ANALISE_REALIZADA))).thenReturn(projectResponse);

        mockMvc.perform(patch("/projects/{id}/status", projectId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /projects/{id}: Should return 204 No Content")
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(projectService).delete(projectId);

        mockMvc.perform(delete("/projects/{id}", projectId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /projects/{id}/recover: Should return 200 OK")
    void recover_ShouldReturnOk() throws Exception {
        doNothing().when(projectService).recover(projectId);

        mockMvc.perform(post("/projects/{id}/recover", projectId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
