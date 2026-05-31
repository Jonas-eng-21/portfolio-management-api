package br.com.jonassoares.portfolio.domain.services;

import br.com.jonassoares.portfolio.api.dtos.ProjectRequest;
import br.com.jonassoares.portfolio.api.dtos.ProjectResponse;
import br.com.jonassoares.portfolio.domain.entities.Project;
import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import br.com.jonassoares.portfolio.domain.repositories.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {
	
	private final ProjectRepository repository;
	
	public ProjectService(ProjectRepository repository) {
		this.repository = repository;
	}
	
	@Transactional
	public ProjectResponse create(ProjectRequest request) {
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
	
}

