package br.com.jonassoares.portfolio.api.dtos;

import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import br.com.jonassoares.portfolio.domain.enums.RiskLevel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectResponse (
		UUID id,
		String name,
		LocalDate startDate,
		LocalDate expectedEndDate,
		LocalDate actualEndDate,
		BigDecimal budget,
		String description,
		Long managerId,
		ProjectStatus status,
		RiskLevel riskLevel
) {}
