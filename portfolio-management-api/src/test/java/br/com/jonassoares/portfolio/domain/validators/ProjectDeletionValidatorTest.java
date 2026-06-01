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

class ProjectDeletionValidatorTest {

    private ProjectDeletionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ProjectDeletionValidator();
    }

    @ParameterizedTest
    @EnumSource(value = ProjectStatus.class, names = {"EM_ANALISE", "ANALISE_REALIZADA", "ANALISE_APROVADA", "PLANEJADO", "CANCELADO"})
    void shouldAllowDeletionForValidStatuses(ProjectStatus status) {
        assertDoesNotThrow(() -> validator.validate(status));
    }

    @Test
    void shouldRejectDeletionWhenStarted() {
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, 
            () -> validator.validate(ProjectStatus.INICIADO));
        assertEquals("Deletion is blocked for projects with status INICIADO", exception.getMessage());
    }

    @Test
    void shouldRejectDeletionWhenInProgress() {
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, 
            () -> validator.validate(ProjectStatus.EM_ANDAMENTO));
        assertEquals("Deletion is blocked for projects with status EM_ANDAMENTO", exception.getMessage());
    }

    @Test
    void shouldRejectDeletionWhenFinished() {
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, 
            () -> validator.validate(ProjectStatus.ENCERRADO));
        assertEquals("Deletion is blocked for projects with status ENCERRADO", exception.getMessage());
    }
}
