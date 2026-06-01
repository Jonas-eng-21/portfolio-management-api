package br.com.jonassoares.portfolio.domain.services;

import br.com.jonassoares.portfolio.domain.enums.RiskLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RiskCalculatorServiceTest {

    private RiskCalculatorService riskCalculatorService;

    @BeforeEach
    void setUp() {
        riskCalculatorService = new RiskCalculatorService();
    }

    @Test
    void shouldCalculateLowRisk_WhenBudgetIsLowAndDurationIsShort() {
        BigDecimal budget = new BigDecimal("100000");
        LocalDate startDate = LocalDate.now();
        LocalDate expectedEndDate = startDate.plusMonths(3);

        RiskLevel result = riskCalculatorService.calculateRisk(budget, startDate, expectedEndDate);

        assertEquals(RiskLevel.BAIXO_RISCO, result);
    }

    @Test
    void shouldCalculateMediumRisk_WhenBudgetIsMedium() {
        BigDecimal budget = new BigDecimal("250000");
        LocalDate startDate = LocalDate.now();
        LocalDate expectedEndDate = startDate.plusMonths(2);

        RiskLevel result = riskCalculatorService.calculateRisk(budget, startDate, expectedEndDate);

        assertEquals(RiskLevel.MEDIO_RISCO, result);
    }

    @Test
    void shouldCalculateMediumRisk_WhenDurationIsMedium() {
        BigDecimal budget = new BigDecimal("50000");
        LocalDate startDate = LocalDate.now();
        LocalDate expectedEndDate = startDate.plusMonths(5);

        RiskLevel result = riskCalculatorService.calculateRisk(budget, startDate, expectedEndDate);

        assertEquals(RiskLevel.MEDIO_RISCO, result);
    }

    @Test
    void shouldCalculateHighRisk_WhenBudgetIsHigh() {
        BigDecimal budget = new BigDecimal("600000");
        LocalDate startDate = LocalDate.now();
        LocalDate expectedEndDate = startDate.plusMonths(2);

        RiskLevel result = riskCalculatorService.calculateRisk(budget, startDate, expectedEndDate);

        assertEquals(RiskLevel.ALTO_RISCO, result);
    }

    @Test
    void shouldCalculateHighRisk_WhenDurationIsLong() {
        BigDecimal budget = new BigDecimal("50000");
        LocalDate startDate = LocalDate.now();
        LocalDate expectedEndDate = startDate.plusMonths(7);

        RiskLevel result = riskCalculatorService.calculateRisk(budget, startDate, expectedEndDate);

        assertEquals(RiskLevel.ALTO_RISCO, result);
    }

    @Test
    void shouldDefaultToLowRisk_WhenInputsAreNull() {
        RiskLevel result = riskCalculatorService.calculateRisk(null, null, null);

        assertEquals(RiskLevel.BAIXO_RISCO, result);
    }
}
