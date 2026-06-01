package br.com.jonassoares.portfolio.domain.validators;

import br.com.jonassoares.portfolio.domain.enums.ProjectStatus;
import br.com.jonassoares.portfolio.domain.exceptions.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ProjectDeletionValidator {

    private static final Set<ProjectStatus> FORBIDDEN_STATUSES = Set.of(
            ProjectStatus.INICIADO,
            ProjectStatus.EM_ANDAMENTO,
            ProjectStatus.ENCERRADO
    );

    public void validate(ProjectStatus status) {
        if (FORBIDDEN_STATUSES.contains(status)) {
            throw new BusinessRuleException(String.format("Deletion is blocked for projects with status %s", status));
        }
    }
}
