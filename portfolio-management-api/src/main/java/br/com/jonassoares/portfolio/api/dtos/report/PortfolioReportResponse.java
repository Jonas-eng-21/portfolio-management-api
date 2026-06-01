package br.com.jonassoares.portfolio.api.dtos.report;

import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import java.math.BigDecimal;
import java.util.Map;

public record PortfolioReportResponse(
    Map<ProjectStatus, Long> projectsByStatus,
    Map<ProjectStatus, BigDecimal> budgetByStatus,
    Long averageClosedProjectDurationDays,
    Long uniqueMembersAllocated
) {
}
