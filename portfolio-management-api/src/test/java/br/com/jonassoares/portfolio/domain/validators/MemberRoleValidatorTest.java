package br.com.jonassoares.portfolio.domain.validators;

import br.com.jonassoares.portfolio.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberRoleValidatorTest {

    private final MemberRoleValidator validator = new MemberRoleValidator();

    @Test
    void validate_ShouldNotThrowException_WhenRoleIsFuncionario() {
        assertDoesNotThrow(() -> validator.validate("FUNCIONARIO"));
    }

    @Test
    void validate_ShouldNotThrowException_WhenRoleIsFuncionarioLowerCase() {
        assertDoesNotThrow(() -> validator.validate("funcionario"));
    }

    @Test
    void validate_ShouldThrowException_WhenRoleIsGerente() {
        assertThrows(BusinessRuleException.class, () -> validator.validate("GERENTE"),
                "Only members with role FUNCIONARIO can be allocated");
    }

    @Test
    void validate_ShouldThrowException_WhenRoleIsNull() {
        assertThrows(BusinessRuleException.class, () -> validator.validate(null),
                "Only members with role FUNCIONARIO can be allocated");
    }
}
