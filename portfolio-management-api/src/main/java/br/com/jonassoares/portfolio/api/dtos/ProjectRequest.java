package br.com.jonassoares.portfolio.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectRequest (
		
		@NotBlank(message = "O nome é obrigatório")
		String name,
		
		@NotNull(message = "A data de início é obrigatória")
		LocalDate startDate,
		
		@NotNull(message = "A previsão de término é obrigatória")
		LocalDate expectedEndDate,
		
		@NotNull @PositiveOrZero(message = "O orçamento deve ser maior ou igual a zero")
		BigDecimal budget,
		
		String description,
		
		@NotNull(message = "O gerente responsável é obrigatório")
		Long managerId
){}
