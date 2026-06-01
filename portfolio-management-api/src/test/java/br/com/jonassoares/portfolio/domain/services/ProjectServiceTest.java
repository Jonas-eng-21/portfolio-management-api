package br.com.jonassoares.portfolio.domain.services;

import br.com.jonassoares.portfolio.api.dtos.ProjectRequest;
import br.com.jonassoares.portfolio.api.dtos.ProjectResponse;
import br.com.jonassoares.portfolio.domain.entities.Project;
import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import br.com.jonassoares.portfolio.domain.enums.RiskLevel;
import br.com.jonassoares.portfolio.domain.exceptions.BusinessRuleException;
import br.com.jonassoares.portfolio.domain.exceptions.ResourceNotFoundException;
import br.com.jonassoares.portfolio.domain.repositories.ProjectRepository;
import br.com.jonassoares.portfolio.domain.validators.ProjectDeletionValidator;
import br.com.jonassoares.portfolio.domain.validators.ProjectStatusTransitionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository repository;

    @Mock
    private RiskCalculatorService riskCalculatorService;

    @Mock
    private ProjectStatusTransitionValidator statusTransitionValidator;

    @Mock
    private ProjectDeletionValidator deletionValidator;

    @InjectMocks
    private ProjectService service;

    private UUID projectId;
    private Project project;
    private ProjectRequest projectRequest;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        project = new Project();
        project.setId(projectId);
        project.setName("Test Project");
        project.setStartDate(LocalDate.now());
        project.setExpectedEndDate(LocalDate.now().plusMonths(5));
        project.setBudget(new BigDecimal("200000"));
        project.setStatus(ProjectStatus.EM_ANALISE);
        project.setRiskLevel(RiskLevel.BAIXO_RISCO);
        project.setDeleted(false);

        projectRequest = new ProjectRequest(
                "Test Project",
                LocalDate.now(),
                LocalDate.now().plusMonths(5),
                new BigDecimal("200000"),
                "Description",
                1L
        );
    }

    @Test
    @DisplayName("Create: Should create project with status EM_ANALISE")
    void create_ShouldCreateProjectWithDefaultStatus() {
        when(riskCalculatorService.calculateRisk(any(), any(), any())).thenReturn(RiskLevel.BAIXO_RISCO);
        when(repository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponse response = service.create(projectRequest);

        assertNotNull(response);
        assertEquals(ProjectStatus.EM_ANALISE, response.status());
        assertEquals(RiskLevel.BAIXO_RISCO, response.riskLevel());
        verify(riskCalculatorService, times(1)).calculateRisk(any(), any(), any());
        verify(repository, times(1)).save(any(Project.class));
    }

    @Test
    @DisplayName("Create: Should throw BusinessRuleException when end date is before start date")
    void create_ShouldThrowExceptionWhenEndDateIsBeforeStartDate() {
        ProjectRequest invalidRequest = new ProjectRequest(
                "Invalid Project",
                LocalDate.now(),
                LocalDate.now().minusDays(1),
                new BigDecimal("100000"),
                "Description",
                1L
        );

        assertThrows(BusinessRuleException.class, () -> service.create(invalidRequest));
        verify(repository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("FindById: Should return project when ID exists and not deleted")
    void findById_ShouldReturnProjectWhenIdExists() {
        when(repository.findById(projectId)).thenReturn(Optional.of(project));

        ProjectResponse response = service.findById(projectId);

        assertNotNull(response);
        assertEquals(projectId, response.id());
    }

    @Test
    @DisplayName("FindById: Should throw ResourceNotFoundException when ID does not exist")
    void findById_ShouldThrowExceptionWhenIdDoesNotExist() {
        when(repository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(projectId));
    }

    @Test
    @DisplayName("FindById: Should throw ResourceNotFoundException when project is deleted")
    void findById_ShouldThrowExceptionWhenProjectIsDeleted() {
        project.setDeleted(true);
        when(repository.findById(projectId)).thenReturn(Optional.of(project));

        assertThrows(ResourceNotFoundException.class, () -> service.findById(projectId));
    }

    @Test
    @DisplayName("Update: Should update project successfully")
    void update_ShouldUpdateProjectSuccessfully() {
        when(repository.findById(projectId)).thenReturn(Optional.of(project));
        when(riskCalculatorService.calculateRisk(any(), any(), any())).thenReturn(RiskLevel.BAIXO_RISCO);
        when(repository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponse response = service.update(projectId, projectRequest);

        assertNotNull(response);
        assertEquals(projectRequest.name(), response.name());
        verify(riskCalculatorService, times(1)).calculateRisk(any(), any(), any());
        verify(repository, times(1)).save(any(Project.class));
    }

    @Test
    @DisplayName("Update: Should throw BusinessRuleException when end date is before start date")
    void update_ShouldThrowExceptionWhenEndDateIsBeforeStartDate() {
        ProjectRequest invalidRequest = new ProjectRequest(
                "Invalid Project",
                LocalDate.now(),
                LocalDate.now().minusDays(1),
                new BigDecimal("100000"),
                "Description",
                1L
        );

        assertThrows(BusinessRuleException.class, () -> service.update(projectId, invalidRequest));
        verify(repository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("UpdateStatus: Should update status on valid transition")
    void updateStatus_ShouldUpdateOnValidTransition() {
        project.setStatus(ProjectStatus.EM_ANALISE);
        when(repository.findById(projectId)).thenReturn(Optional.of(project));
        doNothing().when(statusTransitionValidator).validate(any(), any());
        when(repository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponse response = service.updateStatus(projectId, ProjectStatus.ANALISE_REALIZADA);

        assertEquals(ProjectStatus.ANALISE_REALIZADA, response.status());
        verify(statusTransitionValidator, times(1)).validate(any(), any());
        verify(repository, times(1)).save(any(Project.class));
    }

    @Test
    @DisplayName("UpdateStatus: Should return success when transitioning to same status")
    void updateStatus_ShouldSucceedWhenSameStatus() {
        project.setStatus(ProjectStatus.EM_ANALISE);
        when(repository.findById(projectId)).thenReturn(Optional.of(project));
        doNothing().when(statusTransitionValidator).validate(any(), any());
        when(repository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponse response = service.updateStatus(projectId, ProjectStatus.EM_ANALISE);

        assertEquals(ProjectStatus.EM_ANALISE, response.status());
        verify(statusTransitionValidator, times(1)).validate(any(), any());
        verify(repository, times(1)).save(any(Project.class));
    }

    @Test
    @DisplayName("UpdateStatus: Should throw BusinessRuleException on invalid transition")
    void updateStatus_ShouldThrowExceptionOnInvalidTransition() {
        project.setStatus(ProjectStatus.ENCERRADO);
        when(repository.findById(projectId)).thenReturn(Optional.of(project));
        doThrow(new BusinessRuleException("Invalid transition")).when(statusTransitionValidator).validate(any(), any());

        assertThrows(BusinessRuleException.class, () -> service.updateStatus(projectId, ProjectStatus.EM_ANDAMENTO));
        verify(statusTransitionValidator, times(1)).validate(any(), any());
        verify(repository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("Delete: Should soft delete project successfully")
    void delete_ShouldSoftDeleteSuccessfully() {
        project.setStatus(ProjectStatus.EM_ANALISE);
        project.setDeleted(false);
        when(repository.findById(projectId)).thenReturn(Optional.of(project));
        doNothing().when(deletionValidator).validate(any());

        service.delete(projectId);

        assertTrue(project.isDeleted());
        verify(deletionValidator, times(1)).validate(any());
        verify(repository, times(1)).save(project);
    }

    @Test
    @DisplayName("Delete: Should throw ResourceNotFoundException when already deleted")
    void delete_ShouldThrowExceptionWhenAlreadyDeleted() {
        project.setDeleted(true);
        when(repository.findById(projectId)).thenReturn(Optional.of(project));

        assertThrows(ResourceNotFoundException.class, () -> service.delete(projectId));
        verify(repository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("Delete: Should throw BusinessRuleException when status blocks deletion")
    void delete_ShouldThrowExceptionWhenStatusBlocksDeletion() {
        project.setStatus(ProjectStatus.INICIADO);
        when(repository.findById(projectId)).thenReturn(Optional.of(project));
        doThrow(new BusinessRuleException("Deletion blocked")).when(deletionValidator).validate(any());

        assertThrows(BusinessRuleException.class, () -> service.delete(projectId));
        verify(deletionValidator, times(1)).validate(any());
        verify(repository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("Recover: Should recover deleted project successfully")
    void recover_ShouldRecoverSuccessfully() {
        project.setDeleted(true);
        when(repository.findById(projectId)).thenReturn(Optional.of(project));

        service.recover(projectId);

        assertFalse(project.isDeleted());
        verify(repository, times(1)).save(project);
    }

    @Test
    @DisplayName("Recover: Should throw BusinessRuleException when project is not deleted")
    void recover_ShouldThrowExceptionWhenNotDeleted() {
        project.setDeleted(false);
        when(repository.findById(projectId)).thenReturn(Optional.of(project));

        assertThrows(BusinessRuleException.class, () -> service.recover(projectId));
        verify(repository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("FindAll: Should return paged projects")
    void findAll_ShouldReturnPagedProjects() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> page = new PageImpl<>(List.of(project));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<ProjectResponse> result = service.findAll("Test", ProjectStatus.EM_ANALISE, 1L, RiskLevel.BAIXO_RISCO, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(project.getName(), result.getContent().get(0).name());
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
}
