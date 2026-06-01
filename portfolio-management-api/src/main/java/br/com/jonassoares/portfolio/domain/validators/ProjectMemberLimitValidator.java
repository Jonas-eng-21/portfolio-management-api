package br.com.jonassoares.portfolio.domain.validators;

import br.com.jonassoares.portfolio.domain.exceptions.BusinessRuleException;
import org.springframework.stereotype.Component;

@Component
public class ProjectMemberLimitValidator {

    public void validate(long currentMemberCount) {
        if (currentMemberCount >= 10) {
            throw new BusinessRuleException("Project has reached the maximum limit of 10 members");
        }
    }
}
