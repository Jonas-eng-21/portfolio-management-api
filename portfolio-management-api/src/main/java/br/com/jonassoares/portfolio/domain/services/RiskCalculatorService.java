package br.com.jonassoares.portfolio.domain.services;

import br.com.jonassoares.portfolio.domain.enums.RiskLevel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class RiskCalculatorService {

    public RiskLevel calculateRisk(BigDecimal budget, LocalDate startDate, LocalDate expectedEndDate) {
        if (budget == null || startDate == null || expectedEndDate == null) {
            return RiskLevel.BAIXO_RISCO;
        }

        long durationInMonths = ChronoUnit.MONTHS.between(startDate, expectedEndDate);

        if (budget.compareTo(new BigDecimal("500000")) > 0 || durationInMonths > 6) {
            return RiskLevel.ALTO_RISCO;
        }

        if ((budget.compareTo(new BigDecimal("100000")) > 0 && budget.compareTo(new BigDecimal("500000")) <= 0)
                || (durationInMonths > 3 && durationInMonths <= 6)) {
            return RiskLevel.MEDIO_RISCO;
        }

        return RiskLevel.BAIXO_RISCO;
    }
}
