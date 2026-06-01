package br.com.jonassoares.portfolio.domain.services;

import br.com.jonassoares.portfolio.api.dtos.report.PortfolioReportResponse;
import br.com.jonassoares.portfolio.domain.entities.Project;
import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import br.com.jonassoares.portfolio.domain.repositories.ProjectMemberRepository;
import br.com.jonassoares.portfolio.domain.repositories.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioReportServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @InjectMocks
    private PortfolioReportService portfolioReportService;

    @Test
    @DisplayName("shouldReturnProjectsGroupedByStatus")
    void shouldReturnProjectsGroupedByStatus() {
        Object[] row1 = {ProjectStatus.INICIADO, 5L};
        Object[] row2 = {ProjectStatus.PLANEJADO, 3L};
        when(projectRepository.countProjectsByStatus()).thenReturn(List.of(row1, row2));
        when(projectRepository.sumBudgetByStatus()).thenReturn(Collections.emptyList());
        when(projectRepository.findClosedProjects()).thenReturn(Collections.emptyList());
        when(projectMemberRepository.countUniqueAllocatedMembers()).thenReturn(0L);

        PortfolioReportResponse report = portfolioReportService.generateReport();

        assertThat(report.projectsByStatus()).hasSize(2);
        assertThat(report.projectsByStatus().get(ProjectStatus.INICIADO)).isEqualTo(5L);
        assertThat(report.projectsByStatus().get(ProjectStatus.PLANEJADO)).isEqualTo(3L);
    }

    @Test
    @DisplayName("shouldReturnBudgetGroupedByStatus")
    void shouldReturnBudgetGroupedByStatus() {
        Object[] row1 = {ProjectStatus.INICIADO, new BigDecimal("1000.00")};
        Object[] row2 = {ProjectStatus.PLANEJADO, new BigDecimal("2000.00")};
        when(projectRepository.sumBudgetByStatus()).thenReturn(List.of(row1, row2));
        when(projectRepository.countProjectsByStatus()).thenReturn(Collections.emptyList());
        when(projectRepository.findClosedProjects()).thenReturn(Collections.emptyList());
        when(projectMemberRepository.countUniqueAllocatedMembers()).thenReturn(0L);

        PortfolioReportResponse report = portfolioReportService.generateReport();

        assertThat(report.budgetByStatus()).hasSize(2);
        assertThat(report.budgetByStatus().get(ProjectStatus.INICIADO)).isEqualByComparingTo("1000.00");
        assertThat(report.budgetByStatus().get(ProjectStatus.PLANEJADO)).isEqualByComparingTo("2000.00");
    }

    @Test
    @DisplayName("shouldCalculateAverageDurationOfClosedProjects")
    void shouldCalculateAverageDurationOfClosedProjects() {
        Project p1 = new Project();
        p1.setStartDate(LocalDate.of(2023, 1, 1));
        p1.setActualEndDate(LocalDate.of(2023, 1, 11)); // 10 days

        Project p2 = new Project();
        p2.setStartDate(LocalDate.of(2023, 1, 1));
        p2.setExpectedEndDate(LocalDate.of(2023, 1, 21)); // 20 days (actualEndDate is null)

        when(projectRepository.findClosedProjects()).thenReturn(List.of(p1, p2));
        when(projectRepository.countProjectsByStatus()).thenReturn(Collections.emptyList());
        when(projectRepository.sumBudgetByStatus()).thenReturn(Collections.emptyList());
        when(projectMemberRepository.countUniqueAllocatedMembers()).thenReturn(0L);

        PortfolioReportResponse report = portfolioReportService.generateReport();

        assertThat(report.averageClosedProjectDurationDays()).isEqualTo(15L); // (10+20)/2
    }

    @Test
    @DisplayName("shouldReturnUniqueAllocatedMembers")
    void shouldReturnUniqueAllocatedMembers() {
        when(projectMemberRepository.countUniqueAllocatedMembers()).thenReturn(10L);
        when(projectRepository.countProjectsByStatus()).thenReturn(Collections.emptyList());
        when(projectRepository.sumBudgetByStatus()).thenReturn(Collections.emptyList());
        when(projectRepository.findClosedProjects()).thenReturn(Collections.emptyList());

        PortfolioReportResponse report = portfolioReportService.generateReport();

        assertThat(report.uniqueMembersAllocated()).isEqualTo(10L);
    }
}
