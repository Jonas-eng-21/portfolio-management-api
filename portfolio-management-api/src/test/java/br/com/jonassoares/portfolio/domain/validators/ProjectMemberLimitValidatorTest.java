package br.com.jonassoares.portfolio.domain.validators;

import br.com.jonassoares.portfolio.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectMemberLimitValidatorTest {

    private final ProjectMemberLimitValidator validator = new ProjectMemberLimitValidator();

    @Test
    void validate_ShouldNotThrowException_WhenCurrentMemberCountIs9() {
        assertDoesNotThrow(() -> validator.validate(9));
    }

    @Test
    void validate_ShouldThrowException_WhenCurrentMemberCountIs10() {
        assertThrows(BusinessRuleException.class, () -> validator.validate(10), 
                "Project has reached the maximum limit of 10 members");
    }

    @Test
    void validate_ShouldThrowException_WhenCurrentMemberCountIs11() {
        assertThrows(BusinessRuleException.class, () -> validator.validate(11), 
                "Project has reached the maximum limit of 10 members");
    }
}
