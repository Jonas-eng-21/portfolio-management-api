package br.com.jonassoares.portfolio.domain.services;

import br.com.jonassoares.portfolio.api.dtos.ProjectRequest;
import br.com.jonassoares.portfolio.api.dtos.ProjectResponse;
import br.com.jonassoares.portfolio.domain.entities.Project;
import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import br.com.jonassoares.portfolio.domain.exceptions.BusinessRuleException;
import br.com.jonassoares.portfolio.domain.exceptions.ResourceNotFoundException;
import br.com.jonassoares.portfolio.domain.repositories.ProjectRepository;
import br.com.jonassoares.portfolio.domain.validators.ProjectDeletionValidator;
import br.com.jonassoares.portfolio.domain.validators.ProjectStatusTransitionValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProjectService {
	
	private final ProjectRepository repository;
	private final RiskCalculatorService riskCalculatorService;
	private final ProjectStatusTransitionValidator statusTransitionValidator;
	private final ProjectDeletionValidator deletionValidator;
	
	public ProjectService(ProjectRepository repository,
	                      RiskCalculatorService riskCalculatorService,
	                      ProjectStatusTransitionValidator statusTransitionValidator,
	                      ProjectDeletionValidator deletionValidator) {
		this.repository = repository;
		this.riskCalculatorService = riskCalculatorService;
		this.statusTransitionValidator = statusTransitionValidator;
		this.deletionValidator = deletionValidator;
	}
	
	@Transactional
	public ProjectResponse create(ProjectRequest request) {
		if (request.expectedEndDate() != null && request.startDate() != null
				&& request.expectedEndDate().isBefore(request.startDate())) {
			throw new BusinessRuleException("The expected end date cannot be earlier than the start date.");
		}
		
		Project project = new Project();
		project.setName(request.name());
		project.setStartDate(request.startDate());
		project.setExpectedEndDate(request.expectedEndDate());
		project.setBudget(request.budget());
		project.setDescription(request.description());
		project.setManagerId(request.managerId());
		project.setStatus(ProjectStatus.EM_ANALISE);
		
		project.setRiskLevel(riskCalculatorService.calculateRisk(request.budget(), request.startDate(), request.expectedEndDate()));
		
		Project savedProject = repository.save(project);
		return mapToResponse(savedProject);
	}
	
	@Transactional(readOnly = true)
	public Page<ProjectResponse> findAll(String name, ProjectStatus status, Pageable pageable) {
		return repository.findWithFilters(name, status, pageable).map(this::mapToResponse);
	}
	
	@Transactional(readOnly = true)
	public ProjectResponse findById(UUID id) {
		Project project = getProjectOrThrow(id);
		return mapToResponse(project);
	}
	
	@Transactional
	public ProjectResponse update(UUID id, ProjectRequest request) {
		if (request.expectedEndDate().isBefore(request.startDate())) {
			throw new BusinessRuleException("The expected end date cannot be earlier than the start date.");
		}
		
		Project project = getProjectOrThrow(id);
		
		project.setName(request.name());
		project.setStartDate(request.startDate());
		project.setExpectedEndDate(request.expectedEndDate());
		project.setBudget(request.budget());
		project.setDescription(request.description());
		project.setManagerId(request.managerId());
		
		project.setRiskLevel(riskCalculatorService.calculateRisk(request.budget(), request.startDate(), request.expectedEndDate()));
		
		return mapToResponse(repository.save(project));
	}
	
	@Transactional
	public ProjectResponse updateStatus(UUID id, ProjectStatus newStatus) {
		Project project = getProjectOrThrow(id);
		
		statusTransitionValidator.validate(project.getStatus(), newStatus);
		
		project.setStatus(newStatus);
		return mapToResponse(repository.save(project));
	}
	
	@Transactional
	public void delete(UUID id) {
		Project project = getProjectOrThrow(id);
		
		deletionValidator.validate(project.getStatus());
		
		project.setDeleted(true);
		repository.save(project);
	}
	
	@Transactional
	public void recover(UUID id) {
		Project project = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
		
		if (!project.isDeleted()) {
			throw new BusinessRuleException("This project is not deleted.");
		}
		
		project.setDeleted(false);
		repository.save(project);
	}
	
	private Project getProjectOrThrow(UUID id) {
		Project project = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
		if (project.isDeleted()) {
			throw new ResourceNotFoundException("Project not found with ID: " + id);
		}
		return project;
	}
	
	private ProjectResponse mapToResponse(Project project) {
		return new ProjectResponse(
				project.getId(), project.getName(), project.getStartDate(),
				project.getExpectedEndDate(), project.getActualEndDate(),
				project.getBudget(), project.getDescription(), project.getManagerId(),
				project.getStatus(), project.getRiskLevel()
		);
	}
	
}

