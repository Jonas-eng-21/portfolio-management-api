package br.com.jonassoares.portfolio.domain.validators;

import br.com.jonassoares.portfolio.domain.exceptions.BusinessRuleException;
import org.springframework.stereotype.Component;

@Component
public class MemberAllocationValidator {

    public void validate(long activeProjectsCount) {
        if (activeProjectsCount >= 3) {
            throw new BusinessRuleException("Member is already allocated to the maximum of 3 active projects");
        }
    }
}
