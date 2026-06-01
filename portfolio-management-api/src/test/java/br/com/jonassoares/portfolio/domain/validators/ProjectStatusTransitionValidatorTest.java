package br.com.jonassoares.portfolio.domain.validators;

import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import br.com.jonassoares.portfolio.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectStatusTransitionValidatorTest {

    private ProjectStatusTransitionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ProjectStatusTransitionValidator();
    }

    @Test
    void shouldAllowValidTransition() {
        assertDoesNotThrow(() -> validator.validate(ProjectStatus.EM_ANALISE, ProjectStatus.ANALISE_REALIZADA));
        assertDoesNotThrow(() -> validator.validate(ProjectStatus.ANALISE_REALIZADA, ProjectStatus.ANALISE_APROVADA));
        assertDoesNotThrow(() -> validator.validate(ProjectStatus.ANALISE_APROVADA, ProjectStatus.INICIADO));
        assertDoesNotThrow(() -> validator.validate(ProjectStatus.INICIADO, ProjectStatus.PLANEJADO));
        assertDoesNotThrow(() -> validator.validate(ProjectStatus.PLANEJADO, ProjectStatus.EM_ANDAMENTO));
        assertDoesNotThrow(() -> validator.validate(ProjectStatus.EM_ANDAMENTO, ProjectStatus.ENCERRADO));
    }

    @Test
    void shouldRejectInvalidTransition() {
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, 
            () -> validator.validate(ProjectStatus.EM_ANALISE, ProjectStatus.INICIADO));
        assertEquals("Invalid status transition from EM_ANALISE to INICIADO", exception.getMessage());

        assertThrows(BusinessRuleException.class, 
            () -> validator.validate(ProjectStatus.ENCERRADO, ProjectStatus.EM_ANALISE));
    }

    @ParameterizedTest
    @EnumSource(ProjectStatus.class)
    void shouldAllowCancellationFromAnyStatus(ProjectStatus currentStatus) {
        assertDoesNotThrow(() -> validator.validate(currentStatus, ProjectStatus.CANCELADO));
    }

    @ParameterizedTest
    @EnumSource(ProjectStatus.class)
    void shouldAllowSameStatusTransition(ProjectStatus status) {
        assertDoesNotThrow(() -> validator.validate(status, status));
    }
}
