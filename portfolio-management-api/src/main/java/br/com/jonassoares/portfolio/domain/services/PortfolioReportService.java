package br.com.jonassoares.portfolio.domain.services;

import br.com.jonassoares.portfolio.api.dtos.report.PortfolioReportResponse;
import br.com.jonassoares.portfolio.domain.entities.Project;
import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import br.com.jonassoares.portfolio.domain.repositories.ProjectMemberRepository;
import br.com.jonassoares.portfolio.domain.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioReportService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public PortfolioReportResponse generateReport() {
        Map<ProjectStatus, Long> projectsByStatus = projectRepository.countProjectsByStatus().stream()
                .collect(Collectors.toMap(
                        array -> (ProjectStatus) array[0],
                        array -> (Long) array[1]
                ));

        Map<ProjectStatus, BigDecimal> budgetByStatus = projectRepository.sumBudgetByStatus().stream()
                .collect(Collectors.toMap(
                        array -> (ProjectStatus) array[0],
                        array -> (BigDecimal) array[1]
                ));

        List<Project> closedProjects = projectRepository.findClosedProjects();
        long averageClosedProjectDurationDays = 0L;

        if (!closedProjects.isEmpty()) {
            averageClosedProjectDurationDays = (long) closedProjects.stream()
                    .mapToLong(p -> {
                        var endDate = p.getActualEndDate() != null ? p.getActualEndDate() : p.getExpectedEndDate();
                        return ChronoUnit.DAYS.between(p.getStartDate(), endDate);
                    })
                    .average()
                    .orElse(0.0);
        }

        long uniqueMembersAllocated = projectMemberRepository.countUniqueAllocatedMembers();

        return new PortfolioReportResponse(
                projectsByStatus,
                budgetByStatus,
                averageClosedProjectDurationDays,
                uniqueMembersAllocated
        );
    }
}
