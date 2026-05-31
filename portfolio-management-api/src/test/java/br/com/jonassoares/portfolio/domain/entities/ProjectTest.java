package br.com.jonassoares.portfolio.domain.entities;

import br.com.jonassoares.portfolio.domain.enums.RiskLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectTest {

    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
    }

    @Test
    @DisplayName("Should return BAIXO_RISCO when budget is null")
    void shouldReturnLowRiskWhenBudgetIsNull() {
        project.setBudget(null);
        project.setStartDate(LocalDate.now());
        project.setExpectedEndDate(LocalDate.now().plusMonths(7));
        assertEquals(RiskLevel.BAIXO_RISCO, project.getRiskLevel());
    }

    @Test
    @DisplayName("Should return BAIXO_RISCO when startDate is null")
    void shouldReturnLowRiskWhenStartDateIsNull() {
        project.setBudget(new BigDecimal("600000"));
        project.setStartDate(null);
        project.setExpectedEndDate(LocalDate.now());
        assertEquals(RiskLevel.BAIXO_RISCO, project.getRiskLevel());
    }

    @Test
    @DisplayName("Should return BAIXO_RISCO when expectedEndDate is null")
    void shouldReturnLowRiskWhenExpectedEndDateIsNull() {
        project.setBudget(new BigDecimal("600000"));
        project.setStartDate(LocalDate.now());
        project.setExpectedEndDate(null);
        assertEquals(RiskLevel.BAIXO_RISCO, project.getRiskLevel());
    }

    @ParameterizedTest(name = "Budget {0} and duration {1} months should be LOW_RISK")
    @MethodSource("lowRiskProvider")
    @DisplayName("Should return BAIXO_RISCO for low risk conditions")
    void shouldReturnLowRiskForBoundaryConditions(BigDecimal budget, int months) {
        project.setBudget(budget);
        project.setStartDate(LocalDate.now());
        project.setExpectedEndDate(LocalDate.now().plusMonths(months));
        assertEquals(RiskLevel.BAIXO_RISCO, project.getRiskLevel());
    }

    static Stream<Arguments> lowRiskProvider() {
        return Stream.of(
                Arguments.of(new BigDecimal("100000"), 2),
                Arguments.of(new BigDecimal("50000"), 2),
                Arguments.of(new BigDecimal("100000"), 1)
        );
    }

    @ParameterizedTest(name = "Budget {0} and duration {1} months should be MEDIUM_RISK")
    @MethodSource("mediumRiskProvider")
    @DisplayName("Should return MEDIO_RISCO for medium risk conditions")
    void shouldReturnMediumRiskForBoundaryConditions(BigDecimal budget, int months) {
        project.setBudget(budget);
        project.setStartDate(LocalDate.now());
        project.setExpectedEndDate(LocalDate.now().plusMonths(months));
        assertEquals(RiskLevel.MEDIO_RISCO, project.getRiskLevel());
    }

    static Stream<Arguments> mediumRiskProvider() {
        return Stream.of(
                Arguments.of(new BigDecimal("100001"), 1),
                Arguments.of(new BigDecimal("500000"), 2),
                Arguments.of(new BigDecimal("50000"), 3),
                Arguments.of(new BigDecimal("50000"), 6),
                Arguments.of(new BigDecimal("300000"), 4)
        );
    }

    @ParameterizedTest(name = "Budget {0} and duration {1} months should be HIGH_RISK")
    @MethodSource("highRiskProvider")
    @DisplayName("Should return ALTO_RISCO for high risk conditions")
    void shouldReturnHighRiskForBoundaryConditions(BigDecimal budget, int months) {
        project.setBudget(budget);
        project.setStartDate(LocalDate.now());
        project.setExpectedEndDate(LocalDate.now().plusMonths(months));
        assertEquals(RiskLevel.ALTO_RISCO, project.getRiskLevel());
    }

    static Stream<Arguments> highRiskProvider() {
        return Stream.of(
                Arguments.of(new BigDecimal("500001"), 1),
                Arguments.of(new BigDecimal("50000"), 7),
                Arguments.of(new BigDecimal("600000"), 8)
        );
    }
}
