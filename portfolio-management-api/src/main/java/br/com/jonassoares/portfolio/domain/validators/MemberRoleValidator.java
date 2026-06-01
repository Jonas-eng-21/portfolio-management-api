package br.com.jonassoares.portfolio.domain.validators;

import br.com.jonassoares.portfolio.domain.exceptions.BusinessRuleException;
import org.springframework.stereotype.Component;

@Component
public class MemberRoleValidator {

    public void validate(String role) {
        if (role == null || !"FUNCIONARIO".equalsIgnoreCase(role)) {
            throw new BusinessRuleException("Only members with role FUNCIONARIO can be allocated");
        }
    }
}
