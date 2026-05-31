package br.com.jonassoares.portfolio.domain.services;

import br.com.jonassoares.portfolio.api.dtos.ProjectRequest;
import br.com.jonassoares.portfolio.api.dtos.ProjectResponse;
import br.com.jonassoares.portfolio.domain.entities.Project;
import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import br.com.jonassoares.portfolio.domain.exceptions.BusinessRuleException;
import br.com.jonassoares.portfolio.domain.exceptions.ResourceNotFoundException;
import br.com.jonassoares.portfolio.domain.repositories.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProjectService {
	
	private final ProjectRepository repository;
	
	public ProjectService(ProjectRepository repository) {
		this.repository = repository;
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
		
		Project savedProject = repository.save(project);
		
		return new ProjectResponse(
				savedProject.getId(),
				savedProject.getName(),
				savedProject.getStartDate(),
				savedProject.getExpectedEndDate(),
				savedProject.getActualEndDate(),
				savedProject.getBudget(),
				savedProject.getDescription(),
				savedProject.getManagerId(),
				savedProject.getStatus(),
				savedProject.getRiskLevel()
		);
	}
	
	@Transactional(readOnly = true)
	public Page<ProjectResponse> findAll(String name, ProjectStatus status, Pageable pageable) {
		Page<Project> projectsPage = repository.findWithFilters(name, status, pageable);
		
		return projectsPage.map(project -> new ProjectResponse(
				project.getId(),
				project.getName(),
				project.getStartDate(),
				project.getExpectedEndDate(),
				project.getActualEndDate(),
				project.getBudget(),
				project.getDescription(),
				project.getManagerId(),
				project.getStatus(),
				project.getRiskLevel()
		));
	}
	
	@Transactional(readOnly = true)
	public ProjectResponse findById(UUID id) {
		Project project = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
		
		if (project.isDeleted()) {
			throw new ResourceNotFoundException("Project not found with ID: " + id);
		}
		
		return new ProjectResponse(
				project.getId(),
				project.getName(),
				project.getStartDate(),
				project.getExpectedEndDate(),
				project.getActualEndDate(),
				project.getBudget(),
				project.getDescription(),
				project.getManagerId(),
				project.getStatus(),
				project.getRiskLevel()
		);
	}
	
	@Transactional
	public ProjectResponse update(UUID id, ProjectRequest request) {
		
		if (request.expectedEndDate().isBefore(request.startDate())) {
			throw new BusinessRuleException("The expected end date cannot be earlier than the start date.");
		}
		
		Project project = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
		
		project.setName(request.name());
		project.setStartDate(request.startDate());
		project.setExpectedEndDate(request.expectedEndDate());
		project.setBudget(request.budget());
		project.setDescription(request.description());
		project.setManagerId(request.managerId());
		
		project = repository.save(project);
		
		return new ProjectResponse(
				project.getId(),
				project.getName(),
				project.getStartDate(),
				project.getExpectedEndDate(),
				project.getActualEndDate(),
				project.getBudget(),
				project.getDescription(),
				project.getManagerId(),
				project.getStatus(),
				project.getRiskLevel()
		);
	}
	
	@Transactional
	public ProjectResponse updateStatus(UUID id, ProjectStatus newStatus) {
		Project project = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
		
		if (!isValidTransition(project.getStatus(), newStatus)) {
			throw new BusinessRuleException("Invalid status transition from " + project.getStatus() + " to " + newStatus + ".");
		}
		
		project.setStatus(newStatus);
		project = repository.save(project);
		
		return new ProjectResponse(
				project.getId(),
				project.getName(),
				project.getStartDate(),
				project.getExpectedEndDate(),
				project.getActualEndDate(),
				project.getBudget(),
				project.getDescription(),
				project.getManagerId(),
				project.getStatus(),
				project.getRiskLevel()
		);
	}
	
	private boolean isValidTransition(ProjectStatus current, ProjectStatus next) {

		if (current == next) return true;
		
		return switch (current) {
			case EM_ANALISE -> next == ProjectStatus.ANALISE_REALIZADA || next == ProjectStatus.CANCELADO;
			case ANALISE_REALIZADA -> next == ProjectStatus.ANALISE_APROVADA || next == ProjectStatus.CANCELADO;
			case ANALISE_APROVADA -> next == ProjectStatus.PLANEJADO || next == ProjectStatus.CANCELADO;
			case PLANEJADO -> next == ProjectStatus.INICIADO || next == ProjectStatus.CANCELADO;
			case INICIADO -> next == ProjectStatus.EM_ANDAMENTO || next == ProjectStatus.CANCELADO;
			case EM_ANDAMENTO -> next == ProjectStatus.ENCERRADO || next == ProjectStatus.CANCELADO;
			case ENCERRADO, CANCELADO -> false;
		};
	}
	
	@Transactional
	public void delete(UUID id) {
		Project project = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
		
		if (project.isDeleted()) {
			throw new BusinessRuleException("This project is already deleted.");
		}
		
		if (project.getStatus() == ProjectStatus.INICIADO ||
				project.getStatus() == ProjectStatus.EM_ANDAMENTO ||
				project.getStatus() == ProjectStatus.ENCERRADO) {
			throw new BusinessRuleException("Deletion is blocked. You cannot delete a project with status: " + project.getStatus() + ".");
		}
		
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
	
}

