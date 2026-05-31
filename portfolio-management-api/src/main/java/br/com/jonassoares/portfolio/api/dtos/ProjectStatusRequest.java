package br.com.jonassoares.portfolio.api.dtos;

import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;

public record ProjectStatusRequest(
		@NotNull(message = "The new status is required.")
		ProjectStatus status
) {}
