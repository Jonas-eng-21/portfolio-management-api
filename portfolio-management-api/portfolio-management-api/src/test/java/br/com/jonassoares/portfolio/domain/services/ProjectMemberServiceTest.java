package br.com.jonassoares.portfolio.domain.services;

import br.com.jonassoares.portfolio.api.dtos.member.MemberResponse;
import br.com.jonassoares.portfolio.domain.entities.Project;
import br.com.jonassoares.portfolio.domain.entities.ProjectMember;
import br.com.jonassoares.portfolio.domain.exceptions.ResourceNotFoundException;
import br.com.jonassoares.portfolio.domain.repositories.ProjectMemberRepository;
import br.com.jonassoares.portfolio.domain.repositories.ProjectRepository;
import br.com.jonassoares.portfolio.domain.validators.MemberAllocationValidator;
import br.com.jonassoares.portfolio.domain.validators.MemberRoleValidator;
import br.com.jonassoares.portfolio.domain.validators.ProjectMemberLimitValidator;
import br.com.jonassoares.portfolio.infrastructure.clients.MemberApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectMemberServiceTest {

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MemberApiClient memberApiClient;

    @Mock
    private ProjectMemberLimitValidator projectMemberLimitValidator;

    @Mock
    private MemberAllocationValidator memberAllocationValidator;

    @Mock
    private MemberRoleValidator memberRoleValidator;

    @InjectMocks
    private ProjectMemberService projectMemberService;

    private UUID projectId;
    private Long memberId;
    private Project project;
    private MemberResponse memberResponse;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        memberId = 1L;
        project = new Project();
        project.setId(projectId);
        memberResponse = new MemberResponse(memberId, "John Doe", "FUNCIONARIO");
    }

    @Test
    void allocateMember_ShouldSucceed_WhenAllValidationsPass() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(memberApiClient.getMemberById(memberId)).thenReturn(memberResponse);
        when(projectMemberRepository.countByProject(project)).thenReturn(5L);
        when(projectMemberRepository.countByMemberIdAndProject_StatusIn(anyLong(), anyList())).thenReturn(1L);

        projectMemberService.allocateMember(projectId, memberId);

        verify(memberRoleValidator).validate("FUNCIONARIO");
        verify(projectMemberLimitValidator).validate(5L);
        verify(memberAllocationValidator).validate(1L);
        verify(projectMemberRepository).save(any(ProjectMember.class));
    }

    @Test
    void allocateMember_ShouldThrowResourceNotFoundException_WhenProjectNotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectMemberService.allocateMember(projectId, memberId));

        verifyNoInteractions(memberApiClient, memberRoleValidator, projectMemberLimitValidator, memberAllocationValidator, projectMemberRepository);
    }

    @Test
    void allocateMember_ShouldThrowResourceNotFoundException_WhenProjectIsDeleted() {
        project.setDeleted(true);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        assertThrows(ResourceNotFoundException.class, () -> projectMemberService.allocateMember(projectId, memberId));

        verifyNoInteractions(memberApiClient, memberRoleValidator, projectMemberLimitValidator, memberAllocationValidator, projectMemberRepository);
    }

    @Test
    void allocateMember_ShouldFail_WhenRoleValidatorThrowsException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(memberApiClient.getMemberById(memberId)).thenReturn(memberResponse);
        doThrow(new RuntimeException("Invalid role")).when(memberRoleValidator).validate("FUNCIONARIO");

        assertThrows(RuntimeException.class, () -> projectMemberService.allocateMember(projectId, memberId));

        verify(memberRoleValidator).validate("FUNCIONARIO");
        verifyNoInteractions(projectMemberLimitValidator, memberAllocationValidator, projectMemberRepository);
    }

    @Test
    void allocateMember_ShouldFail_WhenLimitValidatorThrowsException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(memberApiClient.getMemberById(memberId)).thenReturn(memberResponse);
        when(projectMemberRepository.countByProject(project)).thenReturn(10L);
        doThrow(new RuntimeException("Limit reached")).when(projectMemberLimitValidator).validate(10L);

        assertThrows(RuntimeException.class, () -> projectMemberService.allocateMember(projectId, memberId));

        verify(projectMemberLimitValidator).validate(10L);
        verifyNoInteractions(memberAllocationValidator, projectMemberRepository);
    }

    @Test
    void allocateMember_ShouldFail_WhenAllocationValidatorThrowsException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(memberApiClient.getMemberById(memberId)).thenReturn(memberResponse);
        when(projectMemberRepository.countByProject(project)).thenReturn(5L);
        when(projectMemberRepository.countByMemberIdAndProject_StatusIn(anyLong(), anyList())).thenReturn(3L);
        doThrow(new RuntimeException("Too many projects")).when(memberAllocationValidator).validate(3L);

        assertThrows(RuntimeException.class, () -> projectMemberService.allocateMember(projectId, memberId));

        verify(memberAllocationValidator).validate(3L);
        verify(projectMemberRepository, never()).save(any(ProjectMember.class));
    }
}
