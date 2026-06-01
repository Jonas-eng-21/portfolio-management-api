package br.com.jonassoares.portfolio.domain.services;

import br.com.jonassoares.portfolio.api.dtos.member.MemberResponse;
import br.com.jonassoares.portfolio.domain.entities.Project;
import br.com.jonassoares.portfolio.domain.entities.ProjectMember;
import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import br.com.jonassoares.portfolio.domain.exceptions.ResourceNotFoundException;
import br.com.jonassoares.portfolio.domain.repositories.ProjectMemberRepository;
import br.com.jonassoares.portfolio.domain.repositories.ProjectRepository;
import br.com.jonassoares.portfolio.domain.validators.MemberAllocationValidator;
import br.com.jonassoares.portfolio.domain.validators.MemberRoleValidator;
import br.com.jonassoares.portfolio.domain.validators.ProjectMemberLimitValidator;
import br.com.jonassoares.portfolio.infrastructure.clients.MemberApiClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static br.com.jonassoares.portfolio.domain.enums.ProjectStatus.*;

@Service
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final MemberApiClient memberApiClient;
    private final ProjectMemberLimitValidator projectMemberLimitValidator;
    private final MemberAllocationValidator memberAllocationValidator;
    private final MemberRoleValidator memberRoleValidator;

    private static final List<ProjectStatus> ACTIVE_STATUSES = List.of(
            EM_ANALISE, ANALISE_REALIZADA, ANALISE_APROVADA, INICIADO, PLANEJADO, EM_ANDAMENTO
    );

    public ProjectMemberService(ProjectMemberRepository projectMemberRepository,
                                ProjectRepository projectRepository,
                                MemberApiClient memberApiClient,
                                ProjectMemberLimitValidator projectMemberLimitValidator,
                                MemberAllocationValidator memberAllocationValidator,
                                MemberRoleValidator memberRoleValidator) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.memberApiClient = memberApiClient;
        this.projectMemberLimitValidator = projectMemberLimitValidator;
        this.memberAllocationValidator = memberAllocationValidator;
        this.memberRoleValidator = memberRoleValidator;
    }

    @Transactional
    public void allocateMember(UUID projectId, Long memberId) {
        Project project = projectRepository.findById(projectId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        MemberResponse member = memberApiClient.getMemberById(memberId);

        memberRoleValidator.validate(member.role());

        long currentMemberCount = projectMemberRepository.countByProject(project);
        projectMemberLimitValidator.validate(currentMemberCount);

        long activeProjectsCount = projectMemberRepository.countByMemberIdAndProject_StatusIn(memberId, ACTIVE_STATUSES);
        memberAllocationValidator.validate(activeProjectsCount);

        ProjectMember projectMember = new ProjectMember();
        projectMember.setProject(project);
        projectMember.setMemberId(memberId);

        projectMemberRepository.save(projectMember);
    }
}
