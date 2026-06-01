package br.com.jonassoares.portfolio.api.controllers;

import br.com.jonassoares.portfolio.api.dtos.member.AllocateMemberRequest;
import br.com.jonassoares.portfolio.domain.services.ProjectMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void allocateMember(@PathVariable UUID projectId, @RequestBody AllocateMemberRequest request) {
        projectMemberService.allocateMember(projectId, request.memberId());
    }
}
