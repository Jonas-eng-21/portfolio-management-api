package br.com.jonassoares.portfolio.domain.entities;

import br.com.jonassoares.portfolio.domain.enums.RiskLevel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProjectTest {
	
	@Test
	void shouldReturnAltoRiscoWhenBudgetIsGreaterThan500k() {
		Project project = new Project();
		project.setBudget(new BigDecimal("500001"));
		project.setStartDate(LocalDate.now());
		project.setExpectedEndDate(LocalDate.now().plusMonths(2));
		
		Assertions.assertEquals(RiskLevel.ALTO_RISCO, project.getRiskLevel());
	}
	
	@Test
	void shouldReturnMedioRiscoWhenDurationIs4Months() {
		Project project = new Project();
		project.setBudget(new BigDecimal("50000"));
		project.setStartDate(LocalDate.now());
		project.setExpectedEndDate(LocalDate.now().plusMonths(4));
		
		Assertions.assertEquals(RiskLevel.MEDIO_RISCO, project.getRiskLevel());
	}
	
	@Test
	void shouldReturnBaixoRiscoWhenBudgetAndDurationAreLow() {
		Project project = new Project();
		project.setBudget(new BigDecimal("99000"));
		project.setStartDate(LocalDate.now());
		project.setExpectedEndDate(LocalDate.now().plusMonths(2));
		
		Assertions.assertEquals(RiskLevel.BAIXO_RISCO, project.getRiskLevel());
	}
	
}
