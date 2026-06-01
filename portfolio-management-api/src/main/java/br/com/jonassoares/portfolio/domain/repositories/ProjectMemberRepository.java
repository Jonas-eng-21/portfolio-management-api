package br.com.jonassoares.portfolio.domain.repositories;

import br.com.jonassoares.portfolio.domain.entities.Project;
import br.com.jonassoares.portfolio.domain.entities.ProjectMember;
import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {

    long countByProject(Project project);

    long countByMemberIdAndProject_StatusIn(Long memberId, List<ProjectStatus> statuses);
}
