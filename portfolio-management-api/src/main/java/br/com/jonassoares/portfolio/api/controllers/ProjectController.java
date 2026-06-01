package br.com.jonassoares.portfolio.api.controllers;

import br.com.jonassoares.portfolio.api.dtos.ProjectRequest;
import br.com.jonassoares.portfolio.api.dtos.ProjectResponse;
import br.com.jonassoares.portfolio.api.dtos.ProjectStatusRequest;
import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import br.com.jonassoares.portfolio.domain.enums.RiskLevel;
import br.com.jonassoares.portfolio.domain.services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {
	
	private final ProjectService projectService;
	
	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}
	
	@PostMapping
	public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
		ProjectResponse response = projectService.create(request);
		
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.id())
				.toUri();
		
		return ResponseEntity.created(location).body(response);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ProjectResponse> findById(@PathVariable UUID id) {
		ProjectResponse response = projectService.findById(id);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping
	public ResponseEntity<Page<ProjectResponse>> findAll(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) ProjectStatus status,
			@RequestParam(required = false) Long managerId,
			@RequestParam(required = false) RiskLevel riskLevel,
			@PageableDefault(size = 10, sort = "name") Pageable pageable) {
		
		Page<ProjectResponse> responses = projectService.findAll(name, status, managerId, riskLevel, pageable);
		return ResponseEntity.ok(responses);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ProjectResponse> update(@PathVariable UUID id, @RequestBody @Valid ProjectRequest request) {
		ProjectResponse response = projectService.update(id, request);
		return ResponseEntity.ok(response);
	}
	
	@PatchMapping("/{id}/status")
	public ResponseEntity<ProjectResponse> updateStatus(
			@PathVariable UUID id,
			@RequestBody @Valid ProjectStatusRequest request) {
		
		ProjectResponse response = projectService.updateStatus(id, request.status());
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		projectService.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/{id}/recover")
	public ResponseEntity<Void> recover(@PathVariable UUID id) {
		projectService.recover(id);
		return ResponseEntity.ok().build();
	}
}
