package br.com.jonassoares.portfolio.domain.entities;

import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import br.com.jonassoares.portfolio.domain.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
public class Project {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;
	
	@Column(name = "expected_end_date", nullable = false)
	private LocalDate expectedEndDate;
	
	@Column(name = "actual_end_date")
	private LocalDate actualEndDate;
	
	@Column(nullable = false)
	private BigDecimal budget;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Column(nullable = false)
	private boolean deleted = false;
	
	@Column(name = "manager_id", nullable = false)
	private Long managerId;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProjectStatus status;
	
	@Transient
	public RiskLevel getRiskLevel() {
		if (budget == null || startDate == null || expectedEndDate == null) {
			return RiskLevel.BAIXO_RISCO;
		}
		
		long monthsDuration = ChronoUnit.MONTHS.between(startDate, expectedEndDate);
		boolean isHighBudget = budget.compareTo(new BigDecimal("500000")) > 0;
		boolean isMediumBudget = budget.compareTo(new BigDecimal("100000")) > 0;
		
		if (isHighBudget || monthsDuration > 6) {
			return RiskLevel.ALTO_RISCO;
		}
		
		if (isMediumBudget || (monthsDuration >= 3 && monthsDuration <= 6)) {
			return RiskLevel.MEDIO_RISCO;
		}
		
		return RiskLevel.BAIXO_RISCO;
	}
}
