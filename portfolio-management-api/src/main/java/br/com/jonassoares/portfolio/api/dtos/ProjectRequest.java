package br.com.jonassoares.portfolio.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectRequest (
		
		@NotBlank(message = "The name is required")
		String name,
		
		@NotNull(message = "The start date is required")
		LocalDate startDate,
		
		@NotNull(message = "A completion date is required")
		LocalDate expectedEndDate,
		
		@NotNull(message = "The budget is mandatory")
		@PositiveOrZero(message = "The budget must be greater than or equal to zero")
		BigDecimal budget,
		
		String description,
		
		@NotNull(message = "A manager in charge is required")
		Long managerId
){}
