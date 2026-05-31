package br.com.jonassoares.portfolio.api.controllers;

import br.com.jonassoares.portfolio.api.dtos.ProjectRequest;
import br.com.jonassoares.portfolio.api.dtos.ProjectResponse;
import br.com.jonassoares.portfolio.domain.services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/projects")
public class ProjectController {
	
	private final ProjectService projectService;
	
	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}
	
	@PostMapping
	public ResponseEntity<ProjectResponse> create(@RequestBody @Valid ProjectRequest request) {
		ProjectResponse response = projectService.create(request);
		
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.id())
				.toUri();
		
		return ResponseEntity.created(location).body(response);
	}
}
