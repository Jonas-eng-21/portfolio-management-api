package br.com.jonassoares.portfolio.domain.validators;

import br.com.jonassoares.portfolio.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberAllocationValidatorTest {

    private final MemberAllocationValidator validator = new MemberAllocationValidator();

    @Test
    void validate_ShouldNotThrowException_WhenActiveProjectsCountIs2() {
        assertDoesNotThrow(() -> validator.validate(2));
    }

    @Test
    void validate_ShouldThrowException_WhenActiveProjectsCountIs3() {
        assertThrows(BusinessRuleException.class, () -> validator.validate(3),
                "Member is already allocated to the maximum of 3 active projects");
    }

    @Test
    void validate_ShouldThrowException_WhenActiveProjectsCountIs4() {
        assertThrows(BusinessRuleException.class, () -> validator.validate(4),
                "Member is already allocated to the maximum of 3 active projects");
    }
}
